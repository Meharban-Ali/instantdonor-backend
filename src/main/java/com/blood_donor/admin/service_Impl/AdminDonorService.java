package com.blood_donor.admin.service_Impl;

import com.blood_donor.admin.dto.DonorSummaryDto;
import com.blood_donor.admin.mapper.AdminUserMapper;
import com.blood_donor.admin.service.IAdminDonorService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminDonorService
implements IAdminDonorService {
    private static final Logger log = LoggerFactory.getLogger(AdminDonorService.class);
    private final DonorProfileRepository donorRepo;
    private final AdminUserMapper donorMapper;

    @Override
    @Transactional(readOnly=true)
    public Page<DonorSummaryDto> getAllDonors(String city, BloodGroup bloodGroup, int page, int size) {
        boolean hasGroup;
        PageRequest pageable = PageRequest.of((int)page, (int)size, (Sort)Sort.by((String[])new String[]{"createdAt"}).descending());
        boolean hasCity = city != null && !city.isBlank();
        boolean bl = hasGroup = bloodGroup != null;
        Page<DonorProfile> donors = hasCity && hasGroup ? this.donorRepo.findByBloodGroupAndIsAvailableTrue(bloodGroup, (Pageable)pageable) : (hasGroup ? this.donorRepo.findByBloodGroupAndIsAvailableTrue(bloodGroup, (Pageable)pageable) : (hasCity ? this.donorRepo.findByCityIgnoreCaseAndIsAvailableTrueOrderByReliabilityScoreDesc(city, (Pageable)pageable) : this.donorRepo.findAllByOrderByCreatedAtDesc((Pageable)pageable)));
        return donors.map(this.donorMapper::toDonorSummary);
    }

    @Override
    @Transactional(readOnly=true)
    public DonorSummaryDto getDonorById(Long donorId) {
        DonorProfile donor = this.findDonorOrThrow(donorId);
        return this.donorMapper.toDonorSummary(donor);
    }

    @Override
    @Transactional
    @CacheEvict(value={"adminDashboard", "donorSearch", "donorProfile"}, allEntries=true)
    public DonorSummaryDto resetDonorScore(Long donorId) {
        DonorProfile donor = this.findDonorOrThrow(donorId);
        donor.setReliabilityScore(BigDecimal.valueOf(50.0));
        donor.setUpdatedAt(LocalDateTime.now());
        DonorProfile saved = (DonorProfile)this.donorRepo.save(donor);
        log.info("Donor score reset: donorId={}", (Object)donorId);
        return this.donorMapper.toDonorSummary(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value={"adminDashboard", "donorSearch"}, allEntries=true)
    public DonorSummaryDto verifyDonor(Long donorId) {
        DonorProfile donor = this.findDonorOrThrow(donorId);
        double current = donor.getReliabilityScore() != null ? donor.getReliabilityScore().doubleValue() : 50.0;
        double newScore = Math.min(100.0, current + 20.0);
        donor.setReliabilityScore(BigDecimal.valueOf(newScore));
        donor.setUpdatedAt(LocalDateTime.now());
        DonorProfile saved = (DonorProfile)this.donorRepo.save(donor);
        log.info("Donor verified: donorId={}, newScore={}", (Object)donorId, (Object)newScore);
        return this.donorMapper.toDonorSummary(saved);
    }

    private DonorProfile findDonorOrThrow(Long donorId) {
        return (DonorProfile)this.donorRepo.findById(donorId).orElseThrow(() -> ApiException.notFound("Donor not found: " + donorId));
    }

    public AdminDonorService(DonorProfileRepository donorRepo, AdminUserMapper donorMapper) {
        this.donorRepo = donorRepo;
        this.donorMapper = donorMapper;
    }
}

