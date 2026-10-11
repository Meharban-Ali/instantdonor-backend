/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotBlank
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 *  org.springframework.validation.annotation.Validated
 */
package com.blood_donor.ai_service.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.ai-service")
public class AiServiceProperties {
    @NotBlank(message="AI service URL is required")
    private @NotBlank(message="AI service URL is required") String url = "http://localhost:8000";
    @Min(value=500L, message="Connect timeout must be >= 500ms")
    private @Min(value=500L, message="Connect timeout must be >= 500ms") int connectTimeoutMs = 3000;
    @Min(value=1000L, message="Read timeout must be >= 1000ms")
    private @Min(value=1000L, message="Read timeout must be >= 1000ms") int readTimeoutMs = 10000;
    @Min(value=1L, message="Max retries must be >= 1")
    private @Min(value=1L, message="Max retries must be >= 1") int maxRetries = 3;
    @Min(value=100L, message="Retry delay must be >= 100ms")
    private @Min(value=100L, message="Retry delay must be >= 100ms") int retryDelayMs = 500;
    @Min(value=10L, message="Max connections must be >= 10")
    private @Min(value=10L, message="Max connections must be >= 10") int maxTotalConnections = 50;
    @Min(value=5L, message="Max per route must be >= 5")
    private @Min(value=5L, message="Max per route must be >= 5") int maxConnectionsPerRoute = 20;
    private int connectionKeepAliveMs = 30000;
    private int maxDonors = 10;
    @NotBlank(message="AI service API key is required")
    private @NotBlank(message="AI service API key is required") String apiKey = "local-dev-api-key-change-in-production";

    public String getUrl() {
        return this.url;
    }

    public int getConnectTimeoutMs() {
        return this.connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return this.readTimeoutMs;
    }

    public int getMaxRetries() {
        return this.maxRetries;
    }

    public int getRetryDelayMs() {
        return this.retryDelayMs;
    }

    public int getMaxTotalConnections() {
        return this.maxTotalConnections;
    }

    public int getMaxConnectionsPerRoute() {
        return this.maxConnectionsPerRoute;
    }

    public int getConnectionKeepAliveMs() {
        return this.connectionKeepAliveMs;
    }

    public int getMaxDonors() {
        return this.maxDonors;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setRetryDelayMs(int retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public void setMaxConnectionsPerRoute(int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public void setConnectionKeepAliveMs(int connectionKeepAliveMs) {
        this.connectionKeepAliveMs = connectionKeepAliveMs;
    }

    public void setMaxDonors(int maxDonors) {
        this.maxDonors = maxDonors;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

