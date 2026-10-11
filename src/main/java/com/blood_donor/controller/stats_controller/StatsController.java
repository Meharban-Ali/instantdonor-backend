/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.swagger.v3.oas.annotations.Operation
 *  io.swagger.v3.oas.annotations.tags.Tag
 *  org.springframework.cache.annotation.Cacheable
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.blood_donor.controller.stats_controller;

import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import com.blood_donor.model.dto.stats.PublicStatsDto;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.DonationHistoryRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/stats"})
@Tag(name="Stats", description="Public platform statistics")
public class StatsController {
    private final UserRepository userRepository;
    private final DonorProfileRepository donorRepository;
    private final BloodRequestRepository requestRepository;
    private final DonationHistoryRepository historyRepository;

    @GetMapping(value={"/public"})
    @Cacheable(value={"publicStats"}, key="'stats'")
    @Operation(summary="Get public platform statistics", description="Returns donor count, donations, requests etc. No auth needed.")
    public ApiResponse<PublicStatsDto> getPublicStats() {
        long totalDonors = this.donorRepository.count();
        long availableDonors = this.donorRepository.countByIsAvailableTrue();
        long totalDonations = this.historyRepository.count();
        long activeRequests = this.requestRepository.countByStatusIn(List.of(BloodRequestStatus.PENDING, BloodRequestStatus.MATCHING, BloodRequestStatus.ACTIVE));
        long fulfilledRequests = this.requestRepository.countByStatus(BloodRequestStatus.FULFILLED);
        long totalRequests = this.requestRepository.count();
        double fulfillmentRate = totalRequests > 0L ? (double)Math.round((double)fulfilledRequests * 100.0 / (double)totalRequests * 10.0) / 10.0 : 0.0;
        PublicStatsDto stats = PublicStatsDto.builder().totalDonors(totalDonors).availableDonors(availableDonors).totalDonations(totalDonations).activeRequests(activeRequests).fulfilledRequests(fulfilledRequests).fulfillmentRate(fulfillmentRate).build();
        return ApiResponse.success("Stats fetched", stats);
    }

    public StatsController(UserRepository userRepository, DonorProfileRepository donorRepository, BloodRequestRepository requestRepository, DonationHistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.requestRepository = requestRepository;
        this.historyRepository = historyRepository;
    }
}

