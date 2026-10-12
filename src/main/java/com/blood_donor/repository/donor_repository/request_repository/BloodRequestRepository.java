/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Modifying
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 *  org.springframework.stereotype.Repository
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.repository.donor_repository.request_repository;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.repository.donor_repository.projection.CityDemandProjection;
import com.blood_donor.repository.donor_repository.projection.CityStatsProjection;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BloodRequestRepository
extends JpaRepository<BloodRequest, Long> {
    public long countByStatus(BloodRequestStatus var1);

    public long countByStatusIn(List<BloodRequestStatus> var1);

    public long countByCreatedAtAfter(LocalDateTime var1);

    public long countByCreatedAtBetween(LocalDateTime var1, LocalDateTime var2);

    public long countByUrgencyAndStatusIn(UrgencyLevel var1, List<BloodRequestStatus> var2);

    public long countByBloodGroupAndStatusIn(BloodGroup var1, List<BloodRequestStatus> var2);

    public long countByCityIgnoreCaseAndUrgencyAndStatusIn(String var1, UrgencyLevel var2, List<BloodRequestStatus> var3);

    public long countByCityIgnoreCase(String var1);

    public long countByCityIgnoreCaseAndStatusIn(String var1, List<BloodRequestStatus> var2);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT br FROM BloodRequest br WHERE br.id = :id")
    public Optional<BloodRequest> findByIdForUpdate(@Param("id") Long id);

    public boolean existsByCreatedByIdAndStatusInAndBloodGroup(Long userId, List<BloodRequestStatus> statuses, BloodGroup bloodGroup);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByStatusInOrderByCreatedAtDesc(List<BloodRequestStatus> var1, Pageable var2);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByStatusInAndCityIgnoreCase(List<BloodRequestStatus> var1, String var2, Pageable var3);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByStatusInAndBloodGroup(List<BloodRequestStatus> var1, BloodGroup var2, Pageable var3);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByStatusInAndCityIgnoreCaseAndBloodGroup(List<BloodRequestStatus> var1, String var2, BloodGroup var3, Pageable var4);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByCreatedByIdOrderByCreatedAtDesc(Long var1, Pageable var2);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByStatusAndCityIgnoreCaseAndBloodGroupOrderByCreatedAtDesc(BloodRequestStatus var1, String var2, BloodGroup var3, Pageable var4);

    @EntityGraph(attributePaths = {"createdBy"})
    public Page<BloodRequest> findByStatusOrderByCreatedAtDesc(BloodRequestStatus var1, Pageable var2);

    @Query(value="SELECT AVG(TIMESTAMPDIFF(HOUR, created_at, fulfilled_at))\nFROM blood_requests\nWHERE status = 'FULFILLED'\n  AND fulfilled_at IS NOT NULL\n", nativeQuery=true)
    public Double findAvgFulfillmentHours();

    @Query(value="SELECT br.city      AS city,\n       COUNT(br.id) AS activeCount\nFROM BloodRequest br\nWHERE br.status IN :statuses\nGROUP BY br.city\nORDER BY COUNT(br.id) DESC\n")
    public List<CityDemandProjection> findTopCitiesByActiveDemand(@Param(value="statuses") List<BloodRequestStatus> var1, Pageable var2);

    @Query(value="SELECT\n    city                                               AS city,\n    COUNT(*)                                           AS totalRequests,\n    SUM(CASE WHEN status IN ('ACTIVE','PENDING','MATCHING')\n             THEN 1 ELSE 0 END)                        AS activeRequests,\n    SUM(CASE WHEN status = 'FULFILLED'\n             THEN 1 ELSE 0 END)                        AS fulfilledRequests,\n    SUM(CASE WHEN urgency = 'CRITICAL'\n             THEN 1 ELSE 0 END)                        AS criticalRequests\nFROM blood_requests\nWHERE created_at >= :since\nGROUP BY city\nORDER BY COUNT(*) DESC\nLIMIT :limit\n", nativeQuery=true)
    public List<CityStatsProjection> findCityWiseDemand(@Param(value="since") LocalDateTime var1, @Param(value="limit") int var2);

    @EntityGraph(attributePaths = {"createdBy"})
    public List<BloodRequest> findTop10ByOrderByCreatedAtDesc();

    public boolean existsByIdAndStatusIn(Long var1, List<BloodRequestStatus> var2);

    public List<BloodRequest> findByCreatedByIdAndStatusIn(Long var1, List<BloodRequestStatus> var2);

    @Query(value="SELECT br FROM BloodRequest br\nWHERE br.status IN :statuses\n  AND br.expiredAt < :now\n")
    public List<BloodRequest> findExpiredRequests(@Param(value="statuses") List<BloodRequestStatus> var1, @Param(value="now") LocalDateTime var2);

    @Modifying
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status    = :expiredStatus,\n    br.updatedAt = :currentTime\nWHERE br.status IN :activeStatuses\n  AND br.expiredAt < :currentTime\n")
    public int bulkExpireRequests(@Param(value="activeStatuses") List<BloodRequestStatus> var1, @Param(value="expiredStatus") BloodRequestStatus var2, @Param(value="currentTime") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status    = :newStatus,\n    br.updatedAt = :now\nWHERE br.id IN :ids\n")
    public int bulkUpdateStatus(@Param(value="ids") List<Long> var1, @Param(value="newStatus") BloodRequestStatus var2, @Param(value="now") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status    = :cancelStatus,\n    br.updatedAt = :now\nWHERE br.id = :requestId\n")
    public int cancelRequestById(@Param(value="requestId") Long var1, @Param(value="cancelStatus") BloodRequestStatus var2, @Param(value="now") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status      = :fulfillStatus,\n    br.fulfilledAt = :now,\n    br.updatedAt   = :now\nWHERE br.id = :requestId\n")
    public int fulfillRequestById(@Param(value="requestId") Long var1, @Param(value="fulfillStatus") BloodRequestStatus var2, @Param(value="now") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status    = :status,\n    br.updatedAt = :updatedAt\nWHERE br.id = :requestId\n")
    public int updateStatus(@Param(value="requestId") Long var1, @Param(value="status") BloodRequestStatus var2, @Param(value="updatedAt") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE BloodRequest br\nSET br.status      = :status,\n    br.fulfilledAt = :fulfilledAt,\n    br.updatedAt   = :updatedAt\nWHERE br.id = :requestId\n")
    public int updateStatusWithFulfillment(@Param(value="requestId") Long var1, @Param(value="status") BloodRequestStatus var2, @Param(value="fulfilledAt") LocalDateTime var3, @Param(value="updatedAt") LocalDateTime var4);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM BloodRequest br\nWHERE br.createdAt < :threshold\n  AND br.status IN :statuses\n")
    public int deleteByCreatedAtBeforeAndStatusIn(@Param(value="threshold") LocalDateTime var1, @Param(value="statuses") List<BloodRequestStatus> var2);
}

