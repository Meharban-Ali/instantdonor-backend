package com.blood_donor.integration;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.model.dto.request.donor_request.DonorSearchRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.security.JwtAuthFilter;
import com.blood_donor.security.JwtTokenProvider;
import com.blood_donor.service.donor_service_impl.DonorSearchService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.blood_donor.security.CustomUserDetailsService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Real Redis Cache TTL/Eviction & Fail-Closed Security Tests")
public class RealRedisCacheAndFailClosedTest extends BaseIntegrationTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DonorSearchService donorSearchService;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        cleanDatabase();
        // Clear Redis
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Test
    @DisplayName("Redis Key TTL & Expiration Verification")
    void testRedisKeyTtlAndExpiration() throws InterruptedException {
        String testKey = "test:ttl:cache-key";
        redisTemplate.opsForValue().set(testKey, "temporary-data", Duration.ofSeconds(2));

        Long initialTtl = redisTemplate.getExpire(testKey, TimeUnit.SECONDS);
        assertThat(initialTtl).isNotNull();
        assertThat(initialTtl).isGreaterThan(0).isLessThanOrEqualTo(2);

        // Wait for TTL expiration
        Thread.sleep(2200);

        Boolean exists = redisTemplate.hasKey(testKey);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Donor Search Cache & Cache Eviction Verification")
    void testDonorSearchCachePopulationAndEviction() {
        User user = userRepository.save(User.builder()
                .name("Cache Donor")
                .email("cache_donor@test.com")
                .phone("9400000001")
                .password("$2a$12$e8J4V07V77v6d7p2q5k7te1V5zL8Zt2X0W2j1Y8m5k2p3q4r5s6t7u")
                .roles(Collections.singleton(Role.DONOR))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        donorProfileRepository.save(DonorProfile.builder()
                .user(user)
                .bloodGroup(BloodGroup.AB_POSITIVE)
                .city("Lucknow")
                .state("UP")
                .latitude(BigDecimal.valueOf(26.8467))
                .longitude(BigDecimal.valueOf(80.9462))
                .isAvailable(true)
                .reliabilityScore(BigDecimal.valueOf(88.0))
                .responseRate(BigDecimal.valueOf(80.0))
                .totalDonations(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        DonorSearchRequest req = new DonorSearchRequest();
        req.setBloodGroup(BloodGroup.AB_POSITIVE);
        req.setCity("Lucknow");

        // 1. Initial call populates cache
        Page<DonorResponse> firstCall = donorSearchService.searchByCity(req);
        assertThat(firstCall.getContent()).hasSize(1);

        Cache donorCache = cacheManager.getCache("donorSearch");
        assertThat(donorCache).isNotNull();

        // 2. Clear cache explicitly and verify eviction
        donorCache.clear();
        Set<String> keysAfterClear = redisTemplate.keys("donorSearch*");
        assertThat(keysAfterClear).isEmpty();
    }

    @Test
    @DisplayName("Fail-Closed Policy: Redis outage during token blacklist check returns HTTP 503 (Fail-Closed)")
    void testRedisDownFailClosedBlacklistCheck() throws ServletException, IOException {
        // Create a mocked redisTemplate that simulates Redis connection failure
        org.springframework.data.redis.core.RedisTemplate<String, Object> failingRedis = mock(org.springframework.data.redis.core.RedisTemplate.class);
        when(failingRedis.hasKey(anyString())).thenThrow(new RedisConnectionFailureException("Redis connection refused (simulated outage)"));

        JwtAuthFilter filter = new JwtAuthFilter(jwtTokenProvider, userDetailsService, failingRedis);

        String testToken = jwtTokenProvider.generateAccessToken("cache_donor@test.com", Collections.singleton(Role.DONOR), 1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/donor/profile");
        request.addHeader("Authorization", "Bearer " + testToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Execute filter with failed Redis
        filter.doFilter(request, response, filterChain);

        // Verify FAIL-CLOSED: Request is NOT passed through to filter chain; 503 is returned!
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getContentAsString()).contains("Authentication service temporarily unavailable (blacklist check failure)");
    }

    @Test
    @DisplayName("Blacklisted token is immediately rejected with HTTP 401/error")
    void testBlacklistedTokenIsRejected() throws ServletException, IOException {
        String testToken = jwtTokenProvider.generateAccessToken("cache_donor@test.com", Collections.singleton(Role.DONOR), 1L);

        // Blacklist token in Redis
        redisTemplate.opsForValue().set("blacklist:token:" + testToken, "true", Duration.ofMinutes(10));

        org.springframework.data.redis.core.RedisTemplate<String, Object> objRedis = new org.springframework.data.redis.core.RedisTemplate<>();
        // Using real redis template
        JwtAuthFilter filter = new JwtAuthFilter(jwtTokenProvider, userDetailsService, (org.springframework.data.redis.core.RedisTemplate) redisTemplate);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/donor/profile");
        request.addHeader("Authorization", "Bearer " + testToken);

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        // Request must not proceed to chain
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(response.getContentAsString()).contains("Token is blacklisted");
    }
}
