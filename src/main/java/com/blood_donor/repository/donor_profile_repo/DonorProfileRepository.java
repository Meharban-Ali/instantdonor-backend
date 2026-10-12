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
package com.blood_donor.repository.donor_profile_repo;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.entity.DonorProfile;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DonorProfileRepository
extends JpaRepository<DonorProfile, Long> {
    @EntityGraph(attributePaths = {"user"})
    public Optional<DonorProfile> findByUserId(Long var1);

    public boolean existsByUserId(Long var1);

    @EntityGraph(attributePaths = {"user"})
    public List<DonorProfile> findByUserIdIn(List<Long> var1);

    @EntityGraph(attributePaths = {"user"})
    public List<DonorProfile> findByIdIn(List<Long> var1);

    @EntityGraph(attributePaths = {"user"})
    public List<DonorProfile> findByBloodGroupAndCityAndIsAvailableTrue(BloodGroup var1, String var2);

    @EntityGraph(attributePaths = {"user"})
    public List<DonorProfile> findByBloodGroupInAndCityIgnoreCaseAndIsAvailableTrue(List<BloodGroup> var1, String var2);

    @EntityGraph(attributePaths = {"user"})
    public List<DonorProfile> findByBloodGroupInAndIsAvailableTrue(List<BloodGroup> var1);

    @Query(value="SELECT dp.*\nFROM donor_profiles dp\nWHERE dp.is_available = true\n  AND dp.latitude IS NOT NULL AND dp.longitude IS NOT NULL\n  AND dp.blood_group IN :bloodGroups\n  AND (6371 * acos(GREATEST(-1.0, LEAST(1.0,\n      cos(radians(:lat)) * cos(radians(dp.latitude))\n      * cos(radians(dp.longitude) - radians(:lng))\n      + sin(radians(:lat)) * sin(radians(dp.latitude))\n  )))) <= :radiusKm\nORDER BY dp.reliability_score DESC\nLIMIT :limit\n", nativeQuery=true)
    public List<DonorProfile> findNearByCompatibleDonors(@Param(value="lat") double var1, @Param(value="lng") double var3, @Param(value="radiusKm") double var5, @Param(value="bloodGroups") List<String> var7, @Param(value="limit") int var8);

    public long countByIsAvailableTrue();

    public long countByBloodGroup(BloodGroup var1);

    public long countByBloodGroupAndIsAvailableTrue(BloodGroup var1);

    public long countByCityIgnoreCase(String var1);

    public long countByCityIgnoreCaseAndIsAvailableTrue(String var1);

    public long countByBloodGroupAndCityIgnoreCaseAndIsAvailableTrue(BloodGroup var1, String var2);

    @EntityGraph(attributePaths = {"user"})
    public Page<DonorProfile> findByBloodGroupInAndCityIgnoreCaseAndIsAvailableTrue(List<BloodGroup> var1, String var2, Pageable var3);

    @EntityGraph(attributePaths = {"user"})
    public Page<DonorProfile> findByCityIgnoreCaseAndIsAvailableTrueOrderByReliabilityScoreDesc(String var1, Pageable var2);

    @EntityGraph(attributePaths = {"user"})
    public Page<DonorProfile> findByBloodGroupAndIsAvailableTrue(BloodGroup var1, Pageable var2);

    @EntityGraph(attributePaths = {"user"})
    public Page<DonorProfile> findByIsAvailableTrueOrderByReliabilityScoreDesc(Pageable var1);

    @EntityGraph(attributePaths = {"user"})
    public Page<DonorProfile> findAllByOrderByCreatedAtDesc(Pageable var1);

    @Query(value="SELECT d FROM DonorProfile d\nWHERE d.lastDonationDate < :dateThreshold\n   OR d.lastDonationDate IS NULL\n")
    public List<DonorProfile> findEligibleDonors(@Param(value="dateThreshold") LocalDate var1);

    @Query(value="SELECT COUNT(d) FROM DonorProfile d\nWHERE d.lastDonationDate < :dateThreshold\n   OR d.lastDonationDate IS NULL\n")
    public long countEligibleDonors(@Param(value="dateThreshold") LocalDate var1);

    @Query(value="SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END\nFROM DonorProfile d\nWHERE d.id = :donorId\n  AND (d.lastDonationDate < :dateThreshold\n    OR d.lastDonationDate IS NULL)\n")
    public boolean isDonorEligible(@Param(value="donorId") Long var1, @Param(value="dateThreshold") LocalDate var2);

    @Modifying
    @Transactional
    @Query(value="UPDATE DonorProfile dp\nSET dp.reliabilityScore = :score,\n    dp.updatedAt        = :updatedAt\nWHERE dp.id = :donorId\n")
    public int updateReliabilityScore(@Param(value="donorId") Long var1, @Param(value="score") double var2, @Param(value="updatedAt") LocalDateTime var4);

    @Modifying
    @Transactional
    @Query(value="UPDATE DonorProfile dp\nSET dp.isAvailable = :available,\n    dp.updatedAt   = :updatedAt\nWHERE dp.id = :donorId\n")
    public int updateAvailability(@Param(value="donorId") Long var1, @Param(value="available") boolean var2, @Param(value="updatedAt") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE DonorProfile dp\nSET dp.responseRate = :responseRate,\n    dp.updatedAt    = :updatedAt\nWHERE dp.id = :donorId\n")
    public int updateResponseRate(@Param(value="donorId") Long var1, @Param(value="responseRate") double var2, @Param(value="updatedAt") LocalDateTime var4);

    @Modifying
    @Transactional
    @Query(value="UPDATE DonorProfile dp\nSET dp.lastDonationDate = :donationDate,\n    dp.totalDonations   = dp.totalDonations + 1,\n    dp.updatedAt        = :updatedAt\nWHERE dp.id = :donorId\n")
    public int updateAfterDonation(@Param(value="donorId") Long var1, @Param(value="donationDate") LocalDate var2, @Param(value="updatedAt") LocalDateTime var3);

    @Modifying
    @Transactional
    @Query(value="UPDATE DonorProfile dp\nSET dp.isAvailable = true,\n    dp.updatedAt   = :updatedAt\nWHERE dp.id IN :donorIds\n")
    public int bulkUpdateAvailability(@Param(value="donorIds") List<Long> var1, @Param(value="updatedAt") LocalDateTime var2);

    public Page<DonorProfile> findByReliabilityScoreGreaterThanEqualAndIsAvailableTrue(double var1, Pageable var3);

    public Page<DonorProfile> findTopByOrderByReliabilityScoreDesc(Pageable var1);

    public Page<DonorProfile> findTopByOrderByTotalDonationsDesc(Pageable var1);

    @Query(value="SELECT dp FROM DonorProfile dp\nWHERE dp.updatedAt < :before\n  AND dp.isAvailable = true\n")
    public List<DonorProfile> findDonorsNotUpdatedSince(@Param(value="before") LocalDateTime var1);

    @Query(value="SELECT dp FROM DonorProfile dp\nWHERE dp.bloodGroup              = :bloodGroup\n  AND LOWER(dp.city)             = LOWER(:city)\n  AND dp.isAvailable             = true\n  AND dp.reliabilityScore       >= :minScore\n")
    public Page<DonorProfile> findByBloodGroupAndCityAndMinScore(@Param(value="bloodGroup") BloodGroup var1, @Param(value="city") String var2, @Param(value="minScore") double var3, Pageable var5);

    @Query(value="SELECT dp.* FROM donor_profiles dp\nWHERE dp.blood_group IN :bloodGroups\n  AND LOWER(dp.city) = LOWER(:city)\n  AND dp.is_available = true\nORDER BY dp.reliability_score DESC\nLIMIT :limit\n", nativeQuery=true)
    public List<DonorProfile> findCityDonorsWithLimit(@Param(value="bloodGroups") List<String> var1, @Param(value="city") String var2, @Param(value="limit") int var3);

    public List<DonorProfile> findByResponseRateIsNull();

    @Query(value="SELECT (\n    6371 * acos(\n        GREATEST(-1.0, LEAST(1.0,\n            cos(radians(:lat1))\n            * cos(radians(:lat2))\n            * cos(radians(:lng2) - radians(:lng1))\n            + sin(radians(:lat1))\n            * sin(radians(:lat2))\n        ))\n    )\n)\n", nativeQuery=true)
    public Double calculateDistanceKm(@Param(value="lat1") double var1, @Param(value="lng1") double var3, @Param(value="lat2") double var5, @Param(value="lng2") double var7);
}

