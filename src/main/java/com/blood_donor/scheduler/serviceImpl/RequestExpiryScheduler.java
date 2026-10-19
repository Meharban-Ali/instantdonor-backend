package com.blood_donor.scheduler.serviceImpl;

import com.blood_donor.scheduler.service.ISchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RequestExpiryScheduler {
    private static final Logger log = LoggerFactory.getLogger(RequestExpiryScheduler.class);
    private final ISchedulerService schedulerService;

    @Scheduled(fixedDelayString="${app.scheduler.expiry-interval-ms:300000}")
    @CacheEvict(value={"activeRequests"}, allEntries=true)
    public void expireOldRequests() {
        log.debug("Request expiry scheduler triggered");
        try {
            int expired = this.schedulerService.expireOldRequests();
            if (expired > 0) {
                log.info("Request expiry scheduler: {} requests expired", (Object)expired);
            }
        }
        catch (Exception e) {
            log.error("Request expiry scheduler failed: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    @Scheduled(fixedDelay=600000L)
    public void markNoResponseDonors() {
        log.debug("No-response scheduler triggered");
        try {
            int marked = this.schedulerService.markNoResponseDonors();
            if (marked > 0) {
                log.info("No-response scheduler: {} donors marked", (Object)marked);
            }
        }
        catch (Exception e) {
            log.error("No-response scheduler failed: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    public RequestExpiryScheduler(ISchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}

