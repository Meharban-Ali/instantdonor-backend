package com.blood_donor.service;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.RespondToRequestDto;
import com.blood_donor.model.dto.request.donor_request.mapper.BloodRequestMapper;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.DonorRequestResponse;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import com.blood_donor.service.donor_service_impl.BloodRequestQueryService;
import com.blood_donor.service.donor_service_impl.BloodRequestResponseService;
import com.blood_donor.service.donor_service_impl.ReliabilityScoreService;
import com.blood_donor.service.mapper.DonorMapper;
import com.blood_donor.utils.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Donor PII Masking and Business Rules Unit Tests")
class DonorMapperAndAuditSecurityTest {

    @Test
    @DisplayName("DonorMapper should mask phone number in search results when maskPhone is true")
    void shouldMaskPhoneNumberWhenMaskPhoneIsTrue() {
        DonorMapper mapper = new DonorMapper();

        User user = User.builder()
                .id(1L)
                .name("Test Donor")
                .email("donor@test.com")
                .phone("9876543210")
                .build();

        DonorProfile profile = DonorProfile.builder()
                .id(10L)
                .user(user)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .city("Mumbai")
                .state("Maharashtra")
                .latitude(new BigDecimal("19.0760"))
                .longitude(new BigDecimal("72.8777"))
                .isAvailable(true)
                .reliabilityScore(new BigDecimal("95.00"))
                .responseRate(new BigDecimal("90.00"))
                .totalDonations(5)
                .build();

        DonorResponse maskedResponse = mapper.toResponse(profile, 5.0, true);
        assertThat(maskedResponse.getUser().getPhone()).isEqualTo("98****3210");

        DonorResponse unmaskedResponse = mapper.toResponse(profile, 5.0, false);
        assertThat(unmaskedResponse.getUser().getPhone()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("BloodRequestQueryService should query all active statuses: PENDING, MATCHING, ACTIVE")
    void shouldQueryAllActiveStatuses() {
        BloodRequestRepository requestRepo = mock(BloodRequestRepository.class);
        DonorRequestResponseRepository responseRepo = mock(DonorRequestResponseRepository.class);
        SecurityUtils securityUtils = mock(SecurityUtils.class);
        BloodRequestMapper requestMapper = mock(BloodRequestMapper.class);

        BloodRequestQueryService queryService = new BloodRequestQueryService(
                requestRepo, responseRepo, securityUtils, requestMapper
        );

        when(requestRepo.findByStatusInOrderByCreatedAtDesc(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(requestMapper.toResponsePage(any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        queryService.getActiveRequests(0, 10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BloodRequestStatus>> statusCaptor = ArgumentCaptor.forClass(List.class);
        verify(requestRepo).findByStatusInOrderByCreatedAtDesc(statusCaptor.capture(), any(Pageable.class));

        List<BloodRequestStatus> capturedStatuses = statusCaptor.getValue();
        assertThat(capturedStatuses).containsExactlyInAnyOrder(
                BloodRequestStatus.PENDING,
                BloodRequestStatus.MATCHING,
                BloodRequestStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("BloodRequestResponseService should be idempotent when donor submits identical response status")
    void shouldBeIdempotentOnDuplicateResponse() {
        BloodRequestRepository requestRepo = mock(BloodRequestRepository.class);
        DonorRequestResponseRepository responseRepo = mock(DonorRequestResponseRepository.class);
        DonorProfileRepository donorRepo = mock(DonorProfileRepository.class);
        SecurityUtils securityUtils = mock(SecurityUtils.class);
        ReliabilityScoreService scoreService = mock(ReliabilityScoreService.class);
        BloodRequestMapper requestMapper = mock(BloodRequestMapper.class);

        BloodRequestResponseService responseService = new BloodRequestResponseService(
                requestRepo, responseRepo, donorRepo, securityUtils, scoreService, requestMapper
        );

        Long userId = 100L;
        when(securityUtils.getCurrentUserId()).thenReturn(userId);

        DonorProfile donor = DonorProfile.builder().id(10L).build();
        when(donorRepo.findByUserId(userId)).thenReturn(Optional.of(donor));

        BloodRequest request = BloodRequest.builder()
                .id(50L)
                .status(BloodRequestStatus.ACTIVE)
                .unitsRequired(2)
                .build();
        when(requestRepo.findById(50L)).thenReturn(Optional.of(request));

        DonorRequestResponse existingResponse = DonorRequestResponse.builder()
                .id(1L)
                .donor(donor)
                .request(request)
                .status(ResponseStatus.ACCEPTED)
                .build();
        when(responseRepo.findByDonorProfileIdAndRequestId(10L, 50L))
                .thenReturn(Optional.of(existingResponse));

        RespondToRequestDto dto = new RespondToRequestDto();
        dto.setRequestId(50L);
        dto.setAccept(true);

        responseService.respondToRequest(dto);

        verifyNoInteractions(scoreService);
        verify(responseRepo, never()).updateResponseStatus(any(), any(), any(), any(), any());
    }
}
