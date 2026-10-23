package com.blood_donor.service.donor_service_impl;

import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.RespondToRequestDto;
import com.blood_donor.model.dto.request.donor_request.mapper.BloodRequestMapper;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.DonorRequestResponse;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import com.blood_donor.service.donor_service.IBloodRequestResponseService;
import com.blood_donor.service.donor_service_impl.ReliabilityScoreService;
import com.blood_donor.utils.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BloodRequestResponseService
implements IBloodRequestResponseService {
    private static final Logger log = LoggerFactory.getLogger(BloodRequestResponseService.class);
    private final BloodRequestRepository requestRepo;
    private final DonorRequestResponseRepository responseRepo;
    private final DonorProfileRepository donorRepo;
    private final SecurityUtils securityUtils;
    private final ReliabilityScoreService scoreService;
    private final BloodRequestMapper requestMapper;

    @Override
    @Transactional
    @CacheEvict(value={"activeRequests"}, allEntries=true)
    public BloodRequestResponse respondToRequest(RespondToRequestDto dto) {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorProfile donor = this.donorRepo.findByUserId(userId).orElseThrow(() -> ApiException.badRequest("Please register as donor first"));
        BloodRequest request = this.requestRepo.findByIdForUpdate(dto.getRequestId()).orElseThrow(() -> ApiException.notFound("Blood request not found"));
        
        if (request.getCreatedBy() != null && request.getCreatedBy().getId().equals(userId)) {
            throw ApiException.badRequest("You cannot accept or respond to your own blood request");
        }

        if (request.getStatus().isTerminal()) {
            throw ApiException.badRequest("Cannot respond \u2014 request is: " + request.getStatus());
        }
        boolean isAccepted = Boolean.TRUE.equals(dto.getAccept());
        Optional<DonorRequestResponse> existingResponse = this.responseRepo.findByDonorProfileIdAndRequestId(donor.getId(), request.getId());
        ResponseStatus prevStatus = existingResponse.map(DonorRequestResponse::getStatus).orElse(null);
        ResponseStatus newStatus = isAccepted ? ResponseStatus.ACCEPTED : ResponseStatus.REJECTED;

        if (prevStatus == newStatus) {
            log.info("Donor response unchanged: donorId={}, requestId={}, status={}", donor.getId(), request.getId(), newStatus);
            return this.requestMapper.toResponse(request);
        }

        if (existingResponse.isPresent()) {
            this.responseRepo.updateResponseStatus(donor.getId(), request.getId(), newStatus, LocalDateTime.now(), dto.getNotes());
        } else {
            DonorRequestResponse response = DonorRequestResponse.builder().donor(donor).request(request).notes(dto.getNotes()).build();
            if (isAccepted) {
                response.markAsAccepted();
            } else {
                response.markAsRejected(dto.getNotes());
            }
            this.responseRepo.save(response);
        }
        if (isAccepted) {
            this.checkAndUpdateFulfillment(request);
            this.scoreService.onAccept(donor);
        } else {
            this.scoreService.onReject(donor);
        }
        log.info("Donor responded: donorId={}, requestId={}, accepted={}", new Object[]{donor.getId(), request.getId(), isAccepted});
        return this.requestMapper.toResponse(request);
    }

    private void checkAndUpdateFulfillment(BloodRequest request) {
        long acceptedCount = this.responseRepo.countByRequestIdAndStatus(request.getId(), ResponseStatus.ACCEPTED);
        if (acceptedCount >= (long)request.getUnitsRequired().intValue()) {
            this.requestRepo.updateStatus(request.getId(), BloodRequestStatus.FULFILLED, LocalDateTime.now());
            request.setStatus(BloodRequestStatus.FULFILLED);
            request.setFulfilledAt(LocalDateTime.now());

            java.util.List<DonorRequestResponse> accepted = this.responseRepo.findByRequestIdAndStatus(request.getId(), ResponseStatus.ACCEPTED);
            for (DonorRequestResponse resp : accepted) {
                DonorProfile donor = resp.getDonor();
                if (donor != null) {
                    donor.recordDonation();
                    this.donorRepo.save(donor);
                    log.info("Recorded donation on auto fulfill: donorId={}, totalDonations={}, nextEligibleDate={}",
                            donor.getId(), donor.getTotalDonations(), donor.getNextEligibleDate());
                }
            }

            log.info("Request FULFILLED: id={}, acceptedDonors={}", (Object)request.getId(), (Object)acceptedCount);
        } else {
            this.requestRepo.updateStatus(request.getId(), BloodRequestStatus.MATCHING, LocalDateTime.now());
            request.setStatus(BloodRequestStatus.MATCHING);
        }
    }

    public BloodRequestResponseService(BloodRequestRepository requestRepo, DonorRequestResponseRepository responseRepo, DonorProfileRepository donorRepo, SecurityUtils securityUtils, ReliabilityScoreService scoreService, BloodRequestMapper requestMapper) {
        this.requestRepo = requestRepo;
        this.responseRepo = responseRepo;
        this.donorRepo = donorRepo;
        this.securityUtils = securityUtils;
        this.scoreService = scoreService;
        this.requestMapper = requestMapper;
    }
}

