package com.blood_donor.controller;

import com.blood_donor.common.enums.Role;
import com.blood_donor.model.dto.forgetpassword.ChangePasswordRequest;
import com.blood_donor.model.dto.request.auth_request.LoginRequest;
import com.blood_donor.model.dto.request.auth_request.RefreshTokenRequest;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Change Password Session Revocation Integration Tests")
class ChangePasswordRevocationIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider tokenProvider;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    private final Map<String, Object> kvStore = new ConcurrentHashMap<>();
    private final Map<String, Set<Object>> setStore = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    private final ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);
    @SuppressWarnings("unchecked")
    private final SetOperations<String, Object> setOperations = Mockito.mock(SetOperations.class);

    @BeforeEach
    void setUpRedisMocks() {
        kvStore.clear();
        setStore.clear();

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(redisTemplate.opsForSet()).thenReturn(setOperations);

        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            Object val = inv.getArgument(1);
            kvStore.put(key, val);
            return null;
        }).when(valueOperations).set(anyString(), any(), anyLong(), any());

        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            return kvStore.get(key);
        }).when(valueOperations).get(anyString());

        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            return kvStore.containsKey(key);
        }).when(redisTemplate).hasKey(anyString());

        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            Object[] vals = inv.getArguments();
            Set<Object> s = setStore.computeIfAbsent(key, k -> new HashSet<>());
            for (int i = 1; i < vals.length; i++) {
                s.add(vals[i]);
            }
            return (long) (vals.length - 1);
        }).when(setOperations).add(anyString(), any());

        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            return setStore.getOrDefault(key, Collections.emptySet());
        }).when(setOperations).members(anyString());

        Mockito.doAnswer(inv -> {
            String key = inv.getArgument(0);
            kvStore.remove(key);
            setStore.remove(key);
            return true;
        }).when(redisTemplate).delete(anyString());
    }

    @Test
    @DisplayName("Password Change: Revokes all refresh tokens, blacklists access token, allows new password login")
    void testChangePasswordRevocationFlow() throws Exception {
        String email = "changepass_user_" + System.currentTimeMillis() + "@example.com";
        String oldPassword = "OldPassword@123";
        String newPassword = "NewPassword@456";

        User user = User.builder()
                .name("Revoke Test User")
                .email(email)
                .password(passwordEncoder.encode(oldPassword))
                .phone("9998887771")
                .roles(new HashSet<>(Set.of(Role.USER)))
                .isActive(true)
                .build();
        user = userRepository.saveAndFlush(user);

        // 1. Initial login with old password -> get tokens
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail(email);
        loginReq.setPassword(oldPassword);

        String loginResStr = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> resMap = objectMapper.readValue(loginResStr, Map.class);
        Map<?, ?> dataMap = (Map<?, ?>) resMap.get("data");
        String oldAccessToken = (String) dataMap.get("accessToken");
        String oldRefreshToken = (String) dataMap.get("refreshToken");

        // 2. Change password successfully using oldAccessToken
        ChangePasswordRequest changeReq = new ChangePasswordRequest();
        changeReq.setCurrentPassword(oldPassword);
        changeReq.setNewPassword(newPassword);

        mockMvc.perform(put("/api/auth/change-password")
                        .header("Authorization", "Bearer " + oldAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        // 3. Try to use old refresh token -> Expect 401 Unauthorized (revoked)
        RefreshTokenRequest refreshReq = new RefreshTokenRequest();
        refreshReq.setRefreshToken(oldRefreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isUnauthorized());

        // 4. Try to use old access token on /api/auth/me -> Expect 401 Unauthorized (blacklisted / tokens_invalid_before)
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + oldAccessToken))
                .andExpect(status().isUnauthorized());

        // 5. Login with old password -> Expect 401 BadCredentials
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized());

        // 6. Login with new password -> Expect 200 OK
        LoginRequest newLoginReq = new LoginRequest();
        newLoginReq.setEmail(email);
        newLoginReq.setPassword(newPassword);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLoginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }
}
