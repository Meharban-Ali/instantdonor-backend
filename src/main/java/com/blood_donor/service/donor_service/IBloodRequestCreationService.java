package com.blood_donor.service.donor_service;

import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;

public interface IBloodRequestCreationService {
    public BloodRequestResponse createRequest(CreateBloodRequestDto var1);

    public BloodRequestResponse markFulfilled(Long var1);

    public BloodRequestResponse cancelRequest(Long var1);
}

