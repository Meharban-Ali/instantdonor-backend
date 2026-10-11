/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 */
package com.blood_donor.service.donor_service;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.model.entity.BloodRequest;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface IBloodRequestQueryService {
    public Page<BloodRequestResponse> getActiveRequests(int var1, int var2);

    public Page<BloodRequestResponse> searchRequests(String var1, BloodGroup var2, int var3, int var4);

    public BloodRequestResponse getRequestById(Long var1);

    public Page<BloodRequestResponse> getMyRequests(int var1, int var2);

    public Optional<BloodRequest> findActiveById(Long var1);

    public Map<String, Object> getNotificationStatus(Long var1);
}

