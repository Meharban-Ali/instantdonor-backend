package com.blood_donor.repository.donor_repository.response_repository;

import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.DonorRequestResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DonorRequestResponseRepository
extends JpaRepository<DonorRequestResponse, Long> {
    @Query(value="SELECT COUNT(drr) > 0\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorId\n  AND drr.request.id = :requestId\n")
    public boolean existsByDonorIdAndRequestId(@Param(value="donorId") Long var1, @Param(value="requestId") Long var2);

    @Query(value="SELECT drr\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorId\n  AND drr.request.id = :requestId\n")
    public Optional<DonorRequestResponse> findByDonorIdAndRequestId(@Param(value="donorId") Long var1, @Param(value="requestId") Long var2);

    @Query(value="SELECT drr\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorProfileId\n  AND drr.request.id = :requestId\n")
    public Optional<DonorRequestResponse> findByDonorProfileIdAndRequestId(@Param(value="donorProfileId") Long var1, @Param(value="requestId") Long var2);

    @Query(value="SELECT drr\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorId\n")
    public List<DonorRequestResponse> findByDonorId(@Param(value="donorId") Long var1);

    @Query(value="SELECT drr\nFROM DonorRequestResponse drr\nWHERE drr.request.id = :requestId\n  AND drr.status = :status\nORDER BY drr.respondedAt ASC\n")
    public Optional<DonorRequestResponse> findFirstByRequestIdAndStatus(@Param(value="requestId") Long var1, @Param(value="status") ResponseStatus var2);

    @Query(value="SELECT COUNT(drr)\nFROM DonorRequestResponse drr\nWHERE drr.request.id = :requestId\n  AND drr.status = :status\n")
    public long countByRequestIdAndStatus(@Param(value="requestId") Long var1, @Param(value="status") ResponseStatus var2);

    @Modifying
    @Transactional
    @Query(value="UPDATE DonorRequestResponse drr\nSET drr.status      = :status,\n    drr.respondedAt = :respondedAt,\n    drr.notes       = :notes\nWHERE drr.donor.id   = :donorId\n  AND drr.request.id = :requestId\n")
    public int updateResponseStatus(@Param(value="donorId") Long var1, @Param(value="requestId") Long var2, @Param(value="status") ResponseStatus var3, @Param(value="respondedAt") LocalDateTime var4, @Param(value="notes") String var5);

    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Transactional
    @Query(value="UPDATE DonorRequestResponse drr\nSET drr.status      = 'NO_RESPONSE',\n    drr.respondedAt = :now\nWHERE drr.request.id = :requestId\n  AND drr.status     = 'NOTIFIED'\n")
    public int bulkMarkNoResponse(@Param(value="requestId") Long var1, @Param(value="now") LocalDateTime var2);

    @Query(value="SELECT drr FROM DonorRequestResponse drr\nWHERE drr.status = 'NOTIFIED'\n  AND drr.notifiedAt < :timeout\nORDER BY drr.notifiedAt ASC\n")
    public List<DonorRequestResponse> findPendingResponsesBefore(@Param(value="timeout") LocalDateTime var1);

    @Query(value="SELECT COALESCE(\n    (\n        SUM(\n            CASE\n                WHEN drr.status IN ('ACCEPTED', 'REJECTED')\n                THEN 1\n                ELSE 0\n            END\n        ) * 100.0\n    ) / NULLIF(COUNT(drr), 0),\n    0\n)\nFROM DonorRequestResponse drr\nWHERE drr.donor.id = :donorId\n")
    public Double calculateResponseRate(@Param(value="donorId") Long donorId);

    @Query(value="SELECT drr.request.id, drr.status, COUNT(drr.id)\nFROM DonorRequestResponse drr\nWHERE drr.request.id IN :requestIds\n  AND drr.status IN ('NOTIFIED', 'ACCEPTED')\nGROUP BY drr.request.id, drr.status\n")
    public List<Object[]> countStatusesForRequestIds(@Param(value="requestIds") List<Long> var1);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM DonorRequestResponse drr\nWHERE drr.request.id IN (\n    SELECT br.id FROM BloodRequest br\n    WHERE br.createdAt < :threshold\n      AND br.status IN :statuses\n)\n")
    public int deleteByRequestCreatedAtBeforeAndStatusIn(@Param(value="threshold") LocalDateTime var1, @Param(value="statuses") List<BloodRequestStatus> var2);
}

