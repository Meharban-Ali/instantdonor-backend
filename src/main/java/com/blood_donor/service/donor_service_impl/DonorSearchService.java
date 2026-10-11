/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.cache.annotation.Cacheable
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.PageRequest
 *  org.springframework.data.domain.Pageable
 *  org.springframework.data.domain.Sort
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.service.donor_service_impl;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.donor_request.DonorSearchRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.service.donor_service.IDonorSearchService;
import com.blood_donor.service.mapper.DonorMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorSearchService
implements IDonorSearchService {
    private static final Logger log = LoggerFactory.getLogger(DonorSearchService.class);
    private final DonorProfileRepository donorRepo;
    private final DonorMapper donorMapper;

    @Override
    @Transactional(readOnly=true)
    @Cacheable(value={"donorSearch"}, key="#req.bloodGroup.name() + '_city_' + #req.city + '_' + #req.page + '_' + #req.size")
    public Page<DonorResponse> searchByCity(DonorSearchRequest req) {
        if (!req.isCitySearch()) {
            throw ApiException.badRequest("City is required for city-based search");
        }
        List<BloodGroup> compatibleGroups = BloodGroup.getCompatibleDonors(req.getBloodGroup());
        PageRequest pageable = PageRequest.of((int)req.getPage(), (int)req.getSize(), (Sort)Sort.by((String[])new String[]{"reliabilityScore"}).descending());
        Page<DonorProfile> donors = this.donorRepo.findByBloodGroupInAndCityIgnoreCaseAndIsAvailableTrue(compatibleGroups, req.getCity(), (Pageable)pageable);
        log.info("City search: city={}, bloodGroup={}, found={}", new Object[]{req.getCity(), req.getBloodGroup(), donors.getTotalElements()});
        return donors.map(donor -> this.donorMapper.toResponse((DonorProfile)donor, null));
    }

    @Override
    @Transactional(readOnly=true)
    @Cacheable(value={"donorSearch"}, key="#req.bloodGroup.name() + '_radius_' + #req.radiusSearch.latitude + '_' + #req.radiusSearch.longitude + '_' + #req.radiusSearch.radiusKm")
    public List<DonorResponse> searchByLocation(DonorSearchRequest req) {
        if (!req.isRadiusSearch()) {
            throw ApiException.badRequest("Latitude and longitude required for location search");
        }
        List<String> compatibleGroupNames = BloodGroup.getCompatibleDonors(req.getBloodGroup()).stream().map(Enum::name).collect(Collectors.toList());
        List<DonorProfile> donors = this.donorRepo.findNearByCompatibleDonors(req.getRadiusSearch().getLatitude(), req.getRadiusSearch().getLongitude(), req.getRadiusSearch().getRadiusKm(), compatibleGroupNames, req.getSize());
        log.info("Radius search: lat={}, lng={}, radius={}km, found={}", new Object[]{req.getRadiusSearch().getLatitude(), req.getRadiusSearch().getLongitude(), req.getRadiusSearch().getRadiusKm(), donors.size()});
        return donors.stream().map(donor -> {
            Double distanceKm = this.donorRepo.calculateDistanceKm(req.getRadiusSearch().getLatitude(), req.getRadiusSearch().getLongitude(), donor.getLatitude().doubleValue(), donor.getLongitude().doubleValue());
            return this.donorMapper.toResponse((DonorProfile)donor, distanceKm);
        }).collect(Collectors.toList());
    }

    public DonorSearchService(DonorProfileRepository donorRepo, DonorMapper donorMapper) {
        this.donorRepo = donorRepo;
        this.donorMapper = donorMapper;
    }
}

