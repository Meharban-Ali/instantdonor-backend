/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Async
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.service.auth_service;

import com.blood_donor.model.entity.AuditLog;
import com.blood_donor.repository.user_repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {
    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);
    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String action, String details, String performedBy, String clientIp) {
        try {
            AuditLog auditLog = AuditLog.builder().action(action).details(details).performedBy(performedBy).clientIp(clientIp).build();
            this.auditLogRepository.save(auditLog);
            log.debug("Audit log saved: action={}, performedBy={}", (Object)action, (Object)performedBy);
        }
        catch (Exception e) {
            log.error("Failed to save audit log: action={}, error={}", new Object[]{action, e.getMessage(), e});
        }
    }

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
}

