package com.blood_donor.scheduler.serviceImpl;

import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.DonorRequestResponse;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import com.blood_donor.scheduler.service.ISchedulerService;
import com.blood_donor.service.donor_service_impl.ReliabilityScoreService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchedulerService
implements ISchedulerService {
    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
    private final BloodRequestRepository requestRepo;
    private final DonorRequestResponseRepository responseRepo;
    private final DonorProfileRepository donorRepo;
    private final ReliabilityScoreService scoreService;
    @Value(value="${app.scheduler.no-response-timeout-minutes:60}")
    private int noResponseTimeoutMinutes;

    @Override
    @Transactional
    public int expireOldRequests() {
        int count = this.requestRepo.bulkExpireRequests(List.of(BloodRequestStatus.PENDING, BloodRequestStatus.MATCHING, BloodRequestStatus.ACTIVE), BloodRequestStatus.EXPIRED, LocalDateTime.now());
        if (count > 0) {
            log.info("Expired {} blood requests", (Object)count);
        }
        return count;
    }

    @Override
    @Transactional
    public int markNoResponseDonors() {
        LocalDateTime timeout = LocalDateTime.now().minus(this.noResponseTimeoutMinutes, ChronoUnit.MINUTES);
        List<DonorRequestResponse> pendingResponses = this.responseRepo.findPendingResponsesBefore(timeout);
        if (pendingResponses.isEmpty()) {
            return 0;
        }
        int marked = 0;
        for (DonorRequestResponse response : pendingResponses) {
            try {
                response.markAsNoResponse();
                this.responseRepo.save(response);
                DonorProfile donor = response.getDonor();
                this.scoreService.onNoResponse(donor);
                ++marked;
            }
            catch (Exception e) {
                log.error("Failed to mark no-response: responseId={}", (Object)response.getId(), (Object)e);
            }
        }
        if (marked > 0) {
            log.info("Marked {} donors as NO_RESPONSE", (Object)marked);
        }
        return marked;
    }

    @Override
    @Transactional
    public int cleanOldRequests(LocalDateTime threshold) {
        List<BloodRequestStatus> terminalStatuses = List.of(BloodRequestStatus.EXPIRED, BloodRequestStatus.CANCELLED);
        int deletedResponses = this.responseRepo.deleteByRequestCreatedAtBeforeAndStatusIn(threshold, terminalStatuses);
        int deletedRequests = this.requestRepo.deleteByCreatedAtBeforeAndStatusIn(threshold, terminalStatuses);
        log.info("\ud83e\uddf9 Cleaned old terminal requests: deleted {} requests and {} response logs older than {}", new Object[]{deletedRequests, deletedResponses, threshold});
        return deletedRequests;
    }

    public SchedulerService(BloodRequestRepository requestRepo, DonorRequestResponseRepository responseRepo, DonorProfileRepository donorRepo, ReliabilityScoreService scoreService) {
        this.requestRepo = requestRepo;
        this.responseRepo = responseRepo;
        this.donorRepo = donorRepo;
        this.scoreService = scoreService;
    }
}

