/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.jpa.repository.Modifying
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.repository.donor_repository.request_repository;

import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.entity.BloodRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BloodRequestSchedulerRepository {
    @Query(value="SELECT br FROM BloodRequest br\nWHERE br.status IN :statuses\n  AND br.expiredAt < :now\nORDER BY br.expiredAt ASC\n")
    public List<BloodRequest> findExpiredRequests(@Param(value="statuses") List<BloodRequestStatus> var1, @Param(value="now") LocalDateTime var2);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status = :status,\n    br.fulfilledAt = CASE\n        WHEN :status = 'FULFILLED' THEN :now\n        ELSE br.fulfilledAt\n    END\nWHERE br.id = :id\n")
    public int updateStatus(@Param(value="id") Long var1, @Param(value="status") BloodRequestStatus var2, @Param(value="now") LocalDateTime var3);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status = :expiredStatus\nWHERE br.status IN :activeStatuses\n  AND br.expiredAt < :now\n")
    public int bulkExpireRequests(@Param(value="activeStatuses") List<BloodRequestStatus> var1, @Param(value="expiredStatus") BloodRequestStatus var2, @Param(value="now") LocalDateTime var3);
}

