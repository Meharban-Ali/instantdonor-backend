package com.blood_donor.integration;

import com.blood_donor.config.ProductionStartupValidator;
import com.blood_donor.monitoring.CustomMetricsService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Phase 4 Observability, Fail-Fast Validation & Metrics Integration Test")
public class RealDatabasePhase4ObservabilityTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomMetricsService customMetricsService;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    @DisplayName("TraceIdFilter: Generates X-Request-Id when missing and echoes provided trace ID")
    void testTraceIdFilterGeneratesAndEchoesTraceId() {
        // Case 1: Client provides custom trace ID
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", "custom-trace-uuid-9999");
        HttpEntity<Void> requestWithTrace = new HttpEntity<>(headers);

        ResponseEntity<String> response1 = restTemplate.exchange("/actuator/health", HttpMethod.GET, requestWithTrace, String.class);
        assertThat(response1.getHeaders().getFirst("X-Request-Id")).isEqualTo("custom-trace-uuid-9999");

        // Case 2: Client provides no trace ID -> Server generates a UUID
        ResponseEntity<String> response2 = restTemplate.getForEntity("/actuator/health", String.class);
        String generatedTraceId = response2.getHeaders().getFirst("X-Request-Id");
        assertThat(generatedTraceId).isNotNull().isNotBlank();
        assertThat(generatedTraceId.length()).isGreaterThanOrEqualTo(16);
    }

    @Test
    @DisplayName("Micrometer Custom Metrics: All custom counters increment correctly in MeterRegistry")
    void testCustomMetricsCountersIncrement() {
        double authFailBefore = meterRegistry.get("auth.failures").counter().count();
        customMetricsService.recordAuthFailure();
        assertThat(meterRegistry.get("auth.failures").counter().count()).isEqualTo(authFailBefore + 1.0);

        double rateLimitBefore = meterRegistry.get("ratelimit.throttled").counter().count();
        customMetricsService.recordRateLimit429();
        assertThat(meterRegistry.get("ratelimit.throttled").counter().count()).isEqualTo(rateLimitBefore + 1.0);

        double aiFallbackBefore = meterRegistry.get("ai.matching.fallback").counter().count();
        customMetricsService.recordAiFallback();
        assertThat(meterRegistry.get("ai.matching.fallback").counter().count()).isEqualTo(aiFallbackBefore + 1.0);

        double smsSuccessBefore = meterRegistry.get("sms.delivery.success").counter().count();
        customMetricsService.recordSmsSuccess();
        assertThat(meterRegistry.get("sms.delivery.success").counter().count()).isEqualTo(smsSuccessBefore + 1.0);

        double smsFailureBefore = meterRegistry.get("sms.delivery.failure").counter().count();
        customMetricsService.recordSmsFailure();
        assertThat(meterRegistry.get("sms.delivery.failure").counter().count()).isEqualTo(smsFailureBefore + 1.0);
    }

    @Test
    @DisplayName("Actuator Security: /actuator/health is public, /actuator/metrics is protected")
    void testActuatorSecuritySeparation() {
        // Public health endpoint
        ResponseEntity<String> healthResp = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(healthResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(healthResp.getBody()).contains("UP");

        // Protected metrics endpoint without auth -> 401 or 403
        ResponseEntity<String> metricsResp = restTemplate.getForEntity("/actuator/metrics", String.class);
        assertThat(metricsResp.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("ProductionStartupValidator: Fail-fast on short/missing JWT secret or missing prod env variables")
    void testProductionStartupValidatorEnforcesRequirements() {
        // 1. Missing JWT secret
        MockEnvironment envDev = new MockEnvironment();
        ProductionStartupValidator validator1 = new ProductionStartupValidator(envDev);
        ReflectionTestUtils.setField(validator1, "jwtSecret", "");
        assertThatThrownBy(() -> validator1.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("FATAL STARTUP ERROR: JWT secret");

        // 2. JWT secret too short (< 32 bytes)
        ProductionStartupValidator validator2 = new ProductionStartupValidator(envDev);
        ReflectionTestUtils.setField(validator2, "jwtSecret", "short_secret_under_32_bytes");
        assertThatThrownBy(() -> validator2.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("FATAL STARTUP ERROR: JWT secret is too short");

        // 3. Valid secret in dev profile passes cleanly
        ProductionStartupValidator validator3 = new ProductionStartupValidator(envDev);
        ReflectionTestUtils.setField(validator3, "jwtSecret", "ThisIsAVerySecureSecretKeyWithAtLeastFortyEightBytesForSha384!");
        validator3.run(new DefaultApplicationArguments()); // Does not throw

        // 4. In prod profile, missing DB/Redis properties cause fail-fast
        MockEnvironment envProd = new MockEnvironment();
        envProd.setActiveProfiles("prod");
        ProductionStartupValidator validator4 = new ProductionStartupValidator(envProd);
        ReflectionTestUtils.setField(validator4, "jwtSecret", "ThisIsAVerySecureSecretKeyWithAtLeastFortyEightBytesForSha384!");
        assertThatThrownBy(() -> validator4.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Missing required production environment variables");
    }
}
