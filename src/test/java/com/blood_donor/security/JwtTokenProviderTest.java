package com.blood_donor.security;

import com.blood_donor.common.enums.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET = "TestSecretKey_AtLeast32CharactersLong_ForTesting!";
    private static final long ACCESS_EXPIRY = 86400000L;   // 24h
    private static final long REFRESH_EXPIRY = 604800000L;  // 7d

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", ACCESS_EXPIRY);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpiration", REFRESH_EXPIRY);
    }

    @Test
    @DisplayName("Access token generate aur validate hona chahiye, claims verify hone chahiye")
    void shouldGenerateAndValidateAccessToken() {
        String email = "test@blooddonor.com";
        Set<Role> roles = Set.of(Role.DONOR);
        Long userId = 1L;

        String token = jwtTokenProvider.generateAccessToken(email, roles, userId);

        // Basic assertions
        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token, email)).isTrue();
        
        // Claim extraction assertions
        assertThat(jwtTokenProvider.extractEmail(token)).isEqualTo(email);
        assertThat(jwtTokenProvider.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtTokenProvider.extractRoles(token)).containsExactlyInAnyOrder("DONOR");
    }

    @Test
    @DisplayName("Refresh token generate aur validate hona chahiye")
    void shouldGenerateRefreshToken() {
        String email = "donor@test.com";

        String token = jwtTokenProvider.generateRefreshToken(email);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token, email)).isTrue();
        assertThat(jwtTokenProvider.extractEmail(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("Galat email se token validate nahi hona chahiye")
    void shouldNotValidateTokenWithWrongEmail() {
        String token = jwtTokenProvider.generateAccessToken(
            "correct@test.com", Set.of(Role.USER), 1L
        );

        assertThat(jwtTokenProvider.validateToken(token, "wrong@test.com")).isFalse();
    }

    @Test
    @DisplayName("Expired token validate nahi hona chahiye (Negative Expiration)")
    void shouldNotValidateExpiredToken() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret", SECRET);
        // Optimization: Set expiration to past (negative) to avoid flaky Thread.sleep()
        ReflectionTestUtils.setField(expiredProvider, "jwtExpiration", -10000L); // -10s
        ReflectionTestUtils.setField(expiredProvider, "refreshExpiration", REFRESH_EXPIRY);

        String token = expiredProvider.generateAccessToken("test@test.com", Set.of(Role.USER), 1L);

        assertThat(expiredProvider.validateToken(token, "test@test.com")).isFalse();
    }

    @Test
    @DisplayName("Expired token se claims extract karne par ExpiredJwtException throw hona chahiye")
    void shouldThrowExceptionWhenExtractingFromExpiredToken() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(expiredProvider, "jwtExpiration", -10000L); // -10s
        ReflectionTestUtils.setField(expiredProvider, "refreshExpiration", REFRESH_EXPIRY);

        String token = expiredProvider.generateAccessToken("test@test.com", Set.of(Role.USER), 1L);

        assertThatThrownBy(() -> expiredProvider.extractEmail(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Tampered token validate nahi hona chahiye")
    void shouldNotValidateTamperedToken() {
        String token = jwtTokenProvider.generateAccessToken("test@test.com", Set.of(Role.USER), 1L);
        String tampered = token + "tampered";

        assertThat(jwtTokenProvider.validateToken(tampered, "test@test.com")).isFalse();
    }

    @Test
    @DisplayName("Tampered token se claims extract karne par JwtException throw hona chahiye")
    void shouldThrowExceptionWhenExtractingFromTamperedToken() {
        String token = jwtTokenProvider.generateAccessToken("test@test.com", Set.of(Role.USER), 1L);
        String tampered = token + "tampered";

        assertThatThrownBy(() -> jwtTokenProvider.extractEmail(tampered))
                .isInstanceOf(JwtException.class);
    }
}
