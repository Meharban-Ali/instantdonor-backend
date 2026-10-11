/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 */
package com.blood_donor.repository.donor_repository;

import com.blood_donor.model.entity.DonationHistory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationHistoryQueryRepository {
    public Page<DonationHistory> findByDonorProfileIdOrderByDonationDateDesc(Long var1, Pageable var2);

    @Query(value="SELECT dh FROM DonationHistory dh\nWHERE dh.donorProfile.id = :donorProfileId\nORDER BY dh.donationDate DESC\n")
    public Page<DonationHistory> findRecentDonations(@Param(value="donorProfileId") Long var1, Pageable var2);

    public List<DonationHistory> findByBloodRequestId(Long var1);

    @Query(value="SELECT dh FROM DonationHistory dh\nWHERE dh.donorProfile.id = :donorProfileId\n  AND dh.donationDate BETWEEN :startDate AND :endDate\nORDER BY dh.donationDate DESC\n")
    public List<DonationHistory> findByDonorProfileIdAndDateRange(@Param(value="donorProfileId") Long var1, @Param(value="startDate") LocalDateTime var2, @Param(value="endDate") LocalDateTime var3);

    @Query(value="SELECT dh FROM DonationHistory dh\nWHERE dh.isVerified = false\nORDER BY dh.donationDate DESC\n")
    public Page<DonationHistory> findUnverifiedDonations(Pageable var1);

    public Page<DonationHistory> findByHospitalNameIgnoreCase(String var1, Pageable var2);
}

