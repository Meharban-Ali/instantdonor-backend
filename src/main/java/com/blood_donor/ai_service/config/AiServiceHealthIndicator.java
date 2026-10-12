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
            return Health.status("CIRCUIT_OPEN")
                    .withDetail("message", (Object)"AI service unavailable, database fallback active")
                    .build();
        }
        catch (Exception e) {
            return Health.status("CIRCUIT_OPEN")
                    .withDetail("message", (Object)("AI service unreachable: " + e.getMessage() + ", database fallback active"))
                    .build();
        }
    }

    public AiServiceHealthIndicator(IAiMatchingService aiMatchingService) {
        this.aiMatchingService = aiMatchingService;
    }
}

