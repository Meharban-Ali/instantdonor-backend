/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.jpa.repository.Query
 *  org.springframework.data.repository.query.Param
 */
package com.blood_donor.repository.donor_repository;

import com.blood_donor.repository.donor_repository.projection.DonationSummary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationHistoryStatsRepository {
    public long countByDonorProfileId(Long var1);

    public long countByDonorProfileIdAndIsVerifiedTrue(Long var1);

    @Query(value="SELECT\n    dh.donorProfile.id   AS donorProfileId,\n    COUNT(dh)            AS totalDonations,\n    SUM(CASE WHEN dh.isVerified = true THEN 1 ELSE 0 END)\n                         AS verifiedDonations,\n    dh.bloodGroup        AS bloodGroup,\n    MAX(dh.donationDate) AS lastDonationDate\nFROM DonationHistory dh\nWHERE dh.donorProfile.id = :donorProfileId\nGROUP BY dh.donorProfile.id, dh.bloodGroup\n")
    public Optional<DonationSummary> getDonorSummary(@Param(value="donorProfileId") Long var1);

    @Query(value="SELECT COUNT(dh) FROM DonationHistory dh")
    public long countTotalDonations();

    @Query(value="SELECT\n    COUNT(CASE WHEN dh.isVerified = true THEN 1 END) * 100.0\n    / COUNT(dh)\nFROM DonationHistory dh\n")
    public Double calculateVerificationRate();

    @Query(value="SELECT dh.donorProfile.id, COUNT(dh) AS donationCount\nFROM DonationHistory dh\nGROUP BY dh.donorProfile.id\nORDER BY donationCount DESC\n")
    public List<Object[]> findTopDonors(Pageable var1);
}

