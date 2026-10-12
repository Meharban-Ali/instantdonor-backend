package com.blood_donor.service.donor_service_impl;

import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.config.properties.RequestExpiryProperties;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.dto.request.donor_request.mapper.BloodRequestMapper;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.service.donor_service.IBloodRequestCreationService;
import com.blood_donor.utils.SecurityUtils;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BloodRequestCreationService
implements IBloodRequestCreationService {
    private static final Logger log = LoggerFactory.getLogger(BloodRequestCreationService.class);
    private final BloodRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final SecurityUtils securityUtils;
    private final BloodRequestMapper requestMapper;
    private final RequestExpiryProperties expiryProperties;
    @Value(value="${app.request.expiry-hours-normal:48}")
    private int expiryHoursNormal;
    @Value(value="${app.request.expiry-hours-urgent:24}")
    private int expiryHoursUrgent;
    @Value(value="${app.request.expiry-hours-critical:6}")
    private int expiryHoursCritical;

    @Override
    @Transactional
    @CacheEvict(value={"activeRequests"}, allEntries=true)
    public BloodRequestResponse createRequest(CreateBloodRequestDto dto) {
        Long userId = this.securityUtils.getCurrentUserId();
        User currentUser = (User)this.userRepo.findById(userId).orElseThrow(() -> ApiException.notFound("User not found"));

        java.util.List<BloodRequestStatus> activeStatuses = java.util.List.of(BloodRequestStatus.PENDING, BloodRequestStatus.ACTIVE, BloodRequestStatus.MATCHING);
        if (this.requestRepo.existsByCreatedByIdAndStatusInAndBloodGroup(userId, activeStatuses, dto.getBloodGroup())) {
            throw ApiException.badRequest("You already have an active blood request for blood group " + dto.getBloodGroup().getDisplayName());
        }

        int expiryHours = this.getExpiryHours(dto.getUrgency());
        BloodRequest request = BloodRequest.builder().patientName(dto.getPatientName()).bloodGroup(dto.getBloodGroup()).hospitalName(dto.getHospitalName()).city(dto.getCity()).latitude(dto.getLatitude()).longitude(dto.getLongitude()).unitsRequired(dto.getUnitsRequired()).urgency(dto.getUrgency()).contactPhone(dto.getContactPhone()).additionalInfo(dto.getAdditionalInfo()).status(BloodRequestStatus.PENDING).createdBy(currentUser).expiredAt(LocalDateTime.now().plusHours(expiryHours)).build();
        BloodRequest saved = (BloodRequest)this.requestRepo.save(request);
        log.info("Blood request created: id={}, bloodGroup={}, city={}, urgency={}", new Object[]{saved.getId(), saved.getBloodGroup(), saved.getCity(), saved.getUrgency()});
        return this.requestMapper.toResponse(saved);
    }

    private int getExpiryHours(UrgencyLevel urgency) {
        return this.expiryProperties.getExpiryHoursForUrgency(urgency.name());
    }

    @Override
    @Transactional
    @CacheEvict(value={"activeRequests"}, allEntries=true)
    public BloodRequestResponse markFulfilled(Long requestId) {
        BloodRequest request = this.findRequestById(requestId);
        Long currentUserId = this.securityUtils.getCurrentUserId();
        boolean isOwner = request.getCreatedBy().getId().equals(currentUserId);
        boolean isAdmin = this.userRepo.findById(currentUserId).map(u -> u.hasRole(Role.ADMIN)).orElse(false);
        if (!isOwner && !isAdmin) {
            throw ApiException.forbidden("You can only fulfill your own requests");
        }
        if (request.getStatus().isTerminal()) {
            throw ApiException.badRequest("Request is already in terminal state: " + request.getStatus());
        }
        request.setStatus(BloodRequestStatus.FULFILLED);
        request.setFulfilledAt(LocalDateTime.now());
        BloodRequest saved = (BloodRequest)this.requestRepo.save(request);
        log.info("Request manually marked FULFILLED: id={}", (Object)requestId);
        return this.requestMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value={"activeRequests"}, allEntries=true)
    public BloodRequestResponse cancelRequest(Long requestId) {
        BloodRequest request = this.findRequestById(requestId);
        Long currentUserId = this.securityUtils.getCurrentUserId();
        boolean isOwner = request.getCreatedBy().getId().equals(currentUserId);
        boolean isAdmin = this.userRepo.findById(currentUserId).map(u -> u.hasRole(Role.ADMIN)).orElse(false);
        if (!isOwner && !isAdmin) {
            throw ApiException.forbidden("You can only cancel your own requests");
        }
        if (request.getStatus().isTerminal()) {
            throw ApiException.badRequest("Cannot cancel \u2014 already: " + request.getStatus());
        }
        request.setStatus(BloodRequestStatus.CANCELLED);
        log.info("Request cancelled: id={}", (Object)requestId);
        return this.requestMapper.toResponse(request);
    }

    private BloodRequest findRequestById(Long id) {
        return (BloodRequest)this.requestRepo.findById(id).orElseThrow(() -> ApiException.notFound("Blood request not found: " + id));
    }

    public BloodRequestRepository getRequestRepo() {
        return this.requestRepo;
    }

    public UserRepository getUserRepo() {
        return this.userRepo;
    }

    public SecurityUtils getSecurityUtils() {
        return this.securityUtils;
    }

    public BloodRequestMapper getRequestMapper() {
        return this.requestMapper;
    }

    public RequestExpiryProperties getExpiryProperties() {
        return this.expiryProperties;
    }

    public int getExpiryHoursNormal() {
        return this.expiryHoursNormal;
    }

    public int getExpiryHoursUrgent() {
        return this.expiryHoursUrgent;
    }

    public int getExpiryHoursCritical() {
        return this.expiryHoursCritical;
    }

    public BloodRequestCreationService(BloodRequestRepository requestRepo, UserRepository userRepo, SecurityUtils securityUtils, BloodRequestMapper requestMapper, RequestExpiryProperties expiryProperties) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.securityUtils = securityUtils;
        this.requestMapper = requestMapper;
        this.expiryProperties = expiryProperties;
    }
}

