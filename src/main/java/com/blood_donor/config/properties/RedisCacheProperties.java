package com.blood_donor.config.properties;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="app.cache")
public class RedisCacheProperties {
    private long defaultTtlMinutes = 10L;
    private Map<String, Long> ttlMap = new HashMap<String, Long>();

    public long getDefaultTtlMinutes() {
        return this.defaultTtlMinutes;
    }

    public Map<String, Long> getTtlMap() {
        return this.ttlMap;
    }

    public void setDefaultTtlMinutes(long defaultTtlMinutes) {
        this.defaultTtlMinutes = defaultTtlMinutes;
    }

    public void setTtlMap(Map<String, Long> ttlMap) {
        this.ttlMap = ttlMap;
    }
}

