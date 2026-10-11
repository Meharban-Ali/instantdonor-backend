/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 */
package com.blood_donor.repository.donor_repository.request_repository;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.entity.BloodRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BloodRequestQueryRepository {
    public Page<BloodRequest> findByStatusOrderByCreatedAtDesc(BloodRequestStatus var1, Pageable var2);

    public Page<BloodRequest> findByStatusAndCityIgnoreCaseAndBloodGroupOrderByCreatedAtDesc(BloodRequestStatus var1, String var2, BloodGroup var3, Pageable var4);

    public Page<BloodRequest> findByStatusAndUrgencyOrderByCreatedAtDesc(BloodRequestStatus var1, UrgencyLevel var2, Pageable var3);

    public Page<BloodRequest> findByCreatedByIdOrderByCreatedAtDesc(Long var1, Pageable var2);

    @Query(value="SELECT br FROM BloodRequest br\nWHERE br.status = :status\n  AND (:city IS NULL OR LOWER(br.city) = LOWER(:city))\n  AND (:bloodGroup IS NULL OR br.bloodGroup = :bloodGroup)\n  AND (:urgency IS NULL OR br.urgency = :urgency)\nORDER BY\n    CASE br.urgency\n        WHEN 'CRITICAL' THEN 1\n        WHEN 'URGENT'   THEN 2\n        ELSE 3\n    END,\n    br.createdAt DESC\n")
    public Page<BloodRequest> findWithFilters(@Param(value="status") BloodRequestStatus var1, @Param(value="city") String var2, @Param(value="bloodGroup") BloodGroup var3, @Param(value="urgency") UrgencyLevel var4, Pageable var5);
}

