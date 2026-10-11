/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.blood_donor.service.donor_service_impl;

import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReliabilityScoreService {
    private static final Logger log = LoggerFactory.getLogger(ReliabilityScoreService.class);
    private final DonorProfileRepository donorRepo;
    private final DonorRequestResponseRepository responseRepo;
    private static final double ACCEPT_DELTA = 2.0;
    private static final double REJECT_DELTA = -1.0;
    private static final double NO_RESPONSE_DELTA = -3.0;
    private static final double MIN_SCORE = 0.0;
    private static final double MAX_SCORE = 100.0;

    @Transactional
    public void onAccept(DonorProfile donor) {
        this.updateScore(donor, 2.0);
        this.updateResponseRate(donor);
    }

    @Transactional
    public void onReject(DonorProfile donor) {
        this.updateScore(donor, -1.0);
        this.updateResponseRate(donor);
    }

    @Transactional
    public void onNoResponse(DonorProfile donor) {
        this.updateScore(donor, -3.0);
        this.updateResponseRate(donor);
    }

    private void updateScore(DonorProfile donor, double delta) {
        double current = donor.getReliabilityScore().doubleValue();
        double updated = Math.max(0.0, Math.min(100.0, current + delta));
        donor.setReliabilityScore(BigDecimal.valueOf(updated).setScale(2, RoundingMode.HALF_UP));
        this.donorRepo.save(donor);
        log.info("Reliability score updated: donorId={}, delta={}, new={}", new Object[]{donor.getId(), delta, updated});
    }

    private void updateResponseRate(DonorProfile donor) {
        Double rate = this.responseRepo.calculateResponseRate(donor.getId());
        double safeRate = rate != null ? rate : 0.0;
        donor.setResponseRate(BigDecimal.valueOf(safeRate).setScale(2, RoundingMode.HALF_UP));
        this.donorRepo.save(donor);
    }

    public ReliabilityScoreService(DonorProfileRepository donorRepo, DonorRequestResponseRepository responseRepo) {
        this.donorRepo = donorRepo;
        this.responseRepo = responseRepo;
    }
}

