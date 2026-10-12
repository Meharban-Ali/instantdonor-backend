package com.blood_donor.admin.service;

import com.blood_donor.admin.dto.DonorSummaryDto;
import com.blood_donor.common.enums.BloodGroup;
import org.springframework.data.domain.Page;

public interface IAdminDonorService {
    public Page<DonorSummaryDto> getAllDonors(String search, BloodGroup bloodGroup, int page, int size);

    public DonorSummaryDto resetDonorScore(Long donorId);

    public DonorSummaryDto verifyDonor(Long donorId);

    public DonorSummaryDto getDonorById(Long donorId);
}

