package com.blood_donor.security;

import com.blood_donor.monitoring.CustomMetricsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Trusted Proxy & X-Forwarded-For Rate Limiting Spoofing Protection Test")
public class TrustedProxyAndRateLimitSpoofingTest {

    private RateLimitingFilter rateLimitingFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private CustomMetricsService customMetricsService;

    @BeforeEach
    void setUp() {
        rateLimitingFilter = new RateLimitingFilter();
        // Configure low capacity for testing: capacity = 3 attempts
        ReflectionTestUtils.setField(rateLimitingFilter, "enabled", true);
        ReflectionTestUtils.setField(rateLimitingFilter, "loginIpCapacity", 3L);
        ReflectionTestUtils.setField(rateLimitingFilter, "refillIntervalSeconds", 60L);
        ReflectionTestUtils.setField(rateLimitingFilter, "customMetricsService", customMetricsService);
    }

    @Test
    @DisplayName("Attacker rotating spoofed X-Forwarded-For headers from the same remoteAddr is still blocked (cannot bypass rate limit)")
    void testSpoofedXForwardedForCannotBypassRateLimiter() throws ServletException, IOException {
        String attackerRealIp = "192.168.1.150";

        // Request 1: Attacker sends spoofed header X-Forwarded-For: 1.1.1.1
        MockHttpServletRequest req1 = new MockHttpServletRequest("POST", "/api/auth/login");
        req1.setRemoteAddr(attackerRealIp);
        req1.addHeader("X-Forwarded-For", "1.1.1.1");
        req1.setContent("{\"email\":\"user1@test.com\",\"password\":\"wrong\"}".getBytes());
        MockHttpServletResponse res1 = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(req1, res1, filterChain);
        assertThat(res1.getStatus()).isEqualTo(200); // Token consumed, allowed through

        // Request 2: Attacker sends spoofed header X-Forwarded-For: 2.2.2.2
        MockHttpServletRequest req2 = new MockHttpServletRequest("POST", "/api/auth/login");
        req2.setRemoteAddr(attackerRealIp);
        req2.addHeader("X-Forwarded-For", "2.2.2.2");
        req2.setContent("{\"email\":\"user2@test.com\",\"password\":\"wrong\"}".getBytes());
        MockHttpServletResponse res2 = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(req2, res2, filterChain);
        assertThat(res2.getStatus()).isEqualTo(200); // Token consumed, allowed through

        // Request 3: Attacker sends spoofed header X-Forwarded-For: 3.3.3.3
        MockHttpServletRequest req3 = new MockHttpServletRequest("POST", "/api/auth/login");
        req3.setRemoteAddr(attackerRealIp);
        req3.addHeader("X-Forwarded-For", "3.3.3.3");
        req3.setContent("{\"email\":\"user3@test.com\",\"password\":\"wrong\"}".getBytes());
        MockHttpServletResponse res3 = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(req3, res3, filterChain);
        assertThat(res3.getStatus()).isEqualTo(200); // 3rd token consumed

        // Request 4: Attacker sends spoofed header X-Forwarded-For: 4.4.4.4
        // If spoofing worked, this would pass as a new IP. But because remoteAddr is pinned, it MUST return 429!
        MockHttpServletRequest req4 = new MockHttpServletRequest("POST", "/api/auth/login");
        req4.setRemoteAddr(attackerRealIp);
        req4.addHeader("X-Forwarded-For", "4.4.4.4");
        req4.setContent("{\"email\":\"user4@test.com\",\"password\":\"wrong\"}".getBytes());
        MockHttpServletResponse res4 = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(req4, res4, filterChain);

        assertThat(res4.getStatus()).isEqualTo(429);
        assertThat(res4.getContentAsString()).contains("Too many requests");
        verify(customMetricsService).recordRateLimit429();
    }
}
