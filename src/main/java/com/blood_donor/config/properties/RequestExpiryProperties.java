package com.blood_donor.config.properties;

import jakarta.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.request")
public class RequestExpiryProperties {
    private Map<String, Integer> expiryHours = new HashMap<String, Integer>(){
        {
            this.put("NORMAL", 48);
            this.put("URGENT", 24);
            this.put("CRITICAL", 6);
        }
    };
    @Min(value=1L, message="Default expiry must be >= 1 hour")
    private @Min(value=1L, message="Default expiry must be >= 1 hour") int defaultExpiryHours = 48;

    public int getExpiryHoursForUrgency(String urgency) {
        return this.expiryHours.getOrDefault(urgency != null ? urgency.toUpperCase() : "NORMAL", this.defaultExpiryHours);
    }

    public Map<String, Integer> getExpiryHours() {
        return this.expiryHours;
    }

    public int getDefaultExpiryHours() {
        return this.defaultExpiryHours;
    }

    public void setExpiryHours(Map<String, Integer> expiryHours) {
        this.expiryHours = expiryHours;
    }

    public void setDefaultExpiryHours(int defaultExpiryHours) {
        this.defaultExpiryHours = defaultExpiryHours;
    }
}

