/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.scheduler.serviceImpl;

import com.blood_donor.scheduler.service.ISchedulerService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupScheduler {
    private static final Logger log = LoggerFactory.getLogger(CleanupScheduler.class);
    private final ISchedulerService schedulerService;

    @Scheduled(cron="0 0 2 * * *")
    public void cleanupOldRequests() {
        log.info("Cleanup scheduler started");
        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(30L);
            int deleted = this.schedulerService.cleanOldRequests(threshold);
            log.info("Cleanup scheduler completed: deleted {} old requests", (Object)deleted);
        }
        catch (Exception e) {
            log.error("Cleanup scheduler failed: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    public CleanupScheduler(ISchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}

