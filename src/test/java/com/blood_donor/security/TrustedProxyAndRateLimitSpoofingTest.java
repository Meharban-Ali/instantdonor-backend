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

    @Test
    @DisplayName("Distinct forwarded clients via trusted proxy have independent rate-limit buckets")
    void testDistinctForwardedClientsHaveIndependentRateLimitBuckets() throws ServletException, IOException {
        String clientA_Ip = "203.0.113.10";
        String clientB_Ip = "203.0.113.20";

        // Client A consumes all 3 tokens
        for (int i = 1; i <= 3; i++) {
            MockHttpServletRequest reqA = new MockHttpServletRequest("POST", "/api/auth/login");
            reqA.setRemoteAddr(clientA_Ip);
            reqA.setContent(("{\"email\":\"clientA" + i + "@test.com\",\"password\":\"wrong\"}").getBytes());
            MockHttpServletResponse resA = new MockHttpServletResponse();
            rateLimitingFilter.doFilterInternal(reqA, resA, filterChain);
            assertThat(resA.getStatus()).isEqualTo(200);
        }

        // Client A's 4th attempt is rate-limited (429)
        MockHttpServletRequest reqA_Blocked = new MockHttpServletRequest("POST", "/api/auth/login");
        reqA_Blocked.setRemoteAddr(clientA_Ip);
        reqA_Blocked.setContent("{\"email\":\"clientA4@test.com\",\"password\":\"wrong\"}".getBytes());
        MockHttpServletResponse resA_Blocked = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(reqA_Blocked, resA_Blocked, filterChain);
        assertThat(resA_Blocked.getStatus()).isEqualTo(429);

        // Client B is completely unaffected: separate bucket with full tokens available
        MockHttpServletRequest reqB = new MockHttpServletRequest("POST", "/api/auth/login");
        reqB.setRemoteAddr(clientB_Ip);
        reqB.setContent("{\"email\":\"clientB@test.com\",\"password\":\"wrong\"}".getBytes());
        MockHttpServletResponse resB = new MockHttpServletResponse();
        rateLimitingFilter.doFilterInternal(reqB, resB, filterChain);
        assertThat(resB.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("internal-proxies regex strictly trusts only Docker Compose Nginx exact IP (172.28.0.10) and loopback")
    void testInternalProxiesRegexMatchesOnlyComposeNginxSubnetAndLoopback() {
        String internalProxiesPattern = "127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|172\\.28\\.0\\.10";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(internalProxiesPattern);

        // Trusted proxies: exact compose nginx IP and loopback
        assertThat(pattern.matcher("172.28.0.10").matches()).isTrue();
        assertThat(pattern.matcher("127.0.0.1").matches()).isTrue();
        assertThat(pattern.matcher("0:0:0:0:0:0:0:1").matches()).isTrue();

        // Untrusted callers: other containers on 172.28 subnet, public IPs, and non-whitelisted private subnets
        assertThat(pattern.matcher("172.28.0.11").matches()).isFalse();
        assertThat(pattern.matcher("172.28.1.1").matches()).isFalse();
        assertThat(pattern.matcher("198.51.100.55").matches()).isFalse();
        assertThat(pattern.matcher("203.0.113.10").matches()).isFalse();
        assertThat(pattern.matcher("192.168.1.100").matches()).isFalse();
        assertThat(pattern.matcher("10.0.0.1").matches()).isFalse();
        assertThat(pattern.matcher("172.16.0.1").matches()).isFalse();
        assertThat(pattern.matcher("172.29.0.1").matches()).isFalse();
    }
}
