/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 */
package com.blood_donor.repository.donor_repository.request_repository;

import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.repository.donor_repository.projection.BloodDemandStats;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BloodRequestStatsRepository {
    public long countByStatus(BloodRequestStatus var1);

    @Query(value="SELECT\n    br.city       AS city,\n    br.bloodGroup AS bloodGroup,\n    COUNT(br)     AS count\nFROM BloodRequest br\nWHERE br.createdAt >= :since\nGROUP BY br.city, br.bloodGroup\nORDER BY COUNT(br) DESC\n")
    public List<BloodDemandStats> getBloodDemandStats(@Param(value="since") LocalDateTime var1);

    @Query(value="SELECT\n    COUNT(CASE WHEN br.status = 'FULFILLED' THEN 1 END) * 100.0\n    / COUNT(br)\nFROM BloodRequest br\nWHERE br.createdAt >= :since\n")
    public Double calculateFulfillmentRate(@Param(value="since") LocalDateTime var1);

    @Query(value="SELECT COUNT(br) FROM BloodRequest br\nWHERE br.status = 'PENDING'\n  AND br.urgency = 'CRITICAL'\n")
    public long countCriticalPendingRequests();

    @Query(value="SELECT AVG(TIMESTAMPDIFF(HOUR, br.createdAt, br.fulfilledAt))\nFROM BloodRequest br\nWHERE br.status = 'FULFILLED'\n  AND br.createdAt >= :since\n")
    public Double calculateAvgResponseTimeHours(@Param(value="since") LocalDateTime var1);
}

