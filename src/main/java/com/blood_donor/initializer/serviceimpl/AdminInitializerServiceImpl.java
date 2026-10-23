package com.blood_donor.initializer.serviceimpl;

import com.blood_donor.common.enums.Role;
import com.blood_donor.config.properties.AdminProperties;
import com.blood_donor.initializer.service.IAdminInitializerService;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AdminInitializerServiceImpl
implements IAdminInitializerService {
    private static final Logger log = LoggerFactory.getLogger(AdminInitializerServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;
    private final Environment environment;

    @Override
    @Transactional
    public void initializeAdmin() {
        String adminEmail = StringUtils.hasText(this.adminProperties.getEmail())
                ? this.adminProperties.getEmail().trim().toLowerCase(java.util.Locale.ROOT)
                : System.getenv("ADMIN_EMAIL");

        if (StringUtils.hasText(adminEmail)) {
            adminEmail = adminEmail.trim().toLowerCase(java.util.Locale.ROOT);
        }

        if (!StringUtils.hasText(adminEmail)) {
            log.warn("⚠️ ADMIN_EMAIL not configured. Skipping default admin initialization.");
            return;
        }

        if (this.userRepository.existsByEmailIgnoreCase(adminEmail)) {
            log.info("ℹ️  Admin already exists: {}", adminEmail);
            return;
        }

        String password = this.adminProperties.getPassword();
        if (!StringUtils.hasText(password)) {
            password = System.getenv("ADMIN_PASSWORD");
        }

        String encodedPassword;
        if (StringUtils.hasText(password)) {
            encodedPassword = this.passwordEncoder.encode(password);
            log.info("ℹ️ Initializing admin with configured ADMIN_PASSWORD for email: {}", adminEmail);
        } else {
            // ADMIN_PASSWORD blank: check production profile
            boolean isProd = false;
            for (String profile : this.environment.getActiveProfiles()) {
                if ("prod".equalsIgnoreCase(profile) || "production".equalsIgnoreCase(profile)) {
                    isProd = true;
                    break;
                }
            }

            if (isProd) {
                String mailHost = this.environment.getProperty("spring.mail.host");
                if (!StringUtils.hasText(mailHost)) mailHost = System.getenv("MAIL_HOST");

                String mailUser = this.environment.getProperty("spring.mail.username");
                if (!StringUtils.hasText(mailUser)) mailUser = System.getenv("MAIL_USERNAME");

                String mailPass = this.environment.getProperty("spring.mail.password");
                if (!StringUtils.hasText(mailPass)) mailPass = System.getenv("MAIL_PASSWORD");

                if (!StringUtils.hasText(mailHost) || !StringUtils.hasText(mailUser) || !StringUtils.hasText(mailPass)) {
                    String errorMsg = "Production admin bootstrap requires MAIL_HOST, MAIL_USERNAME, and MAIL_PASSWORD when ADMIN_PASSWORD is blank so admin password can be established via forgot-password flow.";
                    log.error("❌ {}", errorMsg);
                    throw new IllegalStateException(errorMsg);
                }
            }

            // Seed with random unusable password (<= 72 bytes for BCrypt) - never logged
            String unusableSecret = UUID.randomUUID().toString() + "_UNUSABLE";
            encodedPassword = this.passwordEncoder.encode(unusableSecret);
            log.info("ℹ️ ADMIN_PASSWORD blank. Admin seeded with random unusable password for {}. Use forgot-password to establish credentials.", adminEmail);
        }

        String name = StringUtils.hasText(this.adminProperties.getName()) ? this.adminProperties.getName() : "Super Admin";
        String phone = StringUtils.hasText(this.adminProperties.getPhone()) ? this.adminProperties.getPhone() : "9999999999";

        User admin = User.builder()
                .name(name)
                .email(adminEmail)
                .password(encodedPassword)
                .phone(phone)
                .roles(new HashSet<Role>(Set.of(Role.USER, Role.ADMIN)))
                .isActive(true)
                .build();
        this.userRepository.save(admin);
        log.info("✅ Default admin created: {}", adminEmail);
        log.info("✅ Admin roles: {}", admin.getRoles());
    }

    public AdminInitializerServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminProperties adminProperties, Environment environment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
        this.environment = environment;
    }
}

