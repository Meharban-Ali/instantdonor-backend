/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.notification.service_Impl;

import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.DonorRequestResponse;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationTracker {
    private static final Logger log = LoggerFactory.getLogger(NotificationTracker.class);
    private final DonorRequestResponseRepository responseRepo;

    public void trackNotification(DonorProfile donor, BloodRequest request) {
        try {
            boolean alreadyNotified = this.responseRepo.findByDonorIdAndRequestId(donor.getId(), request.getId()).isPresent();
            if (alreadyNotified) {
                log.debug("Donor already notified: donorId={}, requestId={}", (Object)donor.getId(), (Object)request.getId());
                return;
            }
            DonorRequestResponse record = DonorRequestResponse.builder().donor(donor).request(request).status(ResponseStatus.NOTIFIED).build();
            this.responseRepo.save(record);
            log.debug("Notification tracked: donorId={}, requestId={}", (Object)donor.getId(), (Object)request.getId());
        }
        catch (Exception e) {
            log.error("Failed to track notification: donorId={}, requestId={}, error={}", new Object[]{donor.getId(), request.getId(), e.getMessage()});
        }
    }

    public NotificationTracker(DonorRequestResponseRepository responseRepo) {
        this.responseRepo = responseRepo;
    }
}

