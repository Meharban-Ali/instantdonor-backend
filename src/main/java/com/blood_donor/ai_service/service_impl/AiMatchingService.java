package com.blood_donor.ai_service.service_impl;

import com.blood_donor.ai_service.dto.request.AiDemandRequest;
import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDemandResponse;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.properties.AiServiceProperties;
import com.blood_donor.ai_service.service.IAiMatchingService;
import com.blood_donor.exception.ApiException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class AiMatchingService
implements IAiMatchingService {
    private static final Logger log = LoggerFactory.getLogger(AiMatchingService.class);
    private final RestTemplate aiServiceRestTemplate;
    private final RetryTemplate aiServiceRetryTemplate;
    private final AiServiceProperties aiProperties;

    public AiMatchingService(@Qualifier(value="aiServiceRestTemplate") RestTemplate aiServiceRestTemplate, @Qualifier(value="aiServiceRetryTemplate") RetryTemplate aiServiceRetryTemplate, AiServiceProperties aiProperties) {
        this.aiServiceRestTemplate = aiServiceRestTemplate;
        this.aiServiceRetryTemplate = aiServiceRetryTemplate;
        this.aiProperties = aiProperties;
    }

    @Override
    public AiMatchResponse findBestDonors(AiMatchRequest request) {
        String url = this.aiProperties.getUrl() + "/api/v1/match-donors";
        log.info("AI match request: url={}, bloodGroup={}, radius={}km, urgency={}", new Object[]{url, request.getBloodGroup(), request.getRadiusKm(), request.getUrgency()});
        try {
            AiMatchResponse response = (AiMatchResponse)this.aiServiceRetryTemplate.execute(context -> {
                if (context.getRetryCount() > 0) {
                    log.warn("AI match retry #{}: url={}", (Object)context.getRetryCount(), (Object)url);
                }
                HttpEntity<AiMatchRequest> entity = new HttpEntity<>(request, this.buildJsonHeaders());
                ResponseEntity resp = this.aiServiceRestTemplate.exchange(url, HttpMethod.POST, entity, AiMatchResponse.class, new Object[0]);
                if (!resp.getStatusCode().is2xxSuccessful()) {
                    throw ApiException.badRequest("AI matching failed with HTTP " + resp.getStatusCode().value());
                }
                return (AiMatchResponse)resp.getBody();
            });
            if (response == null) {
                log.error("AI service returned null response for match");
                throw ApiException.internalServerError("AI matching service returned empty response");
            }
            log.info("AI match complete: found={}, time={}ms, bloodGroup={}, cached={}", new Object[]{response.getTotalFound(), response.getProcessingTimeMs(), response.getBloodGroup(), response.getCached()});
            return response;
        }
        catch (ResourceAccessException e) {
            log.warn("AI service unreachable: url={}, error={}", (Object)url, (Object)e.getMessage());
            throw ApiException.internalServerError("AI matching service temporarily unavailable. Please try again.");
        }
        catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("AI matching unexpected error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("Donor matching failed. Please try again.");
        }
    }

    @Override
    public AiDemandResponse predictDemand(AiDemandRequest request) {
        String url = this.aiProperties.getUrl() + "/api/v1/predict-demand";
        log.info("AI prediction request: url={}, city={}, bloodGroup={}, days={}", new Object[]{url, request.getCity(), request.getBloodGroup(), request.getDaysAhead()});
        try {
            AiDemandResponse response = (AiDemandResponse)this.aiServiceRetryTemplate.execute(context -> {
                if (context.getRetryCount() > 0) {
                    log.warn("AI prediction retry #{}: url={}", (Object)context.getRetryCount(), (Object)url);
                }
                HttpEntity<AiDemandRequest> entity = new HttpEntity<>(request, this.buildJsonHeaders());
                ResponseEntity resp = this.aiServiceRestTemplate.exchange(url, HttpMethod.POST, entity, AiDemandResponse.class, new Object[0]);
                if (!resp.getStatusCode().is2xxSuccessful()) {
                    throw ApiException.badRequest("AI prediction failed with HTTP " + resp.getStatusCode().value());
                }
                return (AiDemandResponse)resp.getBody();
            });
            if (response == null) {
                log.error("AI service returned null prediction");
                throw ApiException.internalServerError("AI prediction service returned empty response");
            }
            log.info("AI prediction complete: city={}, predicted={}, confidence={}%, trend={}, action={}", new Object[]{response.getCity(), response.getPredictedRequests(), response.getConfidencePercent(), response.getTrend(), response.needsUrgentAction() ? "URGENT" : "normal"});
            return response;
        }
        catch (ResourceAccessException e) {
            log.warn("AI service unreachable for prediction: {}", (Object)e.getMessage());
            throw ApiException.internalServerError("AI prediction service temporarily unavailable.");
        }
        catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("AI prediction unexpected error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("Demand prediction failed. Please try again.");
        }
    }

    @Override
    public boolean isAiServiceHealthy() {
        String url = this.aiProperties.getUrl() + "/health";
        try {
            ResponseEntity resp = this.aiServiceRestTemplate.getForEntity(url, String.class, new Object[0]);
            boolean healthy = resp.getStatusCode() == HttpStatus.OK;
            log.debug("AI health check: url={}, status={}, healthy={}", new Object[]{url, resp.getStatusCode(), healthy});
            return healthy;
        }
        catch (Exception e) {
            log.warn("AI health check failed: url={}, error={}", (Object)url, (Object)e.getMessage());
            return false;
        }
    }

    private HttpHeaders buildJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (this.aiProperties.getApiKey() != null && !this.aiProperties.getApiKey().isBlank()) {
            headers.set("X-API-KEY", this.aiProperties.getApiKey());
        }
        return headers;
    }
}

