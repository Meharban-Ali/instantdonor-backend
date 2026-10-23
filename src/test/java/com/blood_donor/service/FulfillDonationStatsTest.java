package com.blood_donor.service;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
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
import com.blood_donor.utils.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Fulfill recordDonation and Tracking Stats Verification")
class FulfillDonationStatsTest {

    @Autowired private BloodRequestCreationService creationService;
    @Autowired private BloodRequestRepository requestRepo;
    @Autowired private DonorRequestResponseRepository responseRepo;
    @Autowired private DonorProfileRepository donorRepo;
    @Autowired private UserRepository userRepo;

    @MockitoBean private SecurityUtils securityUtils;
    @MockitoBean private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("Fulfill updates donor stats, verifies idempotency and response tracking fields")
    void testFulfillDonationFlow() {
        // 1. Create requester user
        User requester = User.builder()
                .name("Requester User")
                .email("req_" + System.currentTimeMillis() + "@test.com")
                .password("Password@123")
                .phone("9876543210")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .isActive(true)
                .build();
        requester = userRepo.saveAndFlush(requester);

        Mockito.when(securityUtils.getCurrentUserId()).thenReturn(requester.getId());

        // 2. Create donor user and profile
        User donorUser = User.builder()
                .name("Donor Rohan")
                .email("donor_" + System.currentTimeMillis() + "@test.com")
                .password("Password@123")
                .phone("9876543211")
                .roles(new HashSet<>(Set.of(Role.USER, Role.DONOR)))
                .isActive(true)
                .build();
        donorUser = userRepo.saveAndFlush(donorUser);

        DonorProfile donor = DonorProfile.builder()
                .user(donorUser)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .city("Delhi")
                .state("Delhi")
                .latitude(BigDecimal.valueOf(28.6139))
                .longitude(BigDecimal.valueOf(77.2090))
                .isAvailable(true)
                .totalDonations(3)
                .lastDonationDate(null)
                .nextEligibleDate(null)
                .reliabilityScore(BigDecimal.valueOf(95.0))
                .responseRate(BigDecimal.valueOf(90.0))
                .build();
        donor = donorRepo.saveAndFlush(donor);

        // 3. Create BloodRequest
        BloodRequest req = BloodRequest.builder()
                .createdBy(requester)
                .patientName("Patient X")
                .contactPhone("9876543210")
                .hospitalName("City Hospital")
                .city("Delhi")
                .bloodGroup(BloodGroup.O_POSITIVE)
                .urgency(UrgencyLevel.CRITICAL)
                .unitsRequired(1)
                .status(BloodRequestStatus.MATCHING)
                .expiredAt(LocalDateTime.now().plusHours(24))
                .build();
        final BloodRequest savedReq = requestRepo.saveAndFlush(req);

        // 4. Create ACCEPTED response from donor
        DonorRequestResponse response = DonorRequestResponse.builder()
                .request(savedReq)
                .donor(donor)
                .status(ResponseStatus.ACCEPTED)
                .respondedAt(LocalDateTime.now())
                .build();
        responseRepo.saveAndFlush(response);

        // Verify initial donor state before fulfillment
        assertEquals(3, donor.getTotalDonations());
        assertTrue(donor.isAvailable());
        assertNull(donor.getLastDonationDate());

        // 5. Execute markFulfilled
        BloodRequestResponse res = creationService.markFulfilled(savedReq.getId());

        // Verify response tracking fields
        assertNotNull(res);
        assertNotNull(res.getTracking());
        assertEquals(1L, res.getTracking().getAcceptedDonors());
        assertEquals(100.0, res.getTracking().getFulfillmentPercentage(), 0.01);
        assertTrue(res.getTracking().isFulfilled());
        assertTrue(res.getTracking().getFulfilled());

        // 6. Verify donor stats updated in DB
        DonorProfile updatedDonor = donorRepo.findById(donor.getId()).orElseThrow();
        assertEquals(4, updatedDonor.getTotalDonations(), "totalDonations should increment by 1");
        assertFalse(updatedDonor.isAvailable(), "isAvailable should become false");
        assertEquals(LocalDate.now(), updatedDonor.getLastDonationDate(), "lastDonationDate should be today");
        assertEquals(LocalDate.now().plusDays(90), updatedDonor.getNextEligibleDate(), "nextEligibleDate should be +90 days");

        // 7. Verify Idempotency - double fulfill on terminal state must fail with badRequest
        assertThrows(Exception.class, () -> creationService.markFulfilled(savedReq.getId()),
                "Fulfilling an already terminal request must fail");

        // Verify total donations did not increment again
        DonorProfile doubleCheckDonor = donorRepo.findById(donor.getId()).orElseThrow();
        assertEquals(4, doubleCheckDonor.getTotalDonations(), "totalDonations must not double count");
    }
}
