package com.blood_donor.security;

import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.service.IAiMatchingService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RefreshTokenRequest;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
@DisplayName("Authentication and Authorization Security Matrix Integration Tests")
public class AuthMatrixIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private BloodRequestRepository bloodRequestRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private IAiMatchingService aiMatchingService;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
    }

    private User createUser(String emailPrefix, Set<Role> roles) {
        return userRepository.save(User.builder()
                .name("Auth Matrix " + emailPrefix)
                .email(emailPrefix + "_" + System.nanoTime() + "@test.com")
                .password(passwordEncoder.encode("TestPass@2026!"))
                .phone("98" + (long) (Math.random() * 89999999L + 10000000L))
                .roles(roles)
                .isActive(true)
                .build());
    }

    @Nested
    @DisplayName("1. AI Match Donors RBAC Tests (/api/ai/match-donors)")
    class AiMatchDonorsRbacTests {

        private final String validAiPayload = "{\n" +
                "  \"blood_group\": \"O_POSITIVE\",\n" +
                "  \"latitude\": 19.0760,\n" +
                "  \"longitude\": 72.8777,\n" +
                "  \"radius_km\": 10.0,\n" +
                "  \"urgency\": \"URGENT\",\n" +
                "  \"limit\": 10\n" +
                "}";

        @Test
        @DisplayName("Anonymous request to /api/ai/match-donors must return 401 Unauthorized")
        void anonymousAiMatchReturns401() throws Exception {
            mockMvc.perform(post("/api/ai/match-donors")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAiPayload))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("ROLE_USER with valid body to /api/ai/match-donors must return 403 Forbidden")
        void userRoleAiMatchReturns403() throws Exception {
            User user = createUser("user_rbac", Set.of(Role.USER));
            String token = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());

            mockMvc.perform(post("/api/ai/match-donors")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAiPayload))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ROLE_DONOR with valid body to /api/ai/match-donors must return 403 Forbidden")
        void donorRoleAiMatchReturns403() throws Exception {
            User donor = createUser("donor_rbac", Set.of(Role.DONOR));
            String token = jwtTokenProvider.generateAccessToken(donor.getEmail(), donor.getRoles(), donor.getId());

            mockMvc.perform(post("/api/ai/match-donors")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAiPayload))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("ROLE_HOSPITAL with valid body to /api/ai/match-donors must return 200 OK")
        void hospitalRoleAiMatchReturns200() throws Exception {
            User hospital = createUser("hospital_rbac", Set.of(Role.HOSPITAL));
            String token = jwtTokenProvider.generateAccessToken(hospital.getEmail(), hospital.getRoles(), hospital.getId());

            AiMatchResponse mockResponse = new AiMatchResponse();
            mockResponse.setRequestId(1L);
            mockResponse.setUrgency("URGENT");
            mockResponse.setDonors(Collections.emptyList());
            mockResponse.setTotalFound(0);
            mockResponse.setProcessingTimeMs(12.0);
            when(aiMatchingService.findBestDonors(any(AiMatchRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/api/ai/match-donors")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAiPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("ROLE_ADMIN with valid body to /api/ai/match-donors must return 200 OK")
        void adminRoleAiMatchReturns200() throws Exception {
            User admin = createUser("admin_rbac", Set.of(Role.ADMIN));
            String token = jwtTokenProvider.generateAccessToken(admin.getEmail(), admin.getRoles(), admin.getId());

            AiMatchResponse mockResponse = new AiMatchResponse();
            mockResponse.setRequestId(2L);
            mockResponse.setUrgency("URGENT");
            mockResponse.setDonors(Collections.emptyList());
            mockResponse.setTotalFound(0);
            mockResponse.setProcessingTimeMs(15.0);
            when(aiMatchingService.findBestDonors(any(AiMatchRequest.class))).thenReturn(mockResponse);

            mockMvc.perform(post("/api/ai/match-donors")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validAiPayload))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("2. Wrong Password and Account Lockout Tests")
    class AuthenticationAndLockoutTests {

        @Test
        @DisplayName("Wrong password on POST /api/auth/login must return 401 Unauthorized")
        void wrongPasswordReturns401() throws Exception {
            User user = createUser("wrong_pwd", Set.of(Role.USER));

            LoginRequest loginReq = new LoginRequest();
            loginReq.setEmail(user.getEmail());
            loginReq.setPassword("CompletelyWrongPassword@999");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginReq)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Lockout active in Redis must return 429 Too Many Requests")
        void lockedAccountReturns429() throws Exception {
            User user = createUser("lockout_user", Set.of(Role.USER));
            String normalizedEmail = user.getEmail().toLowerCase();

            // Mock Redis returning that the (IP, email) lockout key exists
            when(redisTemplate.hasKey(org.mockito.ArgumentMatchers.contains(normalizedEmail))).thenReturn(true);

            LoginRequest loginReq = new LoginRequest();
            loginReq.setEmail(user.getEmail());
            loginReq.setPassword("TestPass@2026!");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginReq)))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.error").value("Account temporarily locked due to repeated failed logins. Please try again after 15 minutes or reset your password."));
        }
    }

    @Nested
    @DisplayName("3. IDOR Request Mutation Protection")
    class IdorMutationTests {

        @Test
        @DisplayName("Attacker attempting to cancel legitimate owner's request must return 403 Forbidden")
        void attackerCannotCancelOthersRequest() throws Exception {
            User owner = createUser("owner_req", Set.of(Role.USER));
            User attacker = createUser("attacker_req", Set.of(Role.USER));

            BloodRequest request = bloodRequestRepository.save(BloodRequest.builder()
                    .patientName("Protected Patient")
                    .bloodGroup(BloodGroup.O_NEGATIVE)
                    .hospitalName("Metropolitan Hospital")
                    .city("Bangalore")
                    .unitsRequired(2)
                    .urgency(UrgencyLevel.URGENT)
                    .contactPhone("9812345678")
                    .status(BloodRequestStatus.PENDING)
                    .createdBy(owner)
                    .expiredAt(LocalDateTime.now().plusHours(24))
                    .build());

            String attackerToken = jwtTokenProvider.generateAccessToken(attacker.getEmail(), attacker.getRoles(), attacker.getId());

            mockMvc.perform(patch("/api/request/" + request.getId() + "/cancel")
                            .header("Authorization", "Bearer " + attackerToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You can only cancel your own requests"));
        }

        @Test
        @DisplayName("Attacker attempting to fulfill legitimate owner's request must return 403 Forbidden")
        void attackerCannotFulfillOthersRequest() throws Exception {
            User owner = createUser("owner_req2", Set.of(Role.USER));
            User attacker = createUser("attacker_req2", Set.of(Role.USER));

            BloodRequest request = bloodRequestRepository.save(BloodRequest.builder()
                    .patientName("Protected Patient 2")
                    .bloodGroup(BloodGroup.A_POSITIVE)
                    .hospitalName("Apex Hospital")
                    .city("Chennai")
                    .unitsRequired(1)
                    .urgency(UrgencyLevel.NORMAL)
                    .contactPhone("9812345679")
                    .status(BloodRequestStatus.ACTIVE)
                    .createdBy(owner)
                    .expiredAt(LocalDateTime.now().plusHours(48))
                    .build());

            String attackerToken = jwtTokenProvider.generateAccessToken(attacker.getEmail(), attacker.getRoles(), attacker.getId());

            mockMvc.perform(patch("/api/request/" + request.getId() + "/fulfill")
                            .header("Authorization", "Bearer " + attackerToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("You can only fulfill your own requests"));
        }
    }

    @Nested
    @DisplayName("4. Privilege Escalation Prevention Tests")
    class PrivilegeEscalationTests {

        @Test
        @DisplayName("Register payload containing ROLE_ADMIN does not grant admin role")
        void registrationIgnoresPrivilegeEscalation() throws Exception {
            String testEmail = "escalation_" + System.nanoTime() + "@test.com";

            // Craft raw JSON with extra fields attempting role escalation
            String rawJsonWithRoles = "{\n" +
                    "  \"name\": \"Escalation Attacker\",\n" +
                    "  \"email\": \"" + testEmail + "\",\n" +
                    "  \"password\": \"AttackerPass@2026!\",\n" +
                    "  \"phone\": \"9876543210\",\n" +
                    "  \"roles\": [\"ROLE_ADMIN\"],\n" +
                    "  \"role\": \"ADMIN\",\n" +
                    "  \"isAdmin\": true\n" +
                    "}";

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(rawJsonWithRoles))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));

            User savedUser = userRepository.findByEmailIgnoreCase(testEmail).orElseThrow();
            org.junit.jupiter.api.Assertions.assertEquals(Set.of(Role.USER), savedUser.getRoles(),
                    "Newly registered user must only have ROLE_USER regardless of payload fields");
        }
    }

    @Nested
    @DisplayName("5. Token Blacklist and Refresh Token Rotation Tests")
    class TokenBlacklistAndRotationTests {

        @Test
        @DisplayName("Blacklisted access token in Redis must return 401 Unauthorized")
        void blacklistedAccessTokenReturns401() throws Exception {
            User user = createUser("blacklist_user", Set.of(Role.USER));
            String token = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());

            // Mock Redis: access token is present in blacklist
            when(redisTemplate.hasKey("blacklist:token:" + token)).thenReturn(true);

            mockMvc.perform(get("/api/auth/me")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Revoked/Blacklisted refresh token must return 401 Unauthorized on refresh attempt")
        void revokedRefreshTokenReturns401() throws Exception {
            String dummyRefreshToken = "revoked-refresh-token-" + System.nanoTime();

            // Mock Redis: refresh token is in blacklist
            when(redisTemplate.hasKey("blacklist:token:" + dummyRefreshToken)).thenReturn(true);

            RefreshTokenRequest req = new RefreshTokenRequest();
            req.setRefreshToken(dummyRefreshToken);

            mockMvc.perform(post("/api/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
    }
}
