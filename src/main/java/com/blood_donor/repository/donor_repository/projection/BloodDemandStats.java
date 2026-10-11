/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.repository.donor_repository.projection;

import com.blood_donor.common.enums.BloodGroup;

public interface BloodDemandStats {
    public String getCity();

    public BloodGroup getBloodGroup();

    public Long getCount();
}

