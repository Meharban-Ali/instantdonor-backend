package com.blood_donor.ai_service.config;

import com.blood_donor.ai_service.config.AiServiceErrorHandler;
import com.blood_donor.ai_service.config.AiServiceLoggingInterceptor;
import com.blood_donor.ai_service.properties.AiServiceProperties;
import java.util.List;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
public class RestTemplateConfig {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);
    private final AiServiceProperties aiProperties;

    @Bean(name={"aiServiceRestTemplate"})
    public RestTemplate aiServiceRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = this.buildRequestFactory();
        BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory((ClientHttpRequestFactory)factory);
        RestTemplate restTemplate = new RestTemplate((ClientHttpRequestFactory)bufferingFactory);
        restTemplate.setInterceptors(List.of(new AiServiceLoggingInterceptor()));
        restTemplate.setErrorHandler((ResponseErrorHandler)new AiServiceErrorHandler());
        log.info("AI RestTemplate configured: url={}, connectTimeout={}ms, readTimeout={}ms, maxConnections={}", new Object[]{this.aiProperties.getUrl(), this.aiProperties.getConnectTimeoutMs(), this.aiProperties.getReadTimeoutMs(), this.aiProperties.getMaxTotalConnections()});
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory buildRequestFactory() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(this.aiProperties.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(this.aiProperties.getMaxConnectionsPerRoute());
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofMilliseconds((long)this.aiProperties.getConnectTimeoutMs())).setResponseTimeout(Timeout.ofMilliseconds((long)this.aiProperties.getReadTimeoutMs())).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager((HttpClientConnectionManager)connectionManager).setDefaultRequestConfig(requestConfig).evictExpiredConnections().build();
        return new HttpComponentsClientHttpRequestFactory((HttpClient)httpClient);
    }

    @Bean(name={"aiServiceRetryTemplate"})
    public RetryTemplate aiServiceRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(this.aiProperties.getMaxRetries());
        retryTemplate.setRetryPolicy((RetryPolicy)retryPolicy);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod((long)this.aiProperties.getRetryDelayMs());
        retryTemplate.setBackOffPolicy((BackOffPolicy)backOffPolicy);
        log.info("AI RetryTemplate configured: maxRetries={}, delayMs={}", (Object)this.aiProperties.getMaxRetries(), (Object)this.aiProperties.getRetryDelayMs());
        return retryTemplate;
    }

    public RestTemplateConfig(AiServiceProperties aiProperties) {
        this.aiProperties = aiProperties;
    }
}

