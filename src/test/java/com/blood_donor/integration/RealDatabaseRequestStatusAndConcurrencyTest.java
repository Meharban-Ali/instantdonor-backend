package com.blood_donor.integration;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.dto.request.donor_request.RespondToRequestDto;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.service.donor_service_impl.BloodRequestCreationService;
import com.blood_donor.service.donor_service_impl.BloodRequestResponseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Real MySQL 8 Request Lifecycle, Self-Accept & Concurrent Accept Tests")
public class RealDatabaseRequestStatusAndConcurrencyTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @Autowired
    private DonorRequestResponseRepository responseRepository;

    @Autowired
    private BloodRequestCreationService creationService;

    @Autowired
    private BloodRequestResponseService responseService;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_DONOR"))
                )
        );
    }

    private User createUser(String email, String phone) {
        return userRepository.save(User.builder()
                .name("User " + email)
                .email(email)
                .phone(phone)
                .password("$2a$12$e8J4V07V77v6d7p2q5k7te1V5zL8Zt2X0W2j1Y8m5k2p3q4r5s6t7u")
                .roles(Set.of(Role.USER, Role.DONOR))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    private DonorProfile createDonor(User user, BloodGroup bg) {
        return donorProfileRepository.save(DonorProfile.builder()
                .user(user)
                .bloodGroup(bg)
                .city("Delhi")
                .state("Delhi")
                .latitude(BigDecimal.valueOf(28.6139))
                .longitude(BigDecimal.valueOf(77.2090))
                .isAvailable(true)
                .reliabilityScore(BigDecimal.valueOf(95.0))
                .responseRate(BigDecimal.valueOf(90.0))
                .totalDonations(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    private CreateBloodRequestDto buildDto(String patientName, BloodGroup bg, String hospital, String city, int units, UrgencyLevel urgency, String phone) {
        CreateBloodRequestDto dto = new CreateBloodRequestDto();
        com.blood_donor.model.dto.request.donor_request.PatientInfo p = new com.blood_donor.model.dto.request.donor_request.PatientInfo();
        p.setPatientName(patientName);
        p.setContactPhone(phone);
        dto.setPatient(p);

        com.blood_donor.model.dto.request.donor_request.HospitalInfo h = new com.blood_donor.model.dto.request.donor_request.HospitalInfo();
        h.setHospitalName(hospital);
        h.setCity(city);
        dto.setHospital(h);

        com.blood_donor.model.dto.request.donor_request.RequestConfig c = new com.blood_donor.model.dto.request.donor_request.RequestConfig();
        c.setBloodGroup(bg);
        c.setUnitsRequired(units);
        c.setUrgency(urgency);
        dto.setConfig(c);

        return dto;
    }

    @Test
    @DisplayName("Request Lifecycle Transitions: PENDING -> MATCHING -> FULFILLED, terminal state rejection")
    void testRequestLifecycleTransitions() {
        User requester = createUser("req1@test.com", "9100000001");
        authenticateUser(requester);

        CreateBloodRequestDto createDto = buildDto("Patient A", BloodGroup.O_POSITIVE, "City Hospital", "Delhi", 2, UrgencyLevel.URGENT, "9100000001");

        BloodRequestResponse created = creationService.createRequest(createDto);
        assertThat(created.getStatus()).isEqualTo(BloodRequestStatus.PENDING);

        // Donor 1 accepts -> status becomes MATCHING
        User donorUser1 = createUser("donor1@test.com", "9100000002");
        createDonor(donorUser1, BloodGroup.O_POSITIVE);
        authenticateUser(donorUser1);

        RespondToRequestDto respDto1 = new RespondToRequestDto();
        respDto1.setRequestId(created.getId());
        respDto1.setAccept(true);
        respDto1.setNotes("Can donate today");
        BloodRequestResponse afterDonor1 = responseService.respondToRequest(respDto1);
        assertThat(afterDonor1.getStatus()).isEqualTo(BloodRequestStatus.MATCHING);

        // Donor 2 accepts -> unitsRequired (2) reached -> status becomes FULFILLED
        User donorUser2 = createUser("donor2@test.com", "9100000003");
        createDonor(donorUser2, BloodGroup.O_POSITIVE);
        authenticateUser(donorUser2);

        RespondToRequestDto respDto2 = new RespondToRequestDto();
        respDto2.setRequestId(created.getId());
        respDto2.setAccept(true);
        BloodRequestResponse afterDonor2 = responseService.respondToRequest(respDto2);
        assertThat(afterDonor2.getStatus()).isEqualTo(BloodRequestStatus.FULFILLED);

        // Donor 3 tries to respond after fulfillment -> must be rejected as terminal
        User donorUser3 = createUser("donor3@test.com", "9100000004");
        createDonor(donorUser3, BloodGroup.O_POSITIVE);
        authenticateUser(donorUser3);

        RespondToRequestDto respDto3 = new RespondToRequestDto();
        respDto3.setRequestId(created.getId());
        respDto3.setAccept(true);

        assertThatThrownBy(() -> responseService.respondToRequest(respDto3))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Cannot respond — request is: FULFILLED");
    }

    @Test
    @DisplayName("Self-accept prevention: Requester cannot respond to own blood request")
    void testSelfAcceptPrevention() {
        User requester = createUser("selfreq@test.com", "9100000010");
        createDonor(requester, BloodGroup.B_POSITIVE);
        authenticateUser(requester);

        CreateBloodRequestDto createDto = buildDto("Self Requester", BloodGroup.B_POSITIVE, "Apollo Hospital", "Delhi", 1, UrgencyLevel.NORMAL, "9100000010");

        BloodRequestResponse created = creationService.createRequest(createDto);

        RespondToRequestDto respDto = new RespondToRequestDto();
        respDto.setRequestId(created.getId());
        respDto.setAccept(true);

        assertThatThrownBy(() -> responseService.respondToRequest(respDto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You cannot accept or respond to your own blood request");
    }

    @Test
    @DisplayName("Duplicate active request prevention for same blood group")
    void testDuplicateActiveRequestPrevention() {
        User requester = createUser("dup_req@test.com", "9100000020");
        authenticateUser(requester);

        CreateBloodRequestDto createDto1 = buildDto("Patient One", BloodGroup.A_POSITIVE, "Max Hospital", "Delhi", 1, UrgencyLevel.NORMAL, "9100000020");

        creationService.createRequest(createDto1);

        CreateBloodRequestDto createDto2 = buildDto("Patient Two", BloodGroup.A_POSITIVE, "Fortis Hospital", "Delhi", 1, UrgencyLevel.URGENT, "9100000020");

        assertThatThrownBy(() -> creationService.createRequest(createDto2))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You already have an active blood request for blood group A+");
    }

    @Test
    @DisplayName("Concurrent Accept Race Condition on MySQL 8: Row-level lock ensures no over-fulfillment")
    void testConcurrentAcceptRaceConditionOnMySql() throws Exception {
        User requester = createUser("concurrent_req@test.com", "9100000030");
        authenticateUser(requester);

        CreateBloodRequestDto createDto = buildDto("Urgent Patient", BloodGroup.O_NEGATIVE, "AIIMS", "Delhi", 1, UrgencyLevel.CRITICAL, "9100000030");

        BloodRequestResponse request = creationService.createRequest(createDto);
        Long requestId = request.getId();

        int numDonors = 4;
        List<User> donorUsers = new ArrayList<>();
        for (int i = 0; i < numDonors; i++) {
            User u = createUser("concurrent_donor_" + i + "@test.com", "920000000" + i);
            createDonor(u, BloodGroup.O_NEGATIVE);
            donorUsers.add(u);
        }

        ExecutorService executor = Executors.newFixedThreadPool(numDonors);
        CountDownLatch readyLatch = new CountDownLatch(numDonors);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successAccepts = new AtomicInteger(0);
        AtomicInteger terminalRejections = new AtomicInteger(0);

        for (int i = 0; i < numDonors; i++) {
            final User user = donorUsers.get(i);
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    // Set thread security context
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    user.getEmail(),
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_DONOR"))
                            )
                    );
                    RespondToRequestDto dto = new RespondToRequestDto();
                    dto.setRequestId(requestId);
                    dto.setAccept(true);
                    responseService.respondToRequest(dto);
                    successAccepts.incrementAndGet();
                } catch (ApiException e) {
                    if (e.getMessage() != null && e.getMessage().contains("Cannot respond")) {
                        terminalRejections.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    SecurityContextHolder.clearContext();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startLatch.countDown(); // Fire all donor threads simultaneously!
        executor.shutdown();
        boolean finished = executor.awaitTermination(15, TimeUnit.SECONDS);
        assertThat(finished).isTrue();

        // Check real DB state
        long acceptedCountInDb = responseRepository.countByRequestIdAndStatus(requestId, ResponseStatus.ACCEPTED);
        assertThat(acceptedCountInDb).isEqualTo(1); // Exactly 1 accepted response allowed

        BloodRequest freshRequest = bloodRequestRepository.findById(requestId).orElseThrow();
        assertThat(freshRequest.getStatus()).isEqualTo(BloodRequestStatus.FULFILLED);
        assertThat(freshRequest.getFulfilledAt()).isNotNull();
    }
}
