package com.blood_donor.security;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Security Access, IDOR Protection, and Validation Tests")
class SecurityAccessAndIdorTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private BloodRequestRepository bloodRequestRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
    }

    @Nested
    @DisplayName("1. Role-Based Access Control (RBAC) Denial Tests")
    class RoleBasedAccessTests {

        @Test
        @DisplayName("DONOR role accessing /api/admin/dashboard must return 403 Forbidden")
        void donorAccessingAdminEndpointReturns403() throws Exception {
            User donorUser = userRepository.save(User.builder()
                    .name("Donor Only User")
                    .email("donor_rbac_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000001")
                    .roles(Set.of(Role.DONOR))
                    .isActive(true)
                    .build());

            String donorToken = jwtTokenProvider.generateAccessToken(
                    donorUser.getEmail(), donorUser.getRoles(), donorUser.getId()
            );

            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + donorToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Regular USER role accessing /api/admin/dashboard must return 403 Forbidden")
        void userAccessingAdminEndpointReturns403() throws Exception {
            User regularUser = userRepository.save(User.builder()
                    .name("Regular User")
                    .email("user_rbac_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000002")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            String userToken = jwtTokenProvider.generateAccessToken(
                    regularUser.getEmail(), regularUser.getRoles(), regularUser.getId()
            );

            mockMvc.perform(get("/api/admin/dashboard")
                            .header("Authorization", "Bearer " + userToken))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Unauthenticated request to /api/admin/dashboard must return 401 Unauthorized")
        void unauthenticatedAccessToAdminReturns401() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("2. Insecure Direct Object Reference (IDOR) Denial Tests")
    class IdorProtectionTests {

        @Test
        @DisplayName("User B attempting to cancel User A's blood request must return 403 Forbidden")
        void attackerCancellingOthersRequestReturns403() throws Exception {
            User ownerUser = userRepository.save(User.builder()
                    .name("Legitimate Owner")
                    .email("owner_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000003")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            User attackerUser = userRepository.save(User.builder()
                    .name("Attacker User")
                    .email("attacker_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000004")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            BloodRequest request = bloodRequestRepository.save(BloodRequest.builder()
                    .patientName("Patient Alpha")
                    .bloodGroup(BloodGroup.O_POSITIVE)
                    .hospitalName("City General Hospital")
                    .city("Mumbai")
                    .unitsRequired(2)
                    .urgency(UrgencyLevel.URGENT)
                    .contactPhone("9800000003")
                    .status(BloodRequestStatus.PENDING)
                    .createdBy(ownerUser)
                    .expiredAt(LocalDateTime.now().plusHours(24))
                    .build());

            String attackerToken = jwtTokenProvider.generateAccessToken(
                    attackerUser.getEmail(), attackerUser.getRoles(), attackerUser.getId()
            );

            // User B attempts to cancel User A's blood request
            mockMvc.perform(patch("/api/request/" + request.getId() + "/cancel")
                            .header("Authorization", "Bearer " + attackerToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You can only cancel your own requests"));
        }

        @Test
        @DisplayName("User B attempting to mark fulfilled User A's blood request must return 403 Forbidden")
        void attackerFulfillingOthersRequestReturns403() throws Exception {
            User ownerUser = userRepository.save(User.builder()
                    .name("Legitimate Owner")
                    .email("owner2_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000005")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            User attackerUser = userRepository.save(User.builder()
                    .name("Attacker User")
                    .email("attacker2_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000006")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            BloodRequest request = bloodRequestRepository.save(BloodRequest.builder()
                    .patientName("Patient Beta")
                    .bloodGroup(BloodGroup.A_POSITIVE)
                    .hospitalName("Metropolitan Clinic")
                    .city("Delhi")
                    .unitsRequired(1)
                    .urgency(UrgencyLevel.NORMAL)
                    .contactPhone("9800000005")
                    .status(BloodRequestStatus.ACTIVE)
                    .createdBy(ownerUser)
                    .expiredAt(LocalDateTime.now().plusHours(48))
                    .build());

            String attackerToken = jwtTokenProvider.generateAccessToken(
                    attackerUser.getEmail(), attackerUser.getRoles(), attackerUser.getId()
            );

            // User B attempts to mark fulfilled User A's blood request
            mockMvc.perform(patch("/api/request/" + request.getId() + "/fulfill")
                            .header("Authorization", "Bearer " + attackerToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You can only fulfill your own requests"));
        }

        @Test
        @DisplayName("Legitimate owner CAN cancel their own blood request successfully")
        void ownerCanCancelOwnRequest() throws Exception {
            User ownerUser = userRepository.save(User.builder()
                    .name("Legitimate Owner")
                    .email("owner3_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000007")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            BloodRequest request = bloodRequestRepository.save(BloodRequest.builder()
                    .patientName("Patient Gamma")
                    .bloodGroup(BloodGroup.B_POSITIVE)
                    .hospitalName("Hope Hospital")
                    .city("Pune")
                    .unitsRequired(1)
                    .urgency(UrgencyLevel.URGENT)
                    .contactPhone("9800000007")
                    .status(BloodRequestStatus.PENDING)
                    .createdBy(ownerUser)
                    .expiredAt(LocalDateTime.now().plusHours(24))
                    .build());

            String ownerToken = jwtTokenProvider.generateAccessToken(
                    ownerUser.getEmail(), ownerUser.getRoles(), ownerUser.getId()
            );

            mockMvc.perform(patch("/api/request/" + request.getId() + "/cancel")
                            .header("Authorization", "Bearer " + ownerToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("3. Input Validation Failure (HTTP 400) Tests")
    class InputValidationTests {

        @Test
        @DisplayName("POST /api/auth/register with empty payload must return 400 Bad Request")
        void emptyRegistrationPayloadReturns400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/auth/register with invalid email and short password must return 400")
        void invalidEmailAndShortPasswordReturns400() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setName("Test Invalid");
            req.setEmail("not-an-email");
            req.setPassword("123");
            req.setPhone("12345");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/auth/register with invalid phone format must return 400")
        void invalidPhoneFormatReturns400() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setName("Valid Name");
            req.setEmail("valid" + System.nanoTime() + "@test.com");
            req.setPassword("Password@123");
            req.setPhone("invalid-phone-abc");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/request with null patient info must return 400 Bad Request")
        void createBloodRequestWithMissingPatientReturns400() throws Exception {
            User user = userRepository.save(User.builder()
                    .name("Requester User")
                    .email("requester_" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9800000008")
                    .roles(Set.of(Role.USER))
                    .isActive(true)
                    .build());

            String token = jwtTokenProvider.generateAccessToken(
                    user.getEmail(), user.getRoles(), user.getId()
            );

            CreateBloodRequestDto invalidDto = new CreateBloodRequestDto();
            // Patient, hospital, and config are null

            mockMvc.perform(post("/api/request")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
