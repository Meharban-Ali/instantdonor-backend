package com.blood_donor.controller;

import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RegisterRequest;
import com.blood_donor.model.dto.request.auth_request.RefreshTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @org.mockito.Mock
    private org.springframework.data.redis.core.ValueOperations<String, Object> valueOperations;

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
}