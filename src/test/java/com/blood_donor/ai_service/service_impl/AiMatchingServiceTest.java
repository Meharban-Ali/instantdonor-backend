package com.blood_donor.ai_service.service_impl;

import com.blood_donor.ai_service.config.AiServiceErrorHandler;
import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.properties.AiServiceProperties;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.monitoring.CustomMetricsService;
import com.blood_donor.notification.properties.NotificationProperties;
import com.blood_donor.notification.service_Impl.AiDonorMatchingStrategy;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class AiMatchingServiceTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private AiServiceProperties aiProperties;
    private RetryTemplate retryTemplate;
    private AiMatchingService aiMatchingService;

    @Mock
    private DonorProfileRepository donorRepo;

    @BeforeEach
    void setUp() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new AiServiceErrorHandler());
        this.mockServer = MockRestServiceServer.bindTo(this.restTemplate).ignoreExpectOrder(true).build();

        this.aiProperties = new AiServiceProperties();
        this.aiProperties.setUrl("http://ai-service:8000");
        this.aiProperties.setApiKey("test-secret-api-key");
        this.aiProperties.setMaxRetries(1);
        this.aiProperties.setRetryDelayMs(50);

        this.retryTemplate = new RetryTemplate();
        this.retryTemplate.setRetryPolicy(new SimpleRetryPolicy(1));

        this.aiMatchingService = new AiMatchingService(
                this.restTemplate,
                this.retryTemplate,
                this.aiProperties
        );
    }

    @Test
    @DisplayName("Verify X-API-KEY header is sent to AI Service on match-donors request")
    void testFindBestDonors_SendsXApiKeyHeader() {
        String jsonResponse = """
            {
              "blood_group": "A+",
              "urgency": "NORMAL",
              "total_found": 0,
              "donors": [],
              "processing_time_ms": 12.5,
              "algorithm_version": "2.0.0",
              "cached": false
            }
            """;

        this.mockServer.expect(requestTo("http://ai-service:8000/api/v1/match-donors"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-API-KEY", "test-secret-api-key"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        AiMatchRequest matchRequest = AiMatchRequest.from(
                BloodGroup.A_POSITIVE, 28.6139, 77.2090, 25.0, UrgencyLevel.NORMAL, 2, 5, 101L
        );

        AiMatchResponse response = this.aiMatchingService.findBestDonors(matchRequest);
        assertNotNull(response);
        assertEquals("A+", response.getBloodGroup());
        this.mockServer.verify();
    }

    @Test
    @DisplayName("Verify 401 Unauthorized from AI service throws ApiException")
    void testFindBestDonors_Unauthorized_ThrowsApiException() {
        this.mockServer.expect(requestTo("http://ai-service:8000/api/v1/match-donors"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-API-KEY", "test-secret-api-key"))
                .andRespond(withUnauthorizedRequest());

        AiMatchRequest matchRequest = AiMatchRequest.from(
                BloodGroup.A_POSITIVE, 28.6139, 77.2090, 25.0, UrgencyLevel.NORMAL, 2, 5, 102L
        );

        assertThrows(ApiException.class, () -> this.aiMatchingService.findBestDonors(matchRequest));
        this.mockServer.verify();
    }

    @Test
    @DisplayName("Verify 401 triggers SQL City fallback and increments ai.matching.fallback metric")
    void testStrategy_FallsBackToCityAndIncrementsMetricOn401() {
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        CustomMetricsService metrics = new CustomMetricsService(registry);

        NotificationProperties notifProps = new NotificationProperties();
        notifProps.setFallbackEnabled(true);
        notifProps.setSearchRadiusKm(25.0);

        AiMatchingService failingAiService = mock(AiMatchingService.class);
        when(failingAiService.findBestDonors(any())).thenThrow(ApiException.badRequest("Invalid or missing API Key"));

        User user = new User();
        user.setId(55L);
        user.setName("Test Donor");
        user.setPhone("9876543210");

        DonorProfile donor = new DonorProfile();
        donor.setId(10L);
        donor.setUser(user);
        donor.setBloodGroup(BloodGroup.A_POSITIVE);
        donor.setCity("Delhi");
        donor.setAvailable(true);
        donor.setReliabilityScore(BigDecimal.valueOf(95.0));

        when(this.donorRepo.findCityDonorsWithLimit(anyList(), eq("Delhi"), eq(5)))
                .thenReturn(List.of(donor));

        AiDonorMatchingStrategy strategy = new AiDonorMatchingStrategy(
                failingAiService,
                this.donorRepo,
                notifProps,
                metrics
        );

        BloodRequest bloodRequest = new BloodRequest();
        bloodRequest.setId(999L);
        bloodRequest.setBloodGroup(BloodGroup.A_POSITIVE);
        bloodRequest.setLatitude(BigDecimal.valueOf(28.6139));
        bloodRequest.setLongitude(BigDecimal.valueOf(77.2090));
        bloodRequest.setCity("Delhi");
        bloodRequest.setUrgency(UrgencyLevel.NORMAL);
        bloodRequest.setUnitsRequired(1);

        List<AiDonorResult> results = strategy.findDonors(bloodRequest, 5);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(10L, results.get(0).getDonorId());

        // Verify Metric incremented
        double fallbackCount = registry.get("ai.matching.fallback").counter().count();
        assertEquals(1.0, fallbackCount, "AI fallback counter must be incremented to 1.0 upon AI service 401");
    }

    @Test
    @DisplayName("Verify GET /health is public and does not require X-API-KEY header")
    void testIsAiServiceHealthy_CallsHealthWithoutApiKeyHeader() {
        this.mockServer.expect(requestTo("http://ai-service:8000/health"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"status\":\"healthy\"}", MediaType.APPLICATION_JSON));

        boolean healthy = this.aiMatchingService.isAiServiceHealthy();

        assertTrue(healthy);
        this.mockServer.verify();
    }
}
