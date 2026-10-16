package com.blood_donor.controller;

import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.request.auth_request.RefreshTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.blood_donor.model.dto.request.auth_request.DeleteAccountRequest;
import com.blood_donor.model.entity.User;
import com.blood_donor.common.enums.Role;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Auth Controller Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired private MockMvc       mockMvc;
    @Autowired private ObjectMapper  objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider tokenProvider;

    @MockitoBean
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("unchecked")
    private org.springframework.data.redis.core.ValueOperations<String, Object> valueOperations =
            org.mockito.Mockito.mock(org.springframework.data.redis.core.ValueOperations.class);

    @Test
    @DisplayName("POST /api/auth/register â€” successful registration")
    void shouldRegisterNewUserSuccessfully() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Integration Test User");
        req.setEmail("integration" + System.nanoTime() + "@test.com");
        req.setPassword("Password@123");
        req.setPhone("9876543210");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(req.getEmail()))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/register â€” invalid email format should return 400")
    void shouldReturn400ForInvalidEmail() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test");
        req.setEmail("not-an-email");
        req.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login â€” wrong credentials return 401")
    void shouldReturn401ForWrongCredentials() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("notexist@test.com");
        req.setPassword("wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/auth/me â€” unauthenticated returns 401")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password â€” always returns 200")
    void shouldReturn200ForForgotPasswordRegardlessOfEmail() throws Exception {
        String body = """
            { "email": "doesnotexist@test.com" }
            """;

        // Security: same response whether email exists or not
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/auth/refresh — successful token refresh")
    void shouldRefreshTokenSuccessfully() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Refresh User");
        req.setEmail("refresh" + System.nanoTime() + "@test.com");
        req.setPassword("Password@123");
        req.setPhone("9876543211");

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(responseBody).path("data").path("refreshToken").asText();

        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/refresh — blank refresh token should return 400")
    void shouldReturn400WhenRefreshTokenIsBlank() throws Exception {
        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken("");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/refresh — invalid refresh token should return 401")
    void shouldReturn401WhenRefreshTokenIsInvalid() throws Exception {
        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken("invalid-refresh-token-value");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/logout — successful logout blacklists token")
    void shouldLogoutAndRevokeTokensSuccessfully() throws Exception {
        // Stub RedisTemplate operations
        org.mockito.Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        org.mockito.Mockito.doNothing().when(valueOperations).set(
                org.mockito.Mockito.anyString(),
                org.mockito.Mockito.any(),
                org.mockito.Mockito.anyLong(),
                org.mockito.Mockito.any(java.util.concurrent.TimeUnit.class)
        );

        RegisterRequest req = new RegisterRequest();
        req.setName("Logout Test User");
        req.setEmail("logout" + System.nanoTime() + "@test.com");
        req.setPassword("Password@123");
        req.setPhone("9876543212");

        // Register user and get tokens
        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();
        String refreshToken = objectMapper.readTree(responseBody).path("data").path("refreshToken").asText();

        // Verify we can access a protected endpoint before logout
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Perform logout
        RefreshTokenRequest logoutReq = new RefreshTokenRequest();
        logoutReq.setRefreshToken(refreshToken);

        // Before calling logout, stub hasKey to return false
        org.mockito.Mockito.when(redisTemplate.hasKey("blacklist:token:" + accessToken)).thenReturn(false);
        org.mockito.Mockito.when(redisTemplate.hasKey("blacklist:token:" + refreshToken)).thenReturn(false);

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("LOGGED_OUT"));

        // Stub hasKey to return true for the revoked tokens
        org.mockito.Mockito.when(redisTemplate.hasKey("blacklist:token:" + accessToken)).thenReturn(true);
        org.mockito.Mockito.when(redisTemplate.hasKey("blacklist:token:" + refreshToken)).thenReturn(true);

        // Verify that access token is now blacklisted (returns 401)
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());

        // Verify that refresh token is now blacklisted (returns 401)
        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/auth/me — successful deletion with valid password")
    void shouldDeleteUserAccountSuccessfullyWithValidPassword() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Delete Success User");
        req.setEmail("del-success" + System.nanoTime() + "@test.com");
        req.setPassword("Password@123");
        req.setPhone("9876543213");

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();

        DeleteAccountRequest deleteReq = new DeleteAccountRequest("Password@123");

        mockMvc.perform(delete("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("ACCOUNT_DELETED"));

        // Verify user can no longer log in
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(req.getEmail());
        loginReq.setPassword("Password@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/auth/me — rejected with 401 on wrong password")
    void shouldFailToDeleteUserAccountWithInvalidPassword() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Delete Wrong Pass User");
        req.setEmail("del-wrong" + System.nanoTime() + "@test.com");
        req.setPassword("Password@123");
        req.setPhone("9876543214");

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();

        DeleteAccountRequest deleteReq = new DeleteAccountRequest("WrongPassword!123");

        mockMvc.perform(delete("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("DELETE /api/auth/me — rejected with 400 on blank password")
    void shouldFailToDeleteUserAccountWithBlankPassword() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Delete Blank Pass User");
        req.setEmail("del-blank" + System.nanoTime() + "@test.com");
        req.setPassword("Password@123");
        req.setPhone("9876543215");

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();

        DeleteAccountRequest deleteReq = new DeleteAccountRequest("");

        mockMvc.perform(delete("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/register — mass-assignment protection ensures role remains strictly USER")
    void shouldPreventMassAssignmentPrivilegeEscalationOnRegister() throws Exception {
        String payloadWithRoles = """
            {
                "name": "Attacker User",
                "email": "attacker%d@test.com",
                "password": "Password@123",
                "phone": "9876543216",
                "role": "ADMIN",
                "roles": ["ADMIN", "HOSPITAL"]
            }
            """.formatted(System.nanoTime());

        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWithRoles))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.roles[0]").value("USER"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();

        // Verify calling admin endpoint fails with 403 Forbidden
        mockMvc.perform(get("/api/admin/dashboard/stats")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/auth/me — rejects deletion of last active admin with 403")
    void shouldPreventLastActiveAdminFromDeletingAccount() throws Exception {
        long adminCount = userRepository.countByRoleAndIsActiveTrue(Role.ADMIN);
        User admin;
        if (adminCount == 0) {
            admin = User.builder()
                    .name("Sole Admin")
                    .email("soleadmin" + System.nanoTime() + "@test.com")
                    .password(passwordEncoder.encode("Password@123"))
                    .phone("9876543217")
                    .roles(new HashSet<>(Set.of(Role.USER, Role.ADMIN)))
                    .isActive(true)
                    .build();
            userRepository.save(admin);
        } else {
            admin = userRepository.findAll().stream()
                    .filter(u -> u.hasRole(Role.ADMIN) && Boolean.TRUE.equals(u.getIsActive()))
                    .findFirst()
                    .orElseThrow();
            userRepository.findAll().stream()
                    .filter(u -> u.hasRole(Role.ADMIN) && !u.getId().equals(admin.getId()))
                    .forEach(u -> {
                        u.setIsActive(false);
                        userRepository.save(u);
                    });
        }

        admin.setPassword(passwordEncoder.encode("Password@123"));
        userRepository.save(admin);

        String adminToken = tokenProvider.generateAccessToken(
                admin.getEmail(),
                admin.getRoles(),
                admin.getId()
        );

        DeleteAccountRequest deleteReq = new DeleteAccountRequest("Password@123");

        mockMvc.perform(delete("/api/auth/me")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteReq)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}