/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.cache.annotation.CacheEvict
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.PageRequest
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.domain.Sort
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.admin.service_Impl;

import com.blood_donor.admin.dto.UserSummaryDto;
import com.blood_donor.admin.mapper.AdminUserMapper;
import com.blood_donor.admin.service.IAdminUserService;
import com.blood_donor.common.enums.Role;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.UserRepository;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService
implements IAdminUserService {
    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);
    private final UserRepository userRepo;
    private final AdminUserMapper userMapper;

    @Override
    @Transactional(readOnly=true)
    public Page<UserSummaryDto> getAllUsers(Role role, String search, int page, int size) {
        PageRequest pageable = PageRequest.of((int)page, (int)size, (Sort)Sort.by((String[])new String[]{"createdAt"}).descending());
        Page<User> users = search != null && !search.isBlank() ? this.userRepo.searchUsers(search.trim(), (Pageable)pageable) : (role != null ? this.userRepo.findByRoleOrderByCreatedAtDesc(role, (Pageable)pageable) : this.userRepo.findAllByOrderByCreatedAtDesc((Pageable)pageable));
        return users.map(this.userMapper::toUserSummary);
    }

    @Override
    @Transactional(readOnly=true)
    public UserSummaryDto getUserById(Long userId) {
        User user = this.findUserOrThrow(userId);
        return this.userMapper.toUserSummary(user);
    }

    @Override
    @Transactional
    @CacheEvict(value={"adminDashboard"}, allEntries=true)
    public UserSummaryDto toggleUserStatus(Long userId) {
        User user = this.findUserOrThrow(userId);
        if (user.getRoles() != null && user.getRoles().contains((Object)Role.ADMIN)) {
            throw ApiException.forbidden("Cannot deactivate admin user");
        }
        user.setIsActive(!user.isActive());
        User saved = (User)this.userRepo.save(user);
        log.info("User status toggled: userId={}, active={}", (Object)userId, (Object)saved.isActive());
        return this.userMapper.toUserSummary(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value={"adminDashboard"}, allEntries=true)
    public UserSummaryDto updateUserRole(Long userId, Role newRole) {
        User user = this.findUserOrThrow(userId);
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<Role>());
        }
        user.getRoles().add(newRole);
        User saved = (User)this.userRepo.save(user);
        log.info("User role updated: userId={}, newRole={}, allRoles={}", new Object[]{userId, newRole, saved.getRoles()});
        return this.userMapper.toUserSummary(saved);
    }

    private User findUserOrThrow(Long userId) {
        return (User)this.userRepo.findById(userId).orElseThrow(() -> ApiException.notFound("User not found: " + userId));
    }

    public AdminUserService(UserRepository userRepo, AdminUserMapper userMapper) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
    }
}

