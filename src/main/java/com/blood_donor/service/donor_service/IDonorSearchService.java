/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 */
package com.blood_donor.service.donor_service;

import com.blood_donor.model.dto.request.donor_request.DonorSearchRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface IDonorSearchService {
    public Page<DonorResponse> searchByCity(DonorSearchRequest var1);

    public List<DonorResponse> searchByLocation(DonorSearchRequest var1);
}

