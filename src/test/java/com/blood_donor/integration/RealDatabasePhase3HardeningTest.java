package com.blood_donor.integration;

import com.blood_donor.admin.service_Impl.AdminUserService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.security.JwtTokenProvider;
import com.blood_donor.service.auth_service.AuthService;
import com.blood_donor.service.donor_service_impl.BloodRequestCreationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("Phase 3 API Consistency, Hardening, and Security Integration Tests")
public class RealDatabasePhase3HardeningTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AdminUserService adminUserService;
    @Autowired private BloodRequestCreationService bloodRequestCreationService;
    @Autowired private AuthService authService;

    @BeforeEach
    void setup() {
        cleanDatabase();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Security Headers: Verify X-Content-Type-Options, X-Frame-Options, Referrer-Policy")
    void testSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/request/active"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"));
    }

    @Test
    @DisplayName("Filter Error Uniformity: 401 Unauthorized returns standard ApiResponse")
    void testUniform401Response() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Filter Error Uniformity: 403 Forbidden returns standard ApiResponse")
    void testUniform403Response() throws Exception {
        User user = baseUserRepo.save(User.builder()
                .name("Regular User")
                .email("user_test@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543210")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .isActive(true)
                .build());

        String token = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Forbidden: insufficient permissions"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("Pagination Caps: size > 50 returns 400 Bad Request with validation message")
    void testPaginationCapExceeded() throws Exception {
        User admin = baseUserRepo.save(User.builder()
                .name("Super Admin")
                .email("admin_pagination@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543211")
                .roles(new HashSet<>(Set.of(Role.ADMIN)))
                .isActive(true)
                .build());

        String adminToken = jwtTokenProvider.generateAccessToken(admin.getEmail(), admin.getRoles(), admin.getId());

        mockMvc.perform(get("/api/admin/users?size=100")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Size must be <= 50")));

        mockMvc.perform(get("/api/admin/donors?size=100")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Size must be <= 50")));
    }

    @Test
    @DisplayName("Admin Role Hardening: Admin cannot demote themselves")
    void testAdminCannotDemoteThemselves() {
        User admin = baseUserRepo.save(User.builder()
                .name("Admin Self")
                .email("admin_self@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543212")
                .roles(new HashSet<>(Set.of(Role.ADMIN)))
                .isActive(true)
                .build());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(admin.getEmail(), null, Set.of())
        );

        assertThatThrownBy(() -> adminUserService.updateUserRole(admin.getId(), Role.USER))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Administrators cannot demote their own account");
    }

    @Test
    @DisplayName("Admin Role Hardening: Cannot demote the last remaining active admin")
    void testCannotDemoteLastActiveAdmin() {
        User soleAdmin = baseUserRepo.save(User.builder()
                .name("Sole Admin")
                .email("sole_admin@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543213")
                .roles(new HashSet<>(Set.of(Role.ADMIN)))
                .isActive(true)
                .build());

        // Simulated caller is another entity
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("other_actor@example.com", null, Set.of())
        );

        assertThatThrownBy(() -> adminUserService.updateUserRole(soleAdmin.getId(), Role.USER))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Cannot demote the last remaining active administrator");
    }

    @Test
    @DisplayName("Blood Request Limit: Max active requests per user cap enforced")
    void testMaxActiveBloodRequestsPerUserEnforced() {
        User requester = baseUserRepo.save(User.builder()
                .name("Requester User")
                .email("requester_limit@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543214")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .isActive(true)
                .build());

        // Create 3 active requests for different blood groups
        BloodGroup[] bloodGroups = {BloodGroup.A_POSITIVE, BloodGroup.B_POSITIVE, BloodGroup.AB_POSITIVE};
        for (BloodGroup bg : bloodGroups) {
            baseRequestRepo.save(BloodRequest.builder()
                    .patientName("Patient " + bg)
                    .bloodGroup(bg)
                    .hospitalName("City Hospital")
                    .city("Delhi")
                    .latitude(new BigDecimal("28.6139"))
                    .longitude(new BigDecimal("77.2090"))
                    .unitsRequired(1)
                    .urgency(UrgencyLevel.NORMAL)
                    .contactPhone("+919876543214")
                    .status(BloodRequestStatus.ACTIVE)
                    .createdBy(requester)
                    .expiredAt(LocalDateTime.now().plusHours(24))
                    .build());
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(requester.getEmail(), null, Set.of())
        );

        CreateBloodRequestDto dto = new CreateBloodRequestDto();
        com.blood_donor.model.dto.request.donor_request.PatientInfo p = new com.blood_donor.model.dto.request.donor_request.PatientInfo();
        p.setPatientName("Fourth Patient");
        p.setContactPhone("+919876543214");
        dto.setPatient(p);

        com.blood_donor.model.dto.request.donor_request.HospitalInfo h = new com.blood_donor.model.dto.request.donor_request.HospitalInfo();
        h.setHospitalName("City Hospital");
        h.setCity("Delhi");
        h.setLatitude(new BigDecimal("28.6139"));
        h.setLongitude(new BigDecimal("77.2090"));
        dto.setHospital(h);

        com.blood_donor.model.dto.request.donor_request.RequestConfig c = new com.blood_donor.model.dto.request.donor_request.RequestConfig();
        c.setBloodGroup(BloodGroup.O_POSITIVE);
        c.setUnitsRequired(1);
        c.setUrgency(UrgencyLevel.NORMAL);
        dto.setConfig(c);

        assertThatThrownBy(() -> bloodRequestCreationService.createRequest(dto))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("You cannot have more than 3 active blood requests simultaneously");
    }

    @Test
    @DisplayName("Account Security: Registration requires strong password complexity")
    void testRegistrationPasswordComplexityPolicy() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test Weak Password");
        req.setEmail("weakpw@example.com");
        req.setPassword("simplepass"); // Missing digits, uppercase, and special chars
        req.setPhone("+919876543215");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Password must contain at least one uppercase letter")));
    }

    @Test
    @DisplayName("Account Security: 5 consecutive failed logins trigger temporary lockout (429)")
    void testRepeatedFailedLoginLockout() throws Exception {
        baseUserRepo.save(User.builder()
                .name("Lockout Target")
                .email("lockout_test@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543216")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .isActive(true)
                .build());

        LoginRequest wrongReq = new LoginRequest();
        wrongReq.setEmail("lockout_test@example.com");
        wrongReq.setPassword("WrongPassword@999");

        // 5 consecutive failed login attempts
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongReq)))
                    .andExpect(status().isUnauthorized());
        }

        // 6th attempt from same IP must be locked out with 429
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongReq)))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Account temporarily locked")));

        // Legitimate user on a different IP is NOT blocked: (IP, email) throttle prevents DoS
        LoginRequest correctReq = new LoginRequest();
        correctReq.setEmail("lockout_test@example.com");
        correctReq.setPassword("Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .with(req -> {
                            req.setRemoteAddr("203.0.113.10");
                            return req;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("Distributed Attack: Multiple IPs attacking one email triggers metric/log alert without locking innocent victim")
    void testDistributedBruteForceDoesNotLockUserAndRecordsAlert() throws Exception {
        baseUserRepo.save(User.builder()
                .name("Distributed Target")
                .email("dist_target@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543299")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .isActive(true)
                .build());

        LoginRequest wrongReq = new LoginRequest();
        wrongReq.setEmail("dist_target@example.com");
        wrongReq.setPassword("WrongPassword@999");

        // Simulate 12 different IPs each failing once against this email
        for (int i = 1; i <= 12; i++) {
            final String attackerIp = "198.51.100." + i;
            mockMvc.perform(post("/api/auth/login")
                            .with(req -> {
                                req.setRemoteAddr(attackerIp);
                                return req;
                            })
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongReq)))
                    .andExpect(status().isUnauthorized());
        }

        // Legitimate victim logs in from home IP: MUST SUCCEED (no DoS hard lock)
        LoginRequest correctReq = new LoginRequest();
        correctReq.setEmail("dist_target@example.com");
        correctReq.setPassword("Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .with(req -> {
                            req.setRemoteAddr("203.0.113.50");
                            return req;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("DPDP Right to Erasure: User can delete and anonymize their account")
    void testDpdpRightToErasure() throws Exception {
        User user = baseUserRepo.save(User.builder()
                .name("John Doe")
                .email("johndoe@example.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("+919876543217")
                .roles(new HashSet<>(Set.of(Role.USER, Role.DONOR)))
                .isActive(true)
                .build());

        DonorProfile donor = baseDonorRepo.save(DonorProfile.builder()
                .user(user)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .city("Mumbai")
                .state("Maharashtra")
                .latitude(new BigDecimal("19.0760"))
                .longitude(new BigDecimal("72.8777"))
                .isAvailable(true)
                .reliabilityScore(new BigDecimal("80.00"))
                .totalDonations(3)
                .responseRate(new BigDecimal("100.00"))
                .build());

        String token = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());

        mockMvc.perform(delete("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("ACCOUNT_DELETED"));

        User anonymized = baseUserRepo.findById(user.getId()).orElseThrow();
        assertThat(anonymized.getName()).isEqualTo("Deleted User " + user.getId());
        assertThat(anonymized.getEmail()).isEqualTo("deleted_" + user.getId() + "@anonymized.local");
        assertThat(anonymized.getIsActive()).isFalse();

        DonorProfile anonymizedDonor = baseDonorRepo.findById(donor.getId()).orElseThrow();
        assertThat(anonymizedDonor.isAvailable()).isFalse();
        assertThat(anonymizedDonor.getCity()).isEqualTo("Anonymized");
    }
}
