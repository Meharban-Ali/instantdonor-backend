package com.blood_donor.admin.service;

import com.blood_donor.admin.dto.BloodGroupStatsDto;
import com.blood_donor.admin.dto.CityDemandDto;
import java.util.List;
import java.util.Map;

public interface IAdminAnalyticsService {
    public List<BloodGroupStatsDto> getBloodGroupStats();

    public List<CityDemandDto> getCityDemandStats();

    public Map<String, Long> getMonthlyDonationTrend();
}

