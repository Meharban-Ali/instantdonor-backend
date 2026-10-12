package com.blood_donor.repository.donor_repository.projection;

import com.blood_donor.common.enums.BloodGroup;
import java.time.LocalDateTime;

public interface DonationSummary {
    public Long getDonorProfileId();

    public Long getTotalDonations();

    public Long getVerifiedDonations();

    public BloodGroup getBloodGroup();

    public LocalDateTime getLastDonationDate();
}

