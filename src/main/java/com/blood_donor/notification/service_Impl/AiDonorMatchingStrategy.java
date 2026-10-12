package com.blood_donor.notification.service_Impl;

import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.service.IAiMatchingService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.notification.properties.NotificationProperties;
import com.blood_donor.notification.service.DonorMatchingStrategy;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AiDonorMatchingStrategy
implements DonorMatchingStrategy {
    private static final Logger log = LoggerFactory.getLogger(AiDonorMatchingStrategy.class);
    private final IAiMatchingService aiMatchingService;
    private final DonorProfileRepository donorRepo;
    private final NotificationProperties notificationProperties;

    @Override
    public List<AiDonorResult> findDonors(BloodRequest request, int limit) {
        if (this.hasLocation(request)) {
            return this.findByAiMatching(request, limit);
        }
        log.info("No location for request={} \u2014 using city fallback", (Object)request.getId());
        return this.findByCityFallback(request, limit);
    }

    private List<AiDonorResult> findByAiMatching(BloodRequest request, int limit) {
        try {
            AiMatchRequest aiRequest = AiMatchRequest.from(request.getBloodGroup(), request.getLatitude().doubleValue(), request.getLongitude().doubleValue(), this.notificationProperties.getSearchRadiusKm(), request.getUrgency() != null ? request.getUrgency() : UrgencyLevel.NORMAL, request.getUnitsRequired() != null ? request.getUnitsRequired() : 1, limit, request.getId());
            AiMatchResponse response = this.aiMatchingService.findBestDonors(aiRequest);
            if (response != null && response.hasResults()) {
                log.info("AI matching success: requestId={}, found={}", (Object)request.getId(), (Object)response.getTotalFound());
                return response.getDonors();
            }
            log.warn("AI matching returned no donors: requestId={}", (Object)request.getId());
            return List.of();
        }
        catch (Exception e) {
            log.warn("AI matching failed: requestId={}, error={}. Falling back to city search", (Object)request.getId(), (Object)e.getMessage());
            return this.findByCityFallback(request, limit);
        }
    }

    private List<AiDonorResult> findByCityFallback(BloodRequest request, int limit) {
        if (!this.notificationProperties.isFallbackEnabled()) {
            log.info("City fallback disabled: requestId={}", (Object)request.getId());
            return List.of();
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            log.warn("City fallback skipped \u2014 no city: requestId={}", (Object)request.getId());
            return List.of();
        }
        try {
            List<String> compatibleGroups = BloodGroup.getCompatibleDonors(request.getBloodGroup()).stream().map(Enum::name).toList();
            List<DonorProfile> donors = this.donorRepo.findCityDonorsWithLimit(compatibleGroups, request.getCity(), limit);
            log.info("City fallback: city={}, found={}, requestId={}", new Object[]{request.getCity(), donors.size(), request.getId()});
            return donors.stream().map(this::toAiDonorResult).toList();
        }
        catch (Exception e) {
            log.error("City fallback failed: requestId={}, error={}", new Object[]{request.getId(), e.getMessage(), e});
            return List.of();
        }
    }

    private boolean hasLocation(BloodRequest request) {
        return request.getLatitude() != null && request.getLongitude() != null;
    }

    private AiDonorResult toAiDonorResult(DonorProfile donor) {
        String rawPhone = donor.getUser() != null ? donor.getUser().getPhone() : null;
        String maskedPhone = this.maskPhone(rawPhone);
        AiDonorResult result = new AiDonorResult();
        result.setDonorId(donor.getId());
        result.setUserId(donor.getUser() != null ? donor.getUser().getId() : null);
        result.setName(donor.getUser() != null ? donor.getUser().getName() : null);
        result.setPhoneMasked(maskedPhone);
        result.setBloodGroup(donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayName() : null);
        result.setCity(donor.getCity());
        result.setLatitude(donor.getLatitude() != null ? Double.valueOf(donor.getLatitude().doubleValue()) : null);
        result.setLongitude(donor.getLongitude() != null ? Double.valueOf(donor.getLongitude().doubleValue()) : null);
        result.setAvailable(donor.isAvailable());
        result.setDistanceKm(0.0);
        result.setReliabilityScore(donor.getReliabilityScore() != null ? donor.getReliabilityScore().doubleValue() : 50.0);
        result.setResponseRate(donor.getResponseRate() != null ? donor.getResponseRate().doubleValue() : 0.0);
        result.setTotalDonations(donor.getTotalDonations() != null ? donor.getTotalDonations() : 0);
        result.setLastDonationDate(donor.getLastDonationDate());
        return result;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        if (phone.length() < 6) {
            return "****";
        }
        return phone.substring(0, 2) + "*".repeat(phone.length() - 6) + phone.substring(phone.length() - 4);
    }

    public AiDonorMatchingStrategy(IAiMatchingService aiMatchingService, DonorProfileRepository donorRepo, NotificationProperties notificationProperties) {
        this.aiMatchingService = aiMatchingService;
        this.donorRepo = donorRepo;
        this.notificationProperties = notificationProperties;
    }
}

