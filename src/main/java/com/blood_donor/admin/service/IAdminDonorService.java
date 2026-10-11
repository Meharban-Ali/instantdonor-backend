/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 */
package com.blood_donor.admin.service;

import com.blood_donor.admin.dto.DonorSummaryDto;
import com.blood_donor.common.enums.BloodGroup;
import org.springframework.data.domain.Page;

public interface IAdminDonorService {
    public Page<DonorSummaryDto> getAllDonors(String var1, BloodGroup var2, int var3, int var4);

    public DonorSummaryDto resetDonorScore(Long var1);

    public DonorSummaryDto verifyDonor(Long var1);

    public DonorSummaryDto getDonorById(Long var1);
}

