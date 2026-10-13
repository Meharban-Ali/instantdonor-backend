package com.blood_donor.controller.auth_controller;

import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.forgetpassword.ChangePasswordRequest;
import com.blood_donor.model.dto.forgetpassword.ForgotPasswordRequest;
import com.blood_donor.model.dto.forgetpassword.ResetPasswordRequest;
import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RefreshTokenRequest;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.request.update_profile.UpdateProfileRequest;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import com.blood_donor.model.dto.response.auth_response.AuthResponse;
import com.blood_donor.model.dto.response.user_profile.UserProfileResponse;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.security.JwtTokenProvider;
import com.blood_donor.service.auth_service.AuditLogService;
import com.blood_donor.service.auth_service.AuthService;
import com.blood_donor.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/auth"})
@Tag(name="Authentication", description="Register and Login APIs")
public class AuthController {
    private final AuthService authService;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;
    private final JwtTokenProvider jwtTokenProvider;

    private String getClientIp() {
        return this.httpServletRequest.getRemoteAddr();
    }

    @PostMapping(value={"/register"})
    @Operation(summary="Register new user", description="Create account with USER/DONOR/HOSPITAL role")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = this.authService.register(request);
        this.auditLogService.log("USER_REGISTER", "Registered user: " + request.getEmail(), request.getEmail(), this.getClientIp());
        return ResponseEntity.status((HttpStatusCode)HttpStatus.CREATED).body(ApiResponse.success("Registration successful", response));
    }

    @PostMapping(value={"/login"})
    @Operation(summary="Login", description="Authenticate and receive JWT access + refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        String clientIp = this.getClientIp();
        AuthResponse response = this.authService.login(request, clientIp);
        this.auditLogService.log("USER_LOGIN", "Logged in successfully", request.getEmail(), clientIp);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping(value={"/refresh"})
    @Operation(summary="Refresh access token", description="Use refresh token to get new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = this.authService.refreshToken(request.getRefreshToken());
        this.auditLogService.log("TOKEN_REFRESH", "Refreshed token", response.getEmail(), this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping(value={"/logout"})
    @Operation(summary="Logout user", description="Logout current user and blacklist access and refresh tokens")
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody(required=false) RefreshTokenRequest request, @RequestHeader(value="Authorization", required=false) String authHeader) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        String refreshToken = request != null ? request.getRefreshToken() : null;
        this.authService.logout(accessToken, refreshToken);
        String email = "anonymous";
        if (accessToken != null) {
            try {
                email = this.jwtTokenProvider.extractEmail(accessToken);
            }
            catch (Exception exception) {
                            }
        }
        this.auditLogService.log("USER_LOGOUT", "User logged out", email, this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", "LOGGED_OUT"));
    }

    @PostMapping(value={"/forgot-password"})
    @Operation(summary="Request password reset email")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        this.authService.sendResetPasswordEmail(request.getEmail());
        this.auditLogService.log("FORGOT_PASSWORD", "Requested password reset", request.getEmail(), this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent if account exists", "EMAIL_SENT"));
    }

    @PostMapping(value={"/reset-password"})
    @Operation(summary="Reset password using token")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        this.authService.resetPassword(request.getToken(), request.getNewPassword());
        this.auditLogService.log("RESET_PASSWORD", "Reset password using token", "anonymous", this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", "PASSWORD_RESET"));
    }

    @GetMapping(value={"/me"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Get current user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        Long userId = this.securityUtils.getCurrentUserId();
        User user = (User)this.userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        UserProfileResponse response = UserProfileResponse.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).phone(user.getPhone()).roles(user.getRoles()).isActive(user.getIsActive()).createdAt(user.getCreatedAt()).build();
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", response));
    }

    @PutMapping(value={"/update-profile"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Update user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = this.securityUtils.getCurrentUserId();
        User user = (User)this.userRepository.findById(userId).orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (this.userRepository.existsByPhone(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
                throw ApiException.conflict("Phone number is already associated with another account");
            }
            user.setPhone(request.getPhone());
        }
        this.userRepository.save(user);
        UserProfileResponse response = UserProfileResponse.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).phone(user.getPhone()).roles(user.getRoles()).isActive(user.getIsActive()).createdAt(user.getCreatedAt()).build();
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }

    @org.springframework.web.bind.annotation.DeleteMapping(value={"/me"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Delete and anonymize my account (DPDP Right to Erasure)")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@RequestHeader(value="Authorization", required=false) String authHeader) {
        Long userId = this.securityUtils.getCurrentUserId();
        String accessToken = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        this.authService.deleteAccount(userId, accessToken);
        this.auditLogService.log("USER_ACCOUNT_DELETE", "User requested account erasure: id=" + userId, "userId:" + userId, this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success("Account successfully anonymized and deleted", "ACCOUNT_DELETED"));
    }

    @PutMapping(value={"/change-password"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Change password for logged-in user")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = this.securityUtils.getCurrentUserId();
        this.authService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "CHANGED"));
    }

    public AuthController(AuthService authService, SecurityUtils securityUtils, UserRepository userRepository, AuditLogService auditLogService, HttpServletRequest httpServletRequest, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.httpServletRequest = httpServletRequest;
        this.jwtTokenProvider = jwtTokenProvider;
    }
}

