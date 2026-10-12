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
    @Value(value="${app.reset-token.expiry-minutes:30}")
    private int resetTokenExpiryMinutes;
    @Value(value="${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    @Value(value="${rate-limiting.login.email-capacity:5}")
    private long loginEmailCapacity;
    @Value(value="${rate-limiting.login.refill-interval:60}")
    private long loginEmailRefillInterval;
    private final java.util.concurrent.ConcurrentHashMap<String, EmailTokenBucket> emailBuckets = new java.util.concurrent.ConcurrentHashMap<>();

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (this.userRepository.existsByEmail(request.getEmail())) {
            throw ApiException.conflict("Email already registered");
        }
        if (request.getPhone() != null && this.userRepository.existsByPhone(request.getPhone())) {
            throw ApiException.conflict("Phone already registered");
        }
        User user = User.builder().name(request.getName()).email(request.getEmail()).password(this.passwordEncoder.encode((CharSequence)request.getPassword())).phone(request.getPhone()).roles(new HashSet<Role>(Set.of(Role.USER))).build();
        User savedUser = (User)this.userRepository.save(user);
        log.info("New user registered: {} with roles {}", (Object)savedUser.getEmail(), savedUser.getRoles());
        return this.buildAuthResponse(savedUser);
    }

    @Transactional(readOnly=true)
    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            if (!this.checkEmailRateLimit(normalizedEmail)) {
                log.warn("Rate limit exceeded for login email: {}", normalizedEmail);
                throw ApiException.tooManyRequests("Too many login attempts for this account. Please try again later.");
            }
        }
        this.authenticationManager.authenticate((Authentication)new UsernamePasswordAuthenticationToken((Object)request.getEmail(), (Object)request.getPassword()));
        User user = this.userRepository.findByEmail(request.getEmail()).orElseThrow(() -> ApiException.notFound("User not found"));
        if (!user.getIsActive().booleanValue()) {
            throw ApiException.forbidden("Account is deactivated");
        }
        log.info("User logged in: {}", (Object)user.getEmail());
        return this.buildAuthResponse(user);
    }

    private boolean checkEmailRateLimit(String email) {
        String key = "rate:limit:login:email:" + email;
        if (this.redisTemplate != null) {
            try {
                return this.tryConsumeEmailRedis(key, this.loginEmailCapacity);
            } catch (Exception e) {
                log.warn("Redis email rate limiting failed, falling back to memory: {}", e.getMessage());
                return this.tryConsumeEmailLocal(key, this.loginEmailCapacity);
            }
        }
        return this.tryConsumeEmailLocal(key, this.loginEmailCapacity);
    }

    private boolean tryConsumeEmailLocal(String key, long capacity) {
        double refillRatePerMs = (double) capacity / (this.loginEmailRefillInterval * 1000.0);
        EmailTokenBucket bucket = this.emailBuckets.computeIfAbsent(key, k -> new EmailTokenBucket(capacity));
        return bucket.tryConsume(refillRatePerMs);
    }

    private synchronized boolean tryConsumeEmailRedis(String key, long capacity) {
        long now = System.currentTimeMillis();
        double refillRatePerMs = (double) capacity / (this.loginEmailRefillInterval * 1000.0);
        java.util.Map<Object, Object> data = this.redisTemplate.opsForHash().entries(key);
        double tokens;
        long lastRefillTime;
        if (data == null || data.isEmpty()) {
            tokens = capacity;
            lastRefillTime = now;
        } else {
            Object tokensObj = data.get("tokens");
            Object lastRefillObj = data.get("lastRefillTime");
            if (tokensObj == null || lastRefillObj == null) {
                tokens = capacity;
                lastRefillTime = now;
            } else {
                try {
                    tokens = Double.parseDouble(tokensObj.toString());
                    lastRefillTime = Long.parseLong(lastRefillObj.toString());
                } catch (NumberFormatException e) {
                    tokens = capacity;
                    lastRefillTime = now;
                }
            }
        }
        long timePassed = now - lastRefillTime;
        if (timePassed > 0) {
            tokens = Math.min((double)capacity, tokens + (double)timePassed * refillRatePerMs);
            lastRefillTime = now;
        }
        if (tokens >= 1.0) {
            tokens -= 1.0;
            java.util.Map<String, Object> update = new java.util.HashMap<>();
            update.put("tokens", tokens);
            update.put("lastRefillTime", lastRefillTime);
            this.redisTemplate.opsForHash().putAll(key, update);
            this.redisTemplate.expire(key, this.loginEmailRefillInterval, TimeUnit.SECONDS);
            return true;
        }
        java.util.Map<String, Object> update = new java.util.HashMap<>();
        update.put("tokens", tokens);
        update.put("lastRefillTime", lastRefillTime);
        this.redisTemplate.opsForHash().putAll(key, update);
        this.redisTemplate.expire(key, this.loginEmailRefillInterval, TimeUnit.SECONDS);
        return false;
    }

    private static class EmailTokenBucket {
        private final long capacity;
        private double tokens;
        private long lastRefillTime;

        public EmailTokenBucket(long capacity) {
            this.capacity = capacity;
            this.tokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume(double refillRatePerMs) {
            long now = System.currentTimeMillis();
            long timePassed = now - this.lastRefillTime;
            if (timePassed > 0) {
                this.tokens = Math.min((double)this.capacity, this.tokens + (double)timePassed * refillRatePerMs);
                this.lastRefillTime = now;
            }
            if (this.tokens >= 1.0) {
                this.tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    @Transactional
    public void sendResetPasswordEmail(String email) {
        this.userRepository.findByEmail(email).ifPresent(user -> {
            this.tokenRepository.deleteAllByUserId(user.getId());
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder().token(token).user((User)user).expiresAt(LocalDateTime.now().plusMinutes(this.resetTokenExpiryMinutes)).used(false).build();
            this.tokenRepository.save(resetToken);
            this.notificationService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
            log.info("Password reset token generated for user: {}", (Object)email);
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
            User user = this.userRepository.findByEmail(email).orElseThrow(() -> ApiException.notFound("User not found"));
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

