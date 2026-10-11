/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.HttpRequest
 *  org.springframework.http.client.ClientHttpRequestExecution
 *  org.springframework.http.client.ClientHttpRequestInterceptor
 *  org.springframework.http.client.ClientHttpResponse
 */
package com.blood_donor.ai_service.config;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class AiServiceLoggingInterceptor
implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AiServiceLoggingInterceptor.class);
    private static final long SLOW_THRESHOLD_MS = 5000L;

    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Instant start = Instant.now();
        log.info("AI service request: method={}, uri={}", (Object)request.getMethod(), (Object)request.getURI());
        try {
            ClientHttpResponse response = execution.execute(request, body);
            long elapsedMs = Duration.between(start, Instant.now()).toMillis();
            log.info("AI service response: status={}, time={}ms, uri={}", new Object[]{response.getStatusCode().value(), elapsedMs, request.getURI()});
            if (elapsedMs > 5000L) {
                log.warn("Slow AI service response: {}ms for {} {}", new Object[]{elapsedMs, request.getMethod(), request.getURI()});
            }
            return response;
        }
        catch (IOException e) {
            long elapsedMs = Duration.between(start, Instant.now()).toMillis();
            log.error("AI service request failed: uri={}, time={}ms, error={}", new Object[]{request.getURI(), elapsedMs, e.getMessage()});
            throw e;
        }
    }
}

