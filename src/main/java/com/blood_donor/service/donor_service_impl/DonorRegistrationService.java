/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.cache.annotation.CacheEvict
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.service.donor_service_impl;

import com.blood_donor.common.enums.Role;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.donor_request.DonorRegisterRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.service.donor_service.IDonorRegistrationService;
import com.blood_donor.service.mapper.DonorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorRegistrationService
implements IDonorRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(DonorRegistrationService.class);
    private final DonorProfileRepository donorRepo;
    private final UserRepository userRepo;
    private final DonorMapper donorMapper;

    @Override
    @Transactional
    @CacheEvict(value={"donorSearch"}, allEntries=true)
    public DonorResponse registerDonor(Long userId, DonorRegisterRequest req) {
        if (this.donorRepo.existsByUserId(userId)) {
            throw ApiException.conflict("Donor profile already exists");
        }
        User user = (User)this.userRepo.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));
        DonorProfile profile = DonorProfile.builder().user(user).bloodGroup(req.getBloodGroup()).city(req.getLocation().getCity()).state(req.getLocation().getState()).latitude(req.getLocation().getLatitude()).longitude(req.getLocation().getLongitude()).lastDonationDate(req.getLastDonationDate()).nextEligibleDate(req.getLastDonationDate() != null ? req.getLastDonationDate().plusDays(90L) : null).build();
        DonorProfile saved = (DonorProfile)this.donorRepo.save(profile);
        user.addRole(Role.DONOR);
        this.userRepo.save(user);
        log.info("Donor registered: userId={}, bloodGroup={}, city={}", new Object[]{userId, req.getBloodGroup(), req.getLocation().getCity()});
        return this.donorMapper.toResponse(saved, null);
    }

    public DonorRegistrationService(DonorProfileRepository donorRepo, UserRepository userRepo, DonorMapper donorMapper) {
        this.donorRepo = donorRepo;
        this.userRepo = userRepo;
        this.donorMapper = donorMapper;
    }
}

