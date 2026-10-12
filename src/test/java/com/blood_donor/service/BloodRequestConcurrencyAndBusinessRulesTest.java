package com.blood_donor.service;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.config.properties.RequestExpiryProperties;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.dto.request.donor_request.RespondToRequestDto;
import com.blood_donor.model.dto.request.donor_request.mapper.BloodRequestMapper;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.DonorRequestResponse;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.service.donor_service_impl.BloodRequestCreationService;
import com.blood_donor.service.donor_service_impl.BloodRequestResponseService;
import com.blood_donor.service.donor_service_impl.ReliabilityScoreService;
import com.blood_donor.utils.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Blood Request Concurrency, Self-Accept, and Duplicate Checks")
class BloodRequestConcurrencyAndBusinessRulesTest {

    @Test
    @DisplayName("Self-Accept Prevention: Requester cannot respond to or accept their own request")
    void shouldPreventRequesterFromAcceptingOwnBloodRequest() {
        BloodRequestRepository requestRepo = mock(BloodRequestRepository.class);
        DonorRequestResponseRepository responseRepo = mock(DonorRequestResponseRepository.class);
        DonorProfileRepository donorRepo = mock(DonorProfileRepository.class);
        SecurityUtils securityUtils = mock(SecurityUtils.class);
        ReliabilityScoreService scoreService = mock(ReliabilityScoreService.class);
        BloodRequestMapper requestMapper = mock(BloodRequestMapper.class);

        BloodRequestResponseService service = new BloodRequestResponseService(
                requestRepo, responseRepo, donorRepo, securityUtils, scoreService, requestMapper
        );

        Long currentUserId = 50L;
        when(securityUtils.getCurrentUserId()).thenReturn(currentUserId);

        User requester = User.builder().id(currentUserId).name("Requester User").email("req@test.com").build();
        DonorProfile requesterAsDonor = DonorProfile.builder().id(10L).user(requester).build();
        when(donorRepo.findByUserId(currentUserId)).thenReturn(Optional.of(requesterAsDonor));

        BloodRequest request = BloodRequest.builder()
                .id(1L)
                .createdBy(requester)
                .status(BloodRequestStatus.PENDING)
                .unitsRequired(1)
                .build();
        when(requestRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(request));

        RespondToRequestDto dto = new RespondToRequestDto();
        dto.setRequestId(1L);
        dto.setAccept(true);

        assertThatThrownBy(() -> service.respondToRequest(dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You cannot accept or respond to your own blood request");

        verify(responseRepo, never()).save(any());
    }

    @Test
    @DisplayName("Duplicate Active Request Prevention: User cannot create multiple active requests for same blood group")
    void shouldPreventDuplicateActiveRequestForSameBloodGroup() {
        BloodRequestRepository requestRepo = mock(BloodRequestRepository.class);
        UserRepository userRepo = mock(UserRepository.class);
        SecurityUtils securityUtils = mock(SecurityUtils.class);
        BloodRequestMapper requestMapper = mock(BloodRequestMapper.class);
        RequestExpiryProperties expiryProps = mock(RequestExpiryProperties.class);

        BloodRequestCreationService creationService = new BloodRequestCreationService(
                requestRepo, userRepo, securityUtils, requestMapper, expiryProps
        );

        Long currentUserId = 20L;
        when(securityUtils.getCurrentUserId()).thenReturn(currentUserId);

        User user = User.builder().id(currentUserId).name("Requester").email("user@test.com").build();
        when(userRepo.findById(currentUserId)).thenReturn(Optional.of(user));

        List<BloodRequestStatus> activeStatuses = List.of(BloodRequestStatus.PENDING, BloodRequestStatus.ACTIVE, BloodRequestStatus.MATCHING);
        when(requestRepo.existsByCreatedByIdAndStatusInAndBloodGroup(currentUserId, activeStatuses, BloodGroup.O_POSITIVE))
                .thenReturn(true);

        CreateBloodRequestDto dto = new CreateBloodRequestDto();
        com.blood_donor.model.dto.request.donor_request.RequestConfig config = new com.blood_donor.model.dto.request.donor_request.RequestConfig();
        config.setBloodGroup(BloodGroup.O_POSITIVE);
        config.setUnitsRequired(1);
        dto.setConfig(config);

        assertThatThrownBy(() -> creationService.createRequest(dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You already have an active blood request for blood group O+");

        verify(requestRepo, never()).save(any());
    }

    @Test
    @DisplayName("Race Condition & Concurrency: Second donor is rejected once request reaches terminal state")
    void shouldRejectSecondDonorWhenRequestIsAlreadyFulfilled() {
        BloodRequestRepository requestRepo = mock(BloodRequestRepository.class);
        DonorRequestResponseRepository responseRepo = mock(DonorRequestResponseRepository.class);
        DonorProfileRepository donorRepo = mock(DonorProfileRepository.class);
        SecurityUtils securityUtils = mock(SecurityUtils.class);
        ReliabilityScoreService scoreService = mock(ReliabilityScoreService.class);
        BloodRequestMapper requestMapper = mock(BloodRequestMapper.class);

        BloodRequestResponseService service = new BloodRequestResponseService(
                requestRepo, responseRepo, donorRepo, securityUtils, scoreService, requestMapper
        );

        Long donorUserId = 77L;
        when(securityUtils.getCurrentUserId()).thenReturn(donorUserId);

        User requester = User.builder().id(99L).name("Requester").build();
        User donorUser = User.builder().id(donorUserId).name("Donor").build();
        DonorProfile donor = DonorProfile.builder().id(5L).user(donorUser).build();
        when(donorRepo.findByUserId(donorUserId)).thenReturn(Optional.of(donor));

        // Request has already been fulfilled by the first donor
        BloodRequest fulfilledRequest = BloodRequest.builder()
                .id(1L)
                .createdBy(requester)
                .status(BloodRequestStatus.FULFILLED)
                .unitsRequired(1)
                .build();
        when(requestRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(fulfilledRequest));

        RespondToRequestDto dto = new RespondToRequestDto();
        dto.setRequestId(1L);
        dto.setAccept(true);

        assertThatThrownBy(() -> service.respondToRequest(dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Cannot respond \u2014 request is: FULFILLED");

        verify(responseRepo, never()).save(any());
    }

    @Test
    @DisplayName("Concurrent Execution Test: Simulated multi-threaded donor responses")
    void shouldHandleConcurrentAcceptanceGracefully() throws InterruptedException, ExecutionException {
        BloodRequestRepository requestRepo = mock(BloodRequestRepository.class);
        DonorRequestResponseRepository responseRepo = mock(DonorRequestResponseRepository.class);
        DonorProfileRepository donorRepo = mock(DonorProfileRepository.class);
        SecurityUtils securityUtils = mock(SecurityUtils.class);
        ReliabilityScoreService scoreService = mock(ReliabilityScoreService.class);
        BloodRequestMapper requestMapper = mock(BloodRequestMapper.class);

        BloodRequestResponseService service = new BloodRequestResponseService(
                requestRepo, responseRepo, donorRepo, securityUtils, scoreService, requestMapper
        );

        User requester = User.builder().id(100L).name("Requester").build();
        BloodRequest sharedRequest = BloodRequest.builder()
                .id(1L)
                .createdBy(requester)
                .status(BloodRequestStatus.PENDING)
                .unitsRequired(1)
                .build();

        when(securityUtils.getCurrentUserId()).thenReturn(201L, 202L);

        User donor1User = User.builder().id(201L).build();
        DonorProfile donor1 = DonorProfile.builder().id(1L).user(donor1User).build();

        User donor2User = User.builder().id(202L).build();
        DonorProfile donor2 = DonorProfile.builder().id(2L).user(donor2User).build();

        when(donorRepo.findByUserId(201L)).thenReturn(Optional.of(donor1));
        when(donorRepo.findByUserId(202L)).thenReturn(Optional.of(donor2));

        // Synchronize through simulated database lock
        when(requestRepo.findByIdForUpdate(1L)).thenAnswer(inv -> {
            synchronized (sharedRequest) {
                return Optional.of(sharedRequest);
            }
        });

        // After donor 1 accepts, responseRepo returns 1 accepted count
        AtomicInteger acceptedCount = new AtomicInteger(0);
        when(responseRepo.countByRequestIdAndStatus(eq(1L), eq(ResponseStatus.ACCEPTED)))
                .thenAnswer(inv -> (long) acceptedCount.incrementAndGet());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<Boolean> donor1Action = () -> {
            startLatch.await();
            try {
                RespondToRequestDto dto = new RespondToRequestDto();
                dto.setRequestId(1L);
                dto.setAccept(true);
                service.respondToRequest(dto);
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Callable<Boolean> donor2Action = () -> {
            startLatch.await();
            try {
                RespondToRequestDto dto = new RespondToRequestDto();
                dto.setRequestId(1L);
                dto.setAccept(true);
                service.respondToRequest(dto);
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Future<Boolean> f1 = executor.submit(donor1Action);
        Future<Boolean> f2 = executor.submit(donor2Action);

        startLatch.countDown(); // trigger both concurrently

        boolean r1 = f1.get();
        boolean r2 = f2.get();

        executor.shutdown();

        // At least one succeeded, and the request transitioned to terminal state
        assertThat(r1 || r2).isTrue();
        assertThat(sharedRequest.getStatus()).isEqualTo(BloodRequestStatus.FULFILLED);
    }
}
