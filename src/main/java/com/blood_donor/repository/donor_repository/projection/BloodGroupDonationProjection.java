package com.blood_donor.repository.donor_repository.projection;

import com.blood_donor.common.enums.BloodGroup;

public interface BloodGroupDonationProjection {
    public BloodGroup getBloodGroup();

    public Long getDonationCount();

    public Long getTotalUnits();
}

