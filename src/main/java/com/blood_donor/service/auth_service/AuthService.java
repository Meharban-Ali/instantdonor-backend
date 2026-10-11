/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.jsonwebtoken.Claims
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.http.HttpStatus
 *  org.springframework.security.authentication.AuthenticationManager
 *  org.springframework.security.authentication.UsernamePasswordAuthenticationToken
 *  org.springframework.security.core.Authentication
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
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
        this.authenticationManager.authenticate((Authentication)new UsernamePasswordAuthenticationToken((Object)request.getEmail(), (Object)request.getPassword()));
        User user = this.userRepository.findByEmail(request.getEmail()).orElseThrow(() -> ApiException.notFound("User not found"));
        if (!user.getIsActive().booleanValue()) {
            throw ApiException.forbidden("Account is deactivated");
        }
        log.info("User logged in: {}", (Object)user.getEmail());
        return this.buildAuthResponse(user);
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
            return this.buildAuthResponse(user);
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

