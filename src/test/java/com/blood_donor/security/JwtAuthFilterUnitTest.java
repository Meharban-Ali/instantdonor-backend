package com.blood_donor.security;

import com.blood_donor.common.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter Fail-Open & Resiliency Unit Tests")
class JwtAuthFilterUnitTest {

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private FilterChain filterChain;

    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthFilter filter;

    private static final String SECRET = "YourSuperSecretJWTKeyThatIsAtLeast256BitsLongAndSecure1234567890!";
    private static final long EXPIRATION = 3600000;
    private static final long REFRESH_EXPIRATION = 604800000;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpiration", REFRESH_EXPIRATION);
        filter = new JwtAuthFilter(jwtTokenProvider, userDetailsService, redisTemplate);
    }

    @Test
    @DisplayName("Redis outage during token blacklist check allows request through (fail-open) without 503")
    void testRedisDownFailOpen() throws ServletException, IOException {
        when(redisTemplate.hasKey(anyString())).thenThrow(new RedisConnectionFailureException("Redis is down"));

        String testToken = jwtTokenProvider.generateAccessToken("admin@test.com", Collections.singleton(Role.ADMIN), 1L);

        UserDetails userDetails = new User(
                "admin@test.com", "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/admin/dashboard");
        request.addHeader("Authorization", "Bearer " + testToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(503);
    }
}
