/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.service.donor_service;

import com.blood_donor.model.dto.request.donor_request.RespondToRequestDto;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;

public interface IBloodRequestResponseService {
    public BloodRequestResponse respondToRequest(RespondToRequestDto var1);
}

