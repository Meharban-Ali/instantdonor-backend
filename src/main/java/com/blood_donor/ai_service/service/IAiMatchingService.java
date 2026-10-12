package com.blood_donor.ai_service.service;

import com.blood_donor.ai_service.dto.request.AiDemandRequest;
import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDemandResponse;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;

public interface IAiMatchingService {
    public AiMatchResponse findBestDonors(AiMatchRequest request);

    public AiDemandResponse predictDemand(AiDemandRequest request);

    public boolean isAiServiceHealthy();
}

