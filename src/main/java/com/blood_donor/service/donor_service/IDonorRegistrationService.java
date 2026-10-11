/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.service.donor_service;

import com.blood_donor.model.dto.request.donor_request.DonorRegisterRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;

public interface IDonorRegistrationService {
    public DonorResponse registerDonor(Long var1, DonorRegisterRequest var2);
}

