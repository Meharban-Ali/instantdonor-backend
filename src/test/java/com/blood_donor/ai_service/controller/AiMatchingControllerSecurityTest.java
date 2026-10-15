package com.blood_donor.ai_service.controller;

import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.service.IAiMatchingService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AiMatchingControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private IAiMatchingService aiService;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("ROLE_USER calling /api/ai/match-donors returns 403 Forbidden")
    void testMatchDonors_ForbiddenForNormalUser() throws Exception {
        User regularUser = userRepository.save(User.builder()
                .name("Normal User")
                .email("user_test_" + System.nanoTime() + "@test.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("9800000010")
                .roles(Set.of(Role.USER))
                .isActive(true)
                .build());

        String userToken = jwtTokenProvider.generateAccessToken(
                regularUser.getEmail(), regularUser.getRoles(), regularUser.getId()
        );

        AiMatchRequest request = AiMatchRequest.from(
                BloodGroup.A_POSITIVE, 28.6139, 77.2090, 25.0, UrgencyLevel.NORMAL, 1, 5, 101L
        );

        mockMvc.perform(post("/api/ai/match-donors")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN calling /api/ai/match-donors succeeds with 200 OK")
    void testMatchDonors_AllowedForAdmin() throws Exception {
        User adminUser = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin_test_" + System.nanoTime() + "@test.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("9800000011")
                .roles(Set.of(Role.ADMIN))
                .isActive(true)
                .build());

        String adminToken = jwtTokenProvider.generateAccessToken(
                adminUser.getEmail(), adminUser.getRoles(), adminUser.getId()
        );

        AiMatchRequest request = AiMatchRequest.from(
                BloodGroup.A_POSITIVE, 28.6139, 77.2090, 25.0, UrgencyLevel.NORMAL, 1, 5, 102L
        );
        when(aiService.findBestDonors(any())).thenReturn(new AiMatchResponse());

        mockMvc.perform(post("/api/ai/match-donors")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_HOSPITAL calling /api/ai/match-donors succeeds with 200 OK")
    void testMatchDonors_AllowedForHospital() throws Exception {
        User hospitalUser = userRepository.save(User.builder()
                .name("Hospital User")
                .email("hospital_test_" + System.nanoTime() + "@test.com")
                .password(passwordEncoder.encode("Password@123"))
                .phone("9800000012")
                .roles(Set.of(Role.HOSPITAL))
                .isActive(true)
                .build());

        String hospitalToken = jwtTokenProvider.generateAccessToken(
                hospitalUser.getEmail(), hospitalUser.getRoles(), hospitalUser.getId()
        );

        AiMatchRequest request = AiMatchRequest.from(
                BloodGroup.A_POSITIVE, 28.6139, 77.2090, 25.0, UrgencyLevel.NORMAL, 1, 5, 103L
        );
        when(aiService.findBestDonors(any())).thenReturn(new AiMatchResponse());

        mockMvc.perform(post("/api/ai/match-donors")
                .header("Authorization", "Bearer " + hospitalToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
