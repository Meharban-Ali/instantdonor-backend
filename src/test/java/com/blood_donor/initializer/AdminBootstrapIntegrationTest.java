package com.blood_donor.initializer;

import com.blood_donor.common.enums.Role;
import com.blood_donor.config.properties.AdminProperties;
import com.blood_donor.initializer.serviceimpl.AdminInitializerServiceImpl;
import com.blood_donor.model.entity.PasswordResetToken;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.PasswordResetTokenRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.service.auth_service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Admin Bootstrap & Forgot Password Flow Test")
class AdminBootstrapIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthService authService;
    @Autowired private Environment environment;

    @MockitoBean private com.blood_donor.notification.service_Impl.NotificationService notificationService;
    @MockitoBean private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("Admin with blank password creates unusable hash; forgot-password reset allows login")
    void testAdminBootstrapBlankPasswordAndResetFlow() {
        String adminEmail = "bootstrap_admin_" + System.currentTimeMillis() + "@bloodnetwork.org";
        AdminProperties props = new AdminProperties();
        props.setEmail(adminEmail);
        props.setName("Bootstrap Admin");
        props.setPassword(""); // blank password

        AdminInitializerServiceImpl service = new AdminInitializerServiceImpl(
                userRepository, passwordEncoder, props, environment
        );

        // 1. Initialize admin
        service.initializeAdmin();

        // 2. Verify admin exists in DB with ROLE_ADMIN and active
        User admin = userRepository.findByEmailIgnoreCase(adminEmail).orElseThrow();
        assertEquals("Bootstrap Admin", admin.getName());
        assertTrue(admin.getRoles().contains(Role.ADMIN));
        assertTrue(admin.getRoles().contains(Role.USER));
        assertTrue(admin.getIsActive());

        // Verify that common/blank passwords do NOT match the unusable hash
        assertFalse(passwordEncoder.matches("", admin.getPassword()));
        assertFalse(passwordEncoder.matches("admin", admin.getPassword()));
        assertFalse(passwordEncoder.matches("password", admin.getPassword()));
        assertFalse(passwordEncoder.matches("Password@123", admin.getPassword()));

        // 3. Second run skips already existing admin
        service.initializeAdmin();
        assertEquals(admin.getPassword(), userRepository.findByEmailIgnoreCase(adminEmail).orElseThrow().getPassword());

        // 4. Request password reset via AuthService
        authService.sendResetPasswordEmail(adminEmail);

        PasswordResetToken resetToken = tokenRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(admin.getId()) && t.isValid())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Reset token should be saved in DB"));

        assertNotNull(resetToken.getToken());

        // 5. Reset password with new password
        String newPassword = "NewAdminPassword@2026!";
        authService.resetPassword(resetToken.getToken(), newPassword);

        // 6. Verify admin can now authenticate with new password
        User updatedAdmin = userRepository.findByEmailIgnoreCase(adminEmail).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedAdmin.getPassword()));
    }

    @Test
    @DisplayName("Production profile fails startup if mail config is missing when password is blank")
    void testProdProfileFailsStartupWithoutMailConfig() {
        String adminEmail = "prod_admin_" + System.currentTimeMillis() + "@bloodnetwork.org";
        AdminProperties props = new AdminProperties();
        props.setEmail(adminEmail);
        props.setPassword("");

        MockEnvironment prodEnv = new MockEnvironment();
        prodEnv.setActiveProfiles("prod");
        // No mail configs set

        AdminInitializerServiceImpl prodService = new AdminInitializerServiceImpl(
                userRepository, passwordEncoder, props, prodEnv
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class, prodService::initializeAdmin);
        assertTrue(ex.getMessage().contains("MAIL_HOST"));
    }
}
