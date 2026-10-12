package com.blood_donor.admin.service_Impl;

import com.blood_donor.admin.dto.BloodGroupStatsDto;
import com.blood_donor.admin.dto.CityDemandDto;
import com.blood_donor.admin.dto.DashboardStatsDto;
import com.blood_donor.admin.dto.PlatformHealthDto;
import com.blood_donor.admin.dto.RecentRequestDto;
import com.blood_donor.admin.service.IDashboardService;
import com.blood_donor.ai_service.service.IAiMatchingService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.DonationHistoryRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService
implements IDashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private final UserRepository userRepo;
    private final DonorProfileRepository donorRepo;
    private final BloodRequestRepository requestRepo;
    private final DonationHistoryRepository donationRepo;
    private final IAiMatchingService aiService;
    private static final List<BloodRequestStatus> ACTIVE_STATUSES = List.of(BloodRequestStatus.PENDING, BloodRequestStatus.ACTIVE, BloodRequestStatus.MATCHING);
    private static final int TOP_CITIES_LIMIT = 5;
    private static final int RECENT_REQUEST_LIMIT = 10;

    @Override
    @Transactional(readOnly=true)
    @Cacheable(value={"dashboardStats"}, key="'admin_dashboard'")
    public DashboardStatsDto getDashboardStats() {
        log.info("Building dashboard stats \u2014 DB query");
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime yearStart = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        return DashboardStatsDto.builder().totalUsers(this.userRepo.count()).activeUsers(this.userRepo.countByIsActiveTrue()).totalDonors(this.donorRepo.count()).activeDonors(this.donorRepo.countByIsAvailableTrue()).totalHospitals(this.userRepo.countByRole(Role.HOSPITAL)).newUsersThisMonth(this.userRepo.countByCreatedAtAfter(monthStart)).totalRequests(this.requestRepo.count()).activeRequests(this.requestRepo.countByStatusIn(ACTIVE_STATUSES)).fulfilledRequests(this.requestRepo.countByStatus(BloodRequestStatus.FULFILLED)).expiredRequests(this.requestRepo.countByStatus(BloodRequestStatus.EXPIRED)).cancelledRequests(this.requestRepo.countByStatus(BloodRequestStatus.CANCELLED)).criticalRequests(this.requestRepo.countByUrgencyAndStatusIn(UrgencyLevel.CRITICAL, ACTIVE_STATUSES)).requestsToday(this.requestRepo.countByCreatedAtAfter(todayStart)).totalDonations(this.donationRepo.count()).donationsThisMonth(this.donationRepo.countByCreatedAtAfter(monthStart)).donationsThisYear(this.donationRepo.countByCreatedAtAfter(yearStart)).donationsToday(this.donationRepo.countByCreatedAtAfter(todayStart)).bloodGroupStats(this.buildBloodGroupStats()).topCitiesByDemand(this.buildTopCities()).recentRequests(this.buildRecentRequests()).platformHealth(this.buildPlatformHealth()).generatedAt(LocalDateTime.now()).dataSource("LIVE").build();
    }

    @Override
    @Transactional(readOnly=true)
    @CacheEvict(value={"dashboardStats"}, key="'admin_dashboard'")
    public DashboardStatsDto refreshDashboardStats() {
        log.info("Dashboard cache evicted \u2014 forcing refresh");
        return this.getDashboardStats();
    }

    private List<BloodGroupStatsDto> buildBloodGroupStats() {
        return Arrays.stream(BloodGroup.values()).map(bg -> {
            long donors = this.donorRepo.countByBloodGroup((BloodGroup)((Object)bg));
            long available = this.donorRepo.countByBloodGroupAndIsAvailableTrue((BloodGroup)((Object)bg));
            long pending = this.requestRepo.countByBloodGroupAndStatusIn((BloodGroup)((Object)bg), ACTIVE_STATUSES);
            double ratio = pending == 0L ? (available > 0L ? 99.0 : 0.0) : (double)available / (double)pending;
            return BloodGroupStatsDto.builder().bloodGroup((BloodGroup)((Object)bg)).bloodGroupDisplay(bg.getDisplayName()).donorCount(donors).availableCount(available).pendingRequests(pending).supplyDemandRatio((double)Math.round(ratio * 100.0) / 100.0).build();
        }).toList();
    }

    private List<CityDemandDto> buildTopCities() {
        return this.requestRepo.findTopCitiesByActiveDemand(ACTIVE_STATUSES, (Pageable)PageRequest.of((int)0, (int)5)).stream().map(projection -> {
            String city = projection.getCity();
            long criticalCount = this.requestRepo.countByCityIgnoreCaseAndUrgencyAndStatusIn(city, UrgencyLevel.CRITICAL, ACTIVE_STATUSES);
            long availableDonors = this.donorRepo.countByCityIgnoreCaseAndIsAvailableTrue(city);
            long activeCount = projection.getActiveCount();
            return CityDemandDto.builder().city(city).activeRequests(activeCount).criticalRequests(criticalCount).availableDonors(availableDonors).build();
        }).toList();
    }

    private List<RecentRequestDto> buildRecentRequests() {
        LocalDateTime now = LocalDateTime.now();
        return this.requestRepo.findTop10ByOrderByCreatedAtDesc().stream().map(req -> RecentRequestDto.builder().requestId(req.getId()).patientName(req.getPatientName()).bloodGroup(req.getBloodGroup()).bloodGroupDisplay(req.getBloodGroup() != null ? req.getBloodGroup().getDisplayName() : null).urgency(req.getUrgency()).city(req.getCity()).status(req.getStatus().name()).createdAt(req.getCreatedAt()).minutesAgo(req.getCreatedAt() != null ? Duration.between(req.getCreatedAt(), now).toMinutes() : 0L).build()).toList();
    }

    private PlatformHealthDto buildPlatformHealth() {
        Double avgHours;
        boolean aiOnline;
        long total = this.requestRepo.count();
        long fulfilled = this.requestRepo.countByStatus(BloodRequestStatus.FULFILLED);
        double rate = total > 0L ? (double)Math.round((double)fulfilled / (double)total * 1000.0) / 10.0 : 0.0;
        try {
            aiOnline = this.aiService.isAiServiceHealthy();
        }
        catch (Exception e) {
            log.warn("AI health check failed in dashboard: {}", (Object)e.getMessage());
            aiOnline = false;
        }
        try {
            avgHours = this.requestRepo.findAvgFulfillmentHours();
        }
        catch (Exception e) {
            log.warn("Avg fulfillment hours query failed: {}", (Object)e.getMessage());
            avgHours = null;
        }
        return PlatformHealthDto.builder().fulfillmentRate(rate).avgResponseTimeHours(avgHours).aiServiceOnline(aiOnline).avgAiMatchScore(null).emailDeliveryRate(95.0).smsDeliveryRate(90.0).dbHealthy(true).redisHealthy(true).build();
    }

    public DashboardService(UserRepository userRepo, DonorProfileRepository donorRepo, BloodRequestRepository requestRepo, DonationHistoryRepository donationRepo, IAiMatchingService aiService) {
        this.userRepo = userRepo;
        this.donorRepo = donorRepo;
        this.requestRepo = requestRepo;
        this.donationRepo = donationRepo;
        this.aiService = aiService;
    }
}

