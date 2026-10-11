/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.cache.annotation.CacheEvict
 *  org.springframework.cache.annotation.Cacheable
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.service.donor_service_impl;

import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.donor_request.DonorUpdateRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.dto.response.donor_response.EligibilityResponse;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.service.donor_service.IDonorProfileService;
import com.blood_donor.service.mapper.DonorMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorProfileService
implements IDonorProfileService {
    private static final Logger log = LoggerFactory.getLogger(DonorProfileService.class);
    private final DonorProfileRepository donorRepo;
    private final DonorMapper donorMapper;

    @Override
    @Transactional(readOnly=true)
    @Cacheable(value={"donorProfile"}, key="#userId")
    public DonorResponse getMyProfile(Long userId) {
        DonorProfile profile = this.findProfileByUserId(userId);
        return this.donorMapper.toResponse(profile, null);
    }

    @Override
    @Transactional
    @CacheEvict(value={"donorSearch", "donorProfile"}, allEntries=true)
    public DonorResponse updateProfile(Long userId, DonorUpdateRequest req) {
        DonorProfile profile = this.findProfileByUserId(userId);
        if (req.hasBloodGroupUpdate()) {
            profile.setBloodGroup(req.getBloodGroup());
        }
        if (req.hasLocationUpdate()) {
            profile.setCity(req.getLocation().getCity());
            profile.setState(req.getLocation().getState());
            profile.setLatitude(req.getLocation().getLatitude());
            profile.setLongitude(req.getLocation().getLongitude());
        }
        if (req.hasAvailabilityUpdate()) {
            profile.setAvailable(req.getAvailability().getIsAvailable());
        }
        DonorProfile updated = (DonorProfile)this.donorRepo.save(profile);
        log.info("Profile updated: userId={}", (Object)userId);
        return this.donorMapper.toResponse(updated, null);
    }

    @Override
    @Transactional
    @CacheEvict(value={"donorSearch"}, allEntries=true)
    public String toggleAvailability(Long userId) {
        DonorProfile profile = this.findProfileByUserId(userId);
        boolean currentStatus = profile.isAvailable();
        if (!currentStatus && !profile.isEligibleToDonate()) {
            throw ApiException.badRequest("Not eligible yet. Next eligible: " + profile.getNextEligibleDate());
        }
        boolean newStatus = !currentStatus;
        int updated = this.donorRepo.updateAvailability(profile.getId(), newStatus, LocalDateTime.now());
        if (updated == 0) {
            throw ApiException.notFound("Donor profile not found");
        }
        String message = newStatus ? "You are now AVAILABLE for donation" : "You are now UNAVAILABLE";
        log.info("Availability toggled: userId={}, available={}", (Object)userId, (Object)newStatus);
        return message;
    }

    @Override
    @Transactional(readOnly=true)
    public EligibilityResponse checkEligibility(Long userId) {
        DonorProfile profile = this.findProfileByUserId(userId);
        boolean eligible = profile.isEligibleToDonate();
        long daysRemaining = 0L;
        if (!eligible && profile.getNextEligibleDate() != null) {
            daysRemaining = LocalDate.now().until(profile.getNextEligibleDate()).getDays();
        }
        return EligibilityResponse.builder().eligible(eligible).lastDonationDate(profile.getLastDonationDate()).nextEligibleDate(profile.getNextEligibleDate()).daysRemaining(daysRemaining).message((String)(eligible ? "You are eligible to donate blood!" : "You can donate after " + daysRemaining + " more days")).build();
    }

    private DonorProfile findProfileByUserId(Long userId) {
        return this.donorRepo.findByUserId(userId).orElseThrow(() -> ApiException.notFound("Donor profile not found. Please register as donor first."));
    }

    public DonorProfileService(DonorProfileRepository donorRepo, DonorMapper donorMapper) {
        this.donorRepo = donorRepo;
        this.donorMapper = donorMapper;
    }
}

