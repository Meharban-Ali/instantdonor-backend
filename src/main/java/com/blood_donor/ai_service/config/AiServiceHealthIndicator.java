/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.actuate.health.Health
 *  org.springframework.boot.actuate.health.HealthIndicator
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.ai_service.config;

import com.blood_donor.ai_service.service.IAiMatchingService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component(value="ai-service")
public class AiServiceHealthIndicator
implements HealthIndicator {
    private final IAiMatchingService aiMatchingService;

    public Health health() {
        try {
            boolean healthy = this.aiMatchingService.isAiServiceHealthy();
            if (healthy) {
                return Health.up().withDetail("message", (Object)"AI service is responsive").build();
            }
            return Health.down().withDetail("message", (Object)"AI service returned unhealthy status").build();
        }
        catch (Exception e) {
            return Health.down((Exception)e).build();
        }
    }

    public AiServiceHealthIndicator(IAiMatchingService aiMatchingService) {
        this.aiMatchingService = aiMatchingService;
    }
}

