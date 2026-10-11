/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.domain.Page
 *  org.springframework.data.domain.PageImpl
 *  org.springframework.data.domain.Pageable
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.model.dto.request.donor_request.mapper;

import com.blood_donor.model.dto.request.donor_request.RequestBloodInfo;
import com.blood_donor.model.dto.request.donor_request.RequestHospitalInfo;
import com.blood_donor.model.dto.request.donor_request.RequestPatientInfo;
import com.blood_donor.model.dto.request.donor_request.RequestTimeInfo;
import com.blood_donor.model.dto.request.donor_request.RequestTrackingInfo;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class BloodRequestMapper {
    private final DonorRequestResponseRepository responseRepo;

    public BloodRequestResponse toResponse(BloodRequest request) {
        long minutesRemaining = this.calculateMinutesRemaining(request);
        long notified = this.responseRepo.countByRequestIdAndStatus(request.getId(), ResponseStatus.NOTIFIED);
        long accepted = this.responseRepo.countByRequestIdAndStatus(request.getId(), ResponseStatus.ACCEPTED);
        double fulfillmentPct = request.getUnitsRequired() > 0 ? (double)accepted * 100.0 / (double)request.getUnitsRequired().intValue() : 0.0;
        return BloodRequestResponse.builder().id(request.getId()).status(request.getStatus()).patient(RequestPatientInfo.builder().patientName(request.getPatientName()).contactPhone(request.getContactPhone()).createdById(request.getCreatedBy().getId()).createdByName(request.getCreatedBy().getName()).build()).hospital(RequestHospitalInfo.builder().hospitalName(request.getHospitalName()).city(request.getCity()).latitude(request.getLatitude()).longitude(request.getLongitude()).build()).blood(RequestBloodInfo.builder().bloodGroup(request.getBloodGroup()).bloodGroupDisplay(request.getBloodGroup().getDisplayName()).urgency(request.getUrgency()).unitsRequired(request.getUnitsRequired()).additionalInfo(request.getAdditionalInfo()).build()).tracking(RequestTrackingInfo.builder().notifiedDonors(notified).acceptedDonors(accepted).fulfillmentPercentage(fulfillmentPct).isFulfilled(request.getStatus().isTerminal() && accepted >= (long)request.getUnitsRequired().intValue()).build()).time(RequestTimeInfo.builder().createdAt(request.getCreatedAt()).expiredAt(request.getExpiredAt()).fulfilledAt(request.getFulfilledAt()).minutesRemaining(Math.max(0L, minutesRemaining)).build()).build();
    }

    private long calculateMinutesRemaining(BloodRequest request) {
        if (request.getExpiredAt() == null) {
            return 0L;
        }
        return Math.max(0L, ChronoUnit.MINUTES.between(LocalDateTime.now(), request.getExpiredAt()));
    }

    public Page<BloodRequestResponse> toResponsePage(Page<BloodRequest> requestPage) {
        List<BloodRequest> requests = requestPage.getContent();
        if (requests.isEmpty()) {
            return Page.empty((Pageable)requestPage.getPageable());
        }
        List<Long> requestIds = requests.stream().map(BloodRequest::getId).toList();
        List<Object[]> rawCounts = this.responseRepo.countStatusesForRequestIds(requestIds);
        HashMap<Long, Map> countsMap = new HashMap<Long, Map>();
        for (Object[] row : rawCounts) {
            Long reqId = (Long)row[0];
            ResponseStatus status = (ResponseStatus)((Object)row[1]);
            Long count = (Long)row[2];
            countsMap.computeIfAbsent(reqId, k -> new HashMap()).put(status, count);
        }
        List<BloodRequestResponse> dtos = requests.stream().map(r -> this.toResponseWithCounts(r, countsMap.getOrDefault(r.getId(), Map.of()))).toList();
        return new PageImpl<>(dtos, requestPage.getPageable(), requestPage.getTotalElements());
    }

    private BloodRequestResponse toResponseWithCounts(BloodRequest request, Map<ResponseStatus, Long> counts) {
        long minutesRemaining = this.calculateMinutesRemaining(request);
        long notified = counts.getOrDefault((Object)ResponseStatus.NOTIFIED, 0L);
        long accepted = counts.getOrDefault((Object)ResponseStatus.ACCEPTED, 0L);
        double fulfillmentPct = request.getUnitsRequired() > 0 ? (double)accepted * 100.0 / (double)request.getUnitsRequired().intValue() : 0.0;
        return BloodRequestResponse.builder().id(request.getId()).status(request.getStatus()).patient(RequestPatientInfo.builder().patientName(request.getPatientName()).contactPhone(request.getContactPhone()).createdById(request.getCreatedBy().getId()).createdByName(request.getCreatedBy().getName()).build()).hospital(RequestHospitalInfo.builder().hospitalName(request.getHospitalName()).city(request.getCity()).latitude(request.getLatitude()).longitude(request.getLongitude()).build()).blood(RequestBloodInfo.builder().bloodGroup(request.getBloodGroup()).bloodGroupDisplay(request.getBloodGroup().getDisplayName()).urgency(request.getUrgency()).unitsRequired(request.getUnitsRequired()).additionalInfo(request.getAdditionalInfo()).build()).tracking(RequestTrackingInfo.builder().notifiedDonors(notified).acceptedDonors(accepted).fulfillmentPercentage(fulfillmentPct).isFulfilled(request.getStatus().isTerminal() && accepted >= (long)request.getUnitsRequired().intValue()).build()).time(RequestTimeInfo.builder().createdAt(request.getCreatedAt()).expiredAt(request.getExpiredAt()).fulfilledAt(request.getFulfilledAt()).minutesRemaining(Math.max(0L, minutesRemaining)).build()).build();
    }

    public BloodRequestMapper(DonorRequestResponseRepository responseRepo) {
        this.responseRepo = responseRepo;
    }
}

