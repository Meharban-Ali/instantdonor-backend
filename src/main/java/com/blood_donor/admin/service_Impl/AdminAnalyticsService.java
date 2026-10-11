/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.cache.annotation.Cacheable
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.admin.service_Impl;

import com.blood_donor.admin.dto.BloodGroupStatsDto;
import com.blood_donor.admin.dto.CityDemandDto;
import com.blood_donor.admin.service.IAdminAnalyticsService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.DonationHistoryRepository;
import com.blood_donor.repository.donor_repository.projection.CityStatsProjection;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAnalyticsService
implements IAdminAnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(AdminAnalyticsService.class);
    private final BloodRequestRepository requestRepo;
    private final DonorProfileRepository donorRepo;
    private final DonationHistoryRepository donationRepo;
    private static final List<BloodRequestStatus> ACTIVE_STATUSES = List.of(BloodRequestStatus.PENDING, BloodRequestStatus.ACTIVE, BloodRequestStatus.MATCHING);

    @Override
    @Cacheable(value={"adminDashboard"}, key="'bloodGroupStats'")
    @Transactional(readOnly=true)
    public List<BloodGroupStatsDto> getBloodGroupStats() {
        return Arrays.stream(BloodGroup.values()).map(bg -> {
            long totalDonors = this.donorRepo.countByBloodGroup((BloodGroup)((Object)bg));
            long availableDonors = this.donorRepo.countByBloodGroupAndIsAvailableTrue((BloodGroup)((Object)bg));
            long pendingRequests = this.requestRepo.countByBloodGroupAndStatusIn((BloodGroup)((Object)bg), ACTIVE_STATUSES);
            double ratio = pendingRequests > 0L ? (double)availableDonors / (double)pendingRequests : (availableDonors > 0L ? 99.0 : 0.0);
            return BloodGroupStatsDto.builder().bloodGroup((BloodGroup)((Object)bg)).bloodGroupDisplay(bg.getDisplayName()).donorCount(totalDonors).availableCount(availableDonors).pendingRequests(pendingRequests).supplyDemandRatio((double)Math.round(ratio * 100.0) / 100.0).build();
        }).toList();
    }

    @Override
    @Cacheable(value={"adminDashboard"}, key="'cityDemand'")
    @Transactional(readOnly=true)
    public List<CityDemandDto> getCityDemandStats() {
        LocalDateTime since = LocalDateTime.now().minusDays(30L);
        List<CityStatsProjection> rawData = this.requestRepo.findCityWiseDemand(since, 10);
        return rawData.stream().map(projection -> {
            String city = projection.getCity();
            long availableDonors = this.donorRepo.countByCityIgnoreCaseAndIsAvailableTrue(city);
            long total = projection.getTotalRequests();
            long fulfilled = projection.getFulfilledRequests();
            Double rate = total > 0L ? (double)Math.round((double)fulfilled / (double)total * 1000.0) / 10.0 : 0.0;
            return CityDemandDto.builder().city(city).activeRequests(projection.getActiveRequests()).fulfilledRequests(projection.getFulfilledRequests()).criticalRequests(projection.getCriticalRequests()).availableDonors(availableDonors).fulfillmentRate(rate).build();
        }).toList();
    }

    @Override
    @Cacheable(value={"adminDashboard"}, key="'monthlyTrend'")
    @Transactional(readOnly=true)
    public Map<String, Long> getMonthlyDonationTrend() {
        LinkedHashMap<String, Long> trend = new LinkedHashMap<String, Long>();
        LocalDate now = LocalDate.now();
        for (int i = 11; i >= 0; --i) {
            LocalDate monthStartDate = now.minusMonths(i).withDayOfMonth(1);
            LocalDateTime monthStart = monthStartDate.atStartOfDay();
            LocalDateTime monthEnd = monthStartDate.plusMonths(1L).atStartOfDay();
            long count = this.donationRepo.countByDonationDateBetween(monthStart, monthEnd);
            String monthKey = monthStartDate.getMonth().toString().substring(0, 3) + " " + monthStartDate.getYear();
            trend.put(monthKey, count);
        }
        return trend;
    }

    public AdminAnalyticsService(BloodRequestRepository requestRepo, DonorProfileRepository donorRepo, DonationHistoryRepository donationRepo) {
        this.requestRepo = requestRepo;
        this.donorRepo = donorRepo;
        this.donationRepo = donationRepo;
    }
}

