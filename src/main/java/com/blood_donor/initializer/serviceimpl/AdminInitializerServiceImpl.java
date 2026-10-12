/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.util.StringUtils
 */
package com.blood_donor.initializer.serviceimpl;

import com.blood_donor.common.enums.Role;
import com.blood_donor.config.properties.AdminProperties;
import com.blood_donor.initializer.service.IAdminInitializerService;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    @Transactional
    public void initializeAdmin() {
        String password = this.adminProperties.getPassword();
        if (!StringUtils.hasText((String)password)) {
            password = System.getenv("ADMIN_PASSWORD");
        }
        if (!StringUtils.hasText((String)password)) {
            log.warn("\u26a0\ufe0f ADMIN_PASSWORD not configured. Skipping default admin initialization for security. Set ADMIN_PASSWORD environment variable to initialize admin account.");
            return;
        }
        if (this.userRepository.existsByEmail(this.adminProperties.getEmail())) {
            log.info("\u2139\ufe0f  Admin already exists: {}", (Object)this.adminProperties.getEmail());
            return;
        }
        User admin = User.builder().name(this.adminProperties.getName()).email(this.adminProperties.getEmail()).password(this.passwordEncoder.encode((CharSequence)password)).phone(this.adminProperties.getPhone()).roles(new HashSet<Role>(Set.of(Role.USER, Role.ADMIN))).build();
        this.userRepository.save(admin);
        log.info("\u2705 Default admin created: {}", (Object)this.adminProperties.getEmail());
        log.info("\u2705 Admin roles: {}", admin.getRoles());
    }

    public AdminInitializerServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }
}

