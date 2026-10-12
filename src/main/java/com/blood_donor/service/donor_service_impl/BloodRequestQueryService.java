package com.blood_donor.service.donor_service_impl;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.mapper.BloodRequestMapper;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import com.blood_donor.service.donor_service.IBloodRequestQueryService;
import com.blood_donor.utils.SecurityUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class BloodRequestQueryService
implements IBloodRequestQueryService {
    private static final Logger log = LoggerFactory.getLogger(BloodRequestQueryService.class);
    private final BloodRequestRepository requestRepo;
    private final DonorRequestResponseRepository responseRepo;
    private final SecurityUtils securityUtils;
    private final BloodRequestMapper requestMapper;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_SIZE = 10;
    private static final List<BloodRequestStatus> ACTIVE_STATUSES = List.of(BloodRequestStatus.PENDING, BloodRequestStatus.MATCHING, BloodRequestStatus.ACTIVE);

    @Override
    @Transactional(readOnly=true)
    @Cacheable(value={"activeRequests"}, key="'active_' + #page + '_' + #size")
    public Page<BloodRequestResponse> getActiveRequests(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        PageRequest pageable = PageRequest.of((int)safePage, (int)safeSize, (Sort)Sort.by((String[])new String[]{"urgency"}).descending().and(Sort.by((String[])new String[]{"createdAt"}).descending()));
        Page<BloodRequest> requestPage = this.requestRepo.findByStatusInOrderByCreatedAtDesc(ACTIVE_STATUSES, (Pageable)pageable);
        return this.requestMapper.toResponsePage(requestPage);
    }

    @Override
    @Transactional(readOnly=true)
    @Cacheable(value={"activeRequests"}, key="'search_'+ (#city     ?: 'all') + '_'+ (#bloodGroup ?: 'all') + '_'+ #page")
    public Page<BloodRequestResponse> searchRequests(String city, BloodGroup bloodGroup, int page, int size) {
        boolean hasGroup;
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        PageRequest pageable = PageRequest.of((int)safePage, (int)safeSize, (Sort)Sort.by((String[])new String[]{"urgency"}).descending().and(Sort.by((String[])new String[]{"createdAt"}).descending()));
        boolean hasCity = city != null && !city.isBlank();
        boolean bl = hasGroup = bloodGroup != null;
        Page<BloodRequest> requests = hasCity && hasGroup ? this.requestRepo.findByStatusInAndCityIgnoreCaseAndBloodGroup(ACTIVE_STATUSES, city.trim(), bloodGroup, (Pageable)pageable) : (hasCity ? this.requestRepo.findByStatusInAndCityIgnoreCase(ACTIVE_STATUSES, city.trim(), (Pageable)pageable) : (hasGroup ? this.requestRepo.findByStatusInAndBloodGroup(ACTIVE_STATUSES, bloodGroup, (Pageable)pageable) : this.requestRepo.findByStatusInOrderByCreatedAtDesc(ACTIVE_STATUSES, (Pageable)pageable)));
        log.info("Search: city={}, bloodGroup={}, page={}, found={}", new Object[]{city, bloodGroup, safePage, requests.getTotalElements()});
        return this.requestMapper.toResponsePage(requests);
    }

    @Override
    @Transactional(readOnly=true)
    public BloodRequestResponse getRequestById(Long requestId) {
        if (requestId == null || requestId <= 0L) {
            throw ApiException.badRequest("Invalid request ID: " + requestId);
        }
        BloodRequest request = (BloodRequest)this.requestRepo.findById(requestId).orElseThrow(() -> ApiException.notFound("Request not found: " + requestId));
        return this.requestMapper.toResponse(request);
    }

    @Override
    @Transactional(readOnly=true)
    public Page<BloodRequestResponse> getMyRequests(int page, int size) {
        Long userId = this.securityUtils.getCurrentUserId();
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        PageRequest pageable = PageRequest.of((int)safePage, (int)safeSize, (Sort)Sort.by((String[])new String[]{"createdAt"}).descending());
        log.debug("getMyRequests: userId={}", (Object)userId);
        Page<BloodRequest> requestPage = this.requestRepo.findByCreatedByIdOrderByCreatedAtDesc(userId, (Pageable)pageable);
        return this.requestMapper.toResponsePage(requestPage);
    }

    @Override
    @Transactional(readOnly=true)
    public Optional<BloodRequest> findActiveById(Long requestId) {
        if (requestId == null || requestId <= 0L) {
            log.warn("findActiveById: invalid requestId={}", (Object)requestId);
            return Optional.empty();
        }
        Optional result = this.requestRepo.findById(requestId);
        if (result.isEmpty()) {
            log.debug("findActiveById: not found requestId={}", (Object)requestId);
        }
        return result;
    }

    @Override
    @Transactional(readOnly=true)
    public Map<String, Object> getNotificationStatus(Long requestId) {
        if (requestId == null || requestId <= 0L) {
            throw ApiException.badRequest("Invalid request ID: " + requestId);
        }
        if (!this.requestRepo.existsById(requestId)) {
            throw ApiException.notFound("Blood request not found: " + requestId);
        }
        long notifiedCount = this.responseRepo.countByRequestIdAndStatus(requestId, ResponseStatus.NOTIFIED);
        long acceptedCount = this.responseRepo.countByRequestIdAndStatus(requestId, ResponseStatus.ACCEPTED);
        long rejectedCount = this.responseRepo.countByRequestIdAndStatus(requestId, ResponseStatus.REJECTED);
        long pendingCount = Math.max(0L, notifiedCount - acceptedCount - rejectedCount);
        log.debug("Notification status: requestId={}, notified={}, accepted={}, rejected={}, pending={}", new Object[]{requestId, notifiedCount, acceptedCount, rejectedCount, pendingCount});
        LinkedHashMap<String, Object> status = new LinkedHashMap<String, Object>();
        status.put("requestId", requestId);
        status.put("notified", notifiedCount);
        status.put("accepted", acceptedCount);
        status.put("rejected", rejectedCount);
        status.put("pending", pendingCount);
        status.put("responseRate", notifiedCount > 0L ? Math.round((double)(acceptedCount + rejectedCount) / (double)notifiedCount * 100.0) : 0L);
        return status;
    }

    public BloodRequestQueryService(BloodRequestRepository requestRepo, DonorRequestResponseRepository responseRepo, SecurityUtils securityUtils, BloodRequestMapper requestMapper) {
        this.requestRepo = requestRepo;
        this.responseRepo = responseRepo;
        this.securityUtils = securityUtils;
        this.requestMapper = requestMapper;
    }
}

