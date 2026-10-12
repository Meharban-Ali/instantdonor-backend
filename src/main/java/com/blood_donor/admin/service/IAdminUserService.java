package com.blood_donor.admin.service;

import com.blood_donor.admin.dto.UserSummaryDto;
import com.blood_donor.common.enums.Role;
import org.springframework.data.domain.Page;

public interface IAdminUserService {
    public Page<UserSummaryDto> getAllUsers(Role role, String search, int page, int size);

    public UserSummaryDto toggleUserStatus(Long userId);

    public UserSummaryDto updateUserRole(Long userId, Role role);

    public UserSummaryDto getUserById(Long userId);
}

