package com.blood_donor.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class CustomMetricsService {

    private final Counter authFailureCounter;
    private final Counter rateLimit429Counter;
    private final Counter aiFallbackCounter;
    private final Counter smsSuccessCounter;
    private final Counter smsFailureCounter;

    public CustomMetricsService(MeterRegistry registry) {
        this.authFailureCounter = Counter.builder("auth.failures")
                .description("Total number of authentication failures")
                .register(registry);

        this.rateLimit429Counter = Counter.builder("ratelimit.throttled")
                .description("Total number of requests throttled by rate limiter (HTTP 429)")
                .register(registry);

        this.aiFallbackCounter = Counter.builder("ai.matching.fallback")
                .description("Total number of AI donor matching fallbacks triggered")
                .register(registry);

        this.smsSuccessCounter = Counter.builder("sms.delivery.success")
                .description("Total number of successfully delivered SMS notifications")
                .register(registry);

        this.smsFailureCounter = Counter.builder("sms.delivery.failure")
                .description("Total number of failed SMS delivery attempts")
                .register(registry);
    }

    public void recordAuthFailure() {
        this.authFailureCounter.increment();
    }

    public void recordRateLimit429() {
        this.rateLimit429Counter.increment();
    }

    public void recordAiFallback() {
        this.aiFallbackCounter.increment();
    }

    public void recordSmsSuccess() {
        this.smsSuccessCounter.increment();
    }

    public void recordSmsFailure() {
        this.smsFailureCounter.increment();
    }
}
