package com.blood_donor.service.donor_service;

import com.blood_donor.model.dto.request.donor_request.DonorUpdateRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.dto.response.donor_response.EligibilityResponse;

public interface IDonorProfileService {
    public DonorResponse getMyProfile(Long var1);

    public DonorResponse updateProfile(Long var1, DonorUpdateRequest var2);

    public String toggleAvailability(Long var1);

    public EligibilityResponse checkEligibility(Long var1);
}

