/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 */
package com.blood_donor.admin.service;

import com.blood_donor.admin.dto.UserSummaryDto;
import com.blood_donor.common.enums.Role;
import org.springframework.data.domain.Page;

public interface IAdminUserService {
    public Page<UserSummaryDto> getAllUsers(Role var1, String var2, int var3, int var4);

    public UserSummaryDto toggleUserStatus(Long var1);

    public UserSummaryDto updateUserRole(Long var1, Role var2);

    public UserSummaryDto getUserById(Long var1);
}

