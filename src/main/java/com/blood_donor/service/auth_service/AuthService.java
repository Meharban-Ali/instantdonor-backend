package com.blood_donor.service.auth_service;

import com.blood_donor.common.enums.Role;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.response.auth_response.AuthResponse;
import com.blood_donor.model.entity.PasswordResetToken;
import com.blood_donor.model.entity.User;
import com.blood_donor.notification.service.INotificationService;
import com.blood_donor.repository.user_repository.PasswordResetTokenRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository tokenRepository;
    private final INotificationService notificationService;
    @Autowired(required=false)
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired(required=false)
    private com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository bloodRequestRepository;
    @Autowired(required=false)
    private com.blood_donor.monitoring.CustomMetricsService customMetricsService;
    @Value(value="${app.reset-token.expiry-minutes:30}")
    private int resetTokenExpiryMinutes;
    @Value(value="${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    @Value(value="${rate-limiting.login.email-capacity:5}")
    private long loginEmailCapacity;
    @Value(value="${rate-limiting.login.refill-interval:60}")
    private long loginEmailRefillInterval;
    private final java.util.concurrent.ConcurrentHashMap<String, Long> ipEmailLockouts = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, Integer> ipEmailFailedAttempts = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, Integer> globalEmailFailedAttempts = new java.util.concurrent.ConcurrentHashMap<>();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = (request.getEmail() != null) ? request.getEmail().trim().toLowerCase(java.util.Locale.ROOT) : "";
        if (this.userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw ApiException.conflict("Email already registered");
        }
        if (request.getPhone() != null && this.userRepository.existsByPhone(request.getPhone())) {
            throw ApiException.conflict("Phone already registered");
        }
        User user = User.builder().name(request.getName()).email(normalizedEmail).password(this.passwordEncoder.encode((CharSequence)request.getPassword())).phone(request.getPhone()).roles(new HashSet<Role>(Set.of(Role.USER))).build();
        User savedUser = (User)this.userRepository.save(user);
        log.info("New user registered: {} with roles {}", com.blood_donor.utils.PiiMasker.maskEmail(savedUser.getEmail()), savedUser.getRoles());
        return this.buildAuthResponse(savedUser);
    }

    @Transactional(readOnly=true)
    public AuthResponse login(LoginRequest request) {
        return login(request, getCurrentClientIp());
    }

    @Transactional(readOnly=true)
    public AuthResponse login(LoginRequest request, String clientIp) {
        String normalizedEmail = (request.getEmail() != null) ? request.getEmail().trim().toLowerCase(java.util.Locale.ROOT) : "";
        String effectiveIp = (clientIp != null && !clientIp.isBlank()) ? clientIp.trim() : "127.0.0.1";

        if (!normalizedEmail.isEmpty()) {
            if (this.isThrottled(effectiveIp, normalizedEmail)) {
                log.warn("Login blocked: (IP, email) pair temporarily throttled for IP {} and email: {}",
                        effectiveIp, com.blood_donor.utils.PiiMasker.maskEmail(normalizedEmail));
                if (this.customMetricsService != null) {
                    this.customMetricsService.recordRateLimit429();
                }
                throw ApiException.tooManyRequests("Account temporarily locked due to repeated failed logins. Please try again after 15 minutes or reset your password.");
            }
        }

        try {
            this.authenticationManager.authenticate((Authentication)new UsernamePasswordAuthenticationToken((Object)normalizedEmail, (Object)request.getPassword()));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            if (this.customMetricsService != null) {
                this.customMetricsService.recordAuthFailure();
            }
            if (!normalizedEmail.isEmpty()) {
                this.recordFailedLogin(effectiveIp, normalizedEmail);
            }
            throw e;
        }

        if (!normalizedEmail.isEmpty()) {
            this.clearFailedLogins(effectiveIp, normalizedEmail);
        }

        User user = this.userRepository.findByEmailIgnoreCase(normalizedEmail).orElseThrow(() -> ApiException.notFound("User not found"));
        if (!user.getIsActive().booleanValue()) {
            throw ApiException.forbidden("Account is deactivated");
        }
        log.info("User logged in: {}", com.blood_donor.utils.PiiMasker.maskEmail(user.getEmail()));
        return this.buildAuthResponse(user);
    }

    private boolean isThrottled(String ip, String email) {
        String compositeKey = ip + ":" + email;
        String lockKey = "lockout:ip_email:" + compositeKey;
        if (this.redisTemplate != null) {
            try {
                return Boolean.TRUE.equals(this.redisTemplate.hasKey(lockKey));
            } catch (Exception e) {
                log.warn("Redis lockout check failed, checking memory: {}", e.getMessage());
            }
        }
        Long lockedUntil = this.ipEmailLockouts.get(compositeKey);
        if (lockedUntil != null) {
            if (System.currentTimeMillis() < lockedUntil) {
                return true;
            } else {
                this.ipEmailLockouts.remove(compositeKey);
            }
        }
        return false;
    }

    private void recordFailedLogin(String ip, String email) {
        String compositeKey = ip + ":" + email;
        String failedKey = "lockout:failed:ip_email:" + compositeKey;
        String lockKey = "lockout:ip_email:" + compositeKey;
        String globalEmailFailKey = "auth:failed:email:" + email;
        int count = 1;
        if (this.redisTemplate != null) {
            try {
                Long incremented = this.redisTemplate.opsForValue().increment(failedKey);
                count = (incremented != null) ? incremented.intValue() : 1;
                this.redisTemplate.expire(failedKey, 15, TimeUnit.MINUTES);
                if (count >= 5) {
                    this.redisTemplate.opsForValue().set(lockKey, "locked", 15, TimeUnit.MINUTES);
                    log.warn("(IP, email) pair temporarily throttled (5 failed attempts) for IP {} and email: {}",
                            ip, com.blood_donor.utils.PiiMasker.maskEmail(email));
                }

                // Distributed brute-force detection across multiple IPs: log alert + metric, but NO hard lockout
                Long globalCount = this.redisTemplate.opsForValue().increment(globalEmailFailKey);
                this.redisTemplate.expire(globalEmailFailKey, 15, TimeUnit.MINUTES);
                if (globalCount != null && globalCount >= 10) {
                    log.warn("SECURITY ALERT: Potential distributed brute-force attack detected on email {} across multiple IPs (global failures: {}). No hard lockout enforced on legitimate user.",
                            com.blood_donor.utils.PiiMasker.maskEmail(email), globalCount);
                    if (this.customMetricsService != null) {
                        this.customMetricsService.recordDistributedAttackAlert();
                    }
                }
                return;
            } catch (Exception e) {
                log.warn("Redis recordFailedLogin failed, using memory: {}", e.getMessage());
            }
        }
        count = this.ipEmailFailedAttempts.merge(compositeKey, 1, Integer::sum);
        if (count >= 5) {
            this.ipEmailLockouts.put(compositeKey, System.currentTimeMillis() + (15 * 60 * 1000L));
            log.warn("(IP, email) pair temporarily throttled in-memory (5 failed attempts) for IP {} and email: {}",
                    ip, com.blood_donor.utils.PiiMasker.maskEmail(email));
        }

        int globalCountMem = this.globalEmailFailedAttempts.merge(email, 1, Integer::sum);
        if (globalCountMem >= 10) {
            log.warn("SECURITY ALERT: Potential distributed brute-force attack detected on email {} across multiple IPs (global in-memory failures: {}). No hard lockout enforced on legitimate user.",
                    com.blood_donor.utils.PiiMasker.maskEmail(email), globalCountMem);
            if (this.customMetricsService != null) {
                this.customMetricsService.recordDistributedAttackAlert();
            }
        }
    }

    private void clearFailedLogins(String ip, String email) {
        String compositeKey = ip + ":" + email;
        String failedKey = "lockout:failed:ip_email:" + compositeKey;
        String lockKey = "lockout:ip_email:" + compositeKey;
        if (this.redisTemplate != null) {
            try {
                this.redisTemplate.delete(java.util.List.of(failedKey, lockKey));
            } catch (Exception e) {
                log.warn("Redis clearFailedLogins failed: {}", e.getMessage());
            }
        }
        this.ipEmailFailedAttempts.remove(compositeKey);
        this.ipEmailLockouts.remove(compositeKey);
    }

    private String getCurrentClientIp() {
        try {
            org.springframework.web.context.request.RequestAttributes attribs =
                    org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (attribs instanceof org.springframework.web.context.request.ServletRequestAttributes) {
                HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes) attribs).getRequest();
                if (request != null && request.getRemoteAddr() != null) {
                    return request.getRemoteAddr();
                }
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }

    @Transactional
    public void sendResetPasswordEmail(String email) {
        String normalizedEmail = (email != null) ? email.trim().toLowerCase(java.util.Locale.ROOT) : "";
        this.userRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(user -> {
            this.tokenRepository.deleteAllByUserId(user.getId());
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder().token(token).user((User)user).expiresAt(LocalDateTime.now().plusMinutes(this.resetTokenExpiryMinutes)).used(false).build();
            this.tokenRepository.save(resetToken);
            this.notificationService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
            log.info("Password reset token generated for user: {}", (Object)normalizedEmail);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = this.tokenRepository.findByToken(token).orElseThrow(() -> new ApiException("Invalid or expired reset link. Please request a new one.", HttpStatus.BAD_REQUEST));
        if (!resetToken.isValid()) {
            throw new ApiException(resetToken.isExpired() ? "Reset link has expired. Please request a new one." : "Reset link has already been used.", HttpStatus.BAD_REQUEST);
        }
        User user = resetToken.getUser();
        user.setPassword(this.passwordEncoder.encode((CharSequence)newPassword));
        this.userRepository.save(user);
        resetToken.setUsed(true);
        this.tokenRepository.save(resetToken);
        this.tokenRepository.deleteAllByUserId(user.getId());
        log.info("Password reset successful for user: {}", (Object)user.getEmail());
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = (User)this.userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (!this.passwordEncoder.matches((CharSequence)currentPassword, user.getPassword())) {
            throw new ApiException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(this.passwordEncoder.encode((CharSequence)newPassword));
        this.userRepository.save(user);
        log.info("Password changed: userId={}", (Object)userId);
    }

    @Transactional(readOnly=true)
    public AuthResponse refreshToken(String refreshToken) {
        try {
            if (this.redisTemplate != null && Boolean.TRUE.equals(this.redisTemplate.hasKey("blacklist:token:" + refreshToken))) {
                throw ApiException.unauthorized("Refresh token has been revoked");
            }
            String email = this.jwtTokenProvider.extractEmail(refreshToken);
            if (email == null || !this.jwtTokenProvider.validateToken(refreshToken, email)) {
                throw ApiException.unauthorized("Invalid or expired refresh token");
            }
            User user = this.userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> ApiException.notFound("User not found"));
            if (!user.getIsActive().booleanValue()) {
                throw ApiException.forbidden("Account is deactivated");
            }
            AuthResponse authResponse = this.buildAuthResponse(user);
            if (this.redisTemplate != null) {
                try {
                    Date expirationDate = this.jwtTokenProvider.extractClaim(refreshToken, Claims::getExpiration);
                    long remainingMs = expirationDate.getTime() - System.currentTimeMillis();
                    if (remainingMs > 0L && this.redisTemplate.opsForValue() != null) {
                        this.redisTemplate.opsForValue().set("blacklist:token:" + refreshToken, "true", remainingMs, TimeUnit.MILLISECONDS);
                        log.info("Old refresh token rotated and blacklisted successfully");
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to blacklist old refresh token: {}", (Object)e.getMessage());
                }
            }
            return authResponse;
        }
        catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("Refresh token validation failed: {}", (Object)e.getMessage());
            throw ApiException.unauthorized("Invalid or expired refresh token");
        }
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        long remainingMs;
        Date expirationDate;
        log.info("Logout called. accessToken present: {}, refreshToken present: {}, redisTemplate present: {}", new Object[]{accessToken != null, refreshToken != null, this.redisTemplate != null});
        if (accessToken != null && this.redisTemplate != null) {
            try {
                expirationDate = this.jwtTokenProvider.extractClaim(accessToken, Claims::getExpiration);
                remainingMs = expirationDate.getTime() - System.currentTimeMillis();
                if (remainingMs > 0L) {
                    this.redisTemplate.opsForValue().set("blacklist:token:" + accessToken, "true", remainingMs, TimeUnit.MILLISECONDS);
                    log.info("Access token blacklisted successfully on logout");
                }
            }
            catch (Exception e) {
                log.warn("Failed to blacklist access token: {}", (Object)e.getMessage());
            }
        }
        if (refreshToken != null && this.redisTemplate != null) {
            try {
                expirationDate = this.jwtTokenProvider.extractClaim(refreshToken, Claims::getExpiration);
                remainingMs = expirationDate.getTime() - System.currentTimeMillis();
                if (remainingMs > 0L) {
                    this.redisTemplate.opsForValue().set("blacklist:token:" + refreshToken, "true", remainingMs, TimeUnit.MILLISECONDS);
                    log.info("Refresh token blacklisted successfully on logout");
                }
            }
            catch (Exception e) {
                log.warn("Failed to blacklist refresh token: {}", (Object)e.getMessage());
            }
        }
    }

    @Transactional
    public void deleteAccount(Long userId, String accessToken, String password) {
        User user = (User)this.userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (password == null || password.isBlank() || !this.passwordEncoder.matches((CharSequence)password, user.getPassword())) {
            log.warn("Account deletion rejected: Invalid password for userId={}", userId);
            throw ApiException.unauthorized("Invalid current password for account deletion");
        }

        if (user.hasRole(Role.ADMIN)) {
            long activeAdminCount = this.userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
            if (activeAdminCount <= 1) {
                throw ApiException.forbidden("Cannot delete or deactivate the last remaining active administrator account");
            }
        }

        log.info("DPDP account erasure initiated for userId: {}", userId);

        user.setName("Deleted User " + userId);
        user.setEmail("deleted_" + userId + "@anonymized.local");
        user.setPhone("+0000000000" + (userId % 1000));
        user.setPassword(this.passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setIsActive(false);

        if (user.getDonorProfile() != null) {
            user.getDonorProfile().setAvailable(false);
            user.getDonorProfile().setCity("Anonymized");
            user.getDonorProfile().setState("Anonymized");
        }

        if (this.bloodRequestRepository != null) {
            java.util.List<com.blood_donor.model.dto.request.blood_request.BloodRequestStatus> activeStatuses =
                    java.util.List.of(com.blood_donor.model.dto.request.blood_request.BloodRequestStatus.PENDING,
                            com.blood_donor.model.dto.request.blood_request.BloodRequestStatus.ACTIVE,
                            com.blood_donor.model.dto.request.blood_request.BloodRequestStatus.MATCHING);
            java.util.List<com.blood_donor.model.entity.BloodRequest> activeRequests =
                    this.bloodRequestRepository.findByCreatedByIdAndStatusIn(userId, activeStatuses);
            for (com.blood_donor.model.entity.BloodRequest req : activeRequests) {
                req.setStatus(com.blood_donor.model.dto.request.blood_request.BloodRequestStatus.CANCELLED);
                this.bloodRequestRepository.save(req);
            }
        }

        if (accessToken != null) {
            this.logout(accessToken, null);
        }

        this.userRepository.save(user);
        log.info("DPDP account erasure completed for userId: {}", userId);
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = this.jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(user.getEmail());
        return AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).tokenType("Bearer").userId(user.getId()).name(user.getName()).email(user.getEmail()).roles(user.getRoles()).expiresIn(3600L).build();
    }

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager, PasswordResetTokenRepository tokenRepository, INotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
        this.notificationService = notificationService;
    }

    public UserRepository getUserRepository() {
        return this.userRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    public JwtTokenProvider getJwtTokenProvider() {
        return this.jwtTokenProvider;
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public PasswordResetTokenRepository getTokenRepository() {
        return this.tokenRepository;
    }

    public INotificationService getNotificationService() {
        return this.notificationService;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

    public int getResetTokenExpiryMinutes() {
        return this.resetTokenExpiryMinutes;
    }

    public String getFrontendUrl() {
        return this.frontendUrl;
    }
}

