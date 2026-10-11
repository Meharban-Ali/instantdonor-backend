/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.notification.service;

import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.blood_donor.model.entity.BloodRequest;
import java.util.List;

public interface DonorMatchingStrategy {
    public List<AiDonorResult> findDonors(BloodRequest var1, int var2);
}

