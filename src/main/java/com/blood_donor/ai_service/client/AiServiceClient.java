/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.retry.support.RetryTemplate
 *  org.springframework.stereotype.Component
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.ResourceAccessException
 *  org.springframework.web.client.RestTemplate
 */
package com.blood_donor.ai_service.client;

import com.blood_donor.ai_service.dto.request.AiDemandRequest;
import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDemandResponse;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.properties.AiServiceProperties;
import com.blood_donor.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class AiServiceClient {
    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);
    @Qualifier(value="aiServiceRestTemplate")
    private final RestTemplate restTemplate;
    @Qualifier(value="aiServiceRetryTemplate")
    private final RetryTemplate retryTemplate;
    private final AiServiceProperties aiProperties;

    public AiMatchResponse matchDonors(AiMatchRequest request) {
        String url = this.aiProperties.getUrl() + "/api/v1/match-donors";
        log.info("Calling AI match: url={}, bloodGroup={}, urgency={}", new Object[]{url, request.getBloodGroup(), request.getUrgency()});
        try {
            return (AiMatchResponse)this.retryTemplate.execute(context -> {
                if (context.getRetryCount() > 0) {
                    log.warn("AI match retry #{}: url={}", (Object)context.getRetryCount(), (Object)url);
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("X-API-KEY", this.aiProperties.getApiKey());
                HttpEntity entity = new HttpEntity((Object)request, (MultiValueMap)headers);
                return (AiMatchResponse)this.restTemplate.postForObject(url, (Object)entity, AiMatchResponse.class, new Object[0]);
            });
        }
        catch (ResourceAccessException e) {
            log.error("AI service unreachable: {}", (Object)e.getMessage());
            throw ApiException.internalServerError("AI matching service unavailable. Please try again.");
        }
        catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("AI match unexpected error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("AI matching service error");
        }
    }

    public AiDemandResponse predictDemand(AiDemandRequest request) {
        String url = this.aiProperties.getUrl() + "/api/v1/predict-demand";
        log.info("Calling AI predict: url={}, city={}, bloodGroup={}", new Object[]{url, request.getCity(), request.getBloodGroup()});
        try {
            return (AiDemandResponse)this.retryTemplate.execute(context -> {
                if (context.getRetryCount() > 0) {
                    log.warn("AI predict retry #{}: url={}", (Object)context.getRetryCount(), (Object)url);
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("X-API-KEY", this.aiProperties.getApiKey());
                HttpEntity entity = new HttpEntity((Object)request, (MultiValueMap)headers);
                return (AiDemandResponse)this.restTemplate.postForObject(url, (Object)entity, AiDemandResponse.class, new Object[0]);
            });
        }
        catch (ResourceAccessException e) {
            log.error("AI service unreachable: {}", (Object)e.getMessage());
            throw ApiException.internalServerError("AI prediction service unavailable.");
        }
        catch (ApiException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("AI predict unexpected error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("AI prediction service error");
        }
    }

    public boolean isAiServiceHealthy() {
        try {
            String url = this.aiProperties.getUrl() + "/health";
            String response = (String)this.restTemplate.getForObject(url, String.class, new Object[0]);
            log.debug("AI health check OK: {}", (Object)response);
            return true;
        }
        catch (Exception e) {
            log.warn("AI service health check failed: {}", (Object)e.getMessage());
            return false;
        }
    }

    public AiServiceClient(RestTemplate restTemplate, RetryTemplate retryTemplate, AiServiceProperties aiProperties) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.aiProperties = aiProperties;
    }
}

