/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 */
package com.blood_donor.repository.donor_repository.response_repository;

import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.DonorRequestResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonorResponseQueryRepository {
    public Optional<DonorRequestResponse> findByDonorProfileIdAndRequestId(Long var1, Long var2);

    public boolean existsByDonorProfileIdAndRequestId(Long var1, Long var2);

    public List<DonorRequestResponse> findByRequestId(Long var1);

    public List<DonorRequestResponse> findByRequestIdAndStatus(Long var1, ResponseStatus var2);

    @Query(value="SELECT drr FROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorProfileId\nORDER BY drr.notifiedAt DESC\n")
    public List<DonorRequestResponse> findByDonorProfileId(@Param(value="donorProfileId") Long var1);

    @Query(value="SELECT drr FROM DonorRequestResponse drr\nWHERE drr.request.id = :requestId\n  AND drr.status = 'NOTIFIED'\nORDER BY drr.notifiedAt ASC\n")
    public List<DonorRequestResponse> findPendingByRequestId(@Param(value="requestId") Long var1);
}

