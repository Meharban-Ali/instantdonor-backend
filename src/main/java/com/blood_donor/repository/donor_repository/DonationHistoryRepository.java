/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.jpa.repository.JpaRepository
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 *  org.springframework.stereotype.Repository
 */
package com.blood_donor.repository.donor_repository;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.entity.DonationHistory;
import com.blood_donor.repository.donor_repository.projection.BloodGroupDonationProjection;
import com.blood_donor.repository.donor_repository.projection.CityDonationProjection;
import com.blood_donor.repository.donor_repository.projection.DonorDonationCountProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationHistoryRepository
extends JpaRepository<DonationHistory, Long> {
    public long countByCreatedAtAfter(LocalDateTime var1);

    public long countByCreatedAtBetween(LocalDateTime var1, LocalDateTime var2);

    public long countByDonationDateAfter(LocalDateTime var1);

    public long countByDonationDateBetween(LocalDateTime var1, LocalDateTime var2);

    public long countByDonorProfileId(Long var1);

    public long countByDonorProfileIdAndDonationDateAfter(Long var1, LocalDateTime var2);

    public long countByBloodGroup(BloodGroup var1);

    public long countByBloodGroupAndDonationDateAfter(BloodGroup var1, LocalDateTime var2);

    public Page<DonationHistory> findByDonorProfileIdOrderByDonationDateDesc(Long var1, Pageable var2);

    public List<DonationHistory> findByDonorProfileIdOrderByDonationDateDesc(Long var1);

    public List<DonationHistory> findByBloodRequestId(Long var1);

    public Optional<DonationHistory> findFirstByDonorProfileIdOrderByDonationDateDesc(Long var1);

    public List<DonationHistory> findTop10ByOrderByCreatedAtDesc();

    @Query(value="SELECT dh FROM DonationHistory dh\nWHERE LOWER(dh.donorProfile.city) = LOWER(:city)\nORDER BY dh.donationDate DESC\n")
    public Page<DonationHistory> findByCityIgnoreCaseOrderByDonationDateDesc(@Param(value="city") String var1, Pageable var2);

    public Page<DonationHistory> findByBloodGroupOrderByDonationDateDesc(BloodGroup var1, Pageable var2);

    public boolean existsByDonorProfileIdAndBloodRequestId(Long var1, Long var2);

    @Query(value="SELECT dh.donorProfile.id\nFROM DonationHistory dh\nWHERE dh.donationDate = :eligibleFrom\nGROUP BY dh.donorProfile.id\n")
    public List<Long> findDonorIdsEligibleAfter(@Param(value="eligibleFrom") LocalDateTime var1);

    @Query(value="SELECT COUNT(dh)\nFROM DonationHistory dh\nWHERE dh.donationDate BETWEEN :startDate AND :endDate\n")
    public long countByDonationDateRange(@Param(value="startDate") LocalDateTime var1, @Param(value="endDate") LocalDateTime var2);

    @Query(value="SELECT MAX(dh.donationDate)\nFROM DonationHistory dh\nWHERE dh.donorProfile.id = :donorId\n")
    public Optional<LocalDateTime> findLastDonationDate(@Param(value="donorId") Long var1);

    @Query(value="SELECT dh.donorProfile.id   AS donorId,\n       COUNT(dh.id)  AS donationCount\nFROM DonationHistory dh\nGROUP BY dh.donorProfile.id\nORDER BY COUNT(dh.id) DESC\n")
    public List<DonorDonationCountProjection> findTopDonorsByDonationCount(Pageable var1);

    @Query(value="SELECT dh.donorProfile.city       AS city,\n       COUNT(dh.id)  AS donationCount\nFROM DonationHistory dh\nWHERE dh.donationDate >= :since\nGROUP BY dh.donorProfile.city\nORDER BY COUNT(dh.id) DESC\n")
    public List<CityDonationProjection> findCityWiseDonations(@Param(value="since") LocalDateTime var1, Pageable var2);

    @Query(value="SELECT dh.bloodGroup  AS bloodGroup,\n       COUNT(dh.id)   AS donationCount,\n       SUM(dh.unitsDonated)  AS totalUnits\nFROM DonationHistory dh\nWHERE dh.donationDate >= :since\nGROUP BY dh.bloodGroup\nORDER BY COUNT(dh.id) DESC\n")
    public List<BloodGroupDonationProjection> findBloodGroupWiseDonations(@Param(value="since") LocalDateTime var1);

    @Query(value="SELECT AVG(donation_count)\nFROM (\n    SELECT COUNT(id) AS donation_count\n    FROM donation_history\n    GROUP BY donor_id\n) donor_counts\n", nativeQuery=true)
    public Double findAvgDonationsPerDonor();

    public Page<DonationHistory> findAllByOrderByCreatedAtDesc(Pageable var1);

    @Query(value="SELECT dh FROM DonationHistory dh\nJOIN dh.donorProfile dp\nJOIN dp.user u\nWHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))\n")
    public Page<DonationHistory> searchByDonorName(@Param(value="query") String var1, Pageable var2);
}

