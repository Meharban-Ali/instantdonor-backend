/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.ai_service.service;

import com.blood_donor.ai_service.dto.request.AiDemandRequest;
import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDemandResponse;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;

public interface IAiMatchingService {
    public AiMatchResponse findBestDonors(AiMatchRequest var1);

    public AiDemandResponse predictDemand(AiDemandRequest var1);

    public boolean isAiServiceHealthy();
}

