/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Min
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 *  org.springframework.validation.annotation.Validated
 */
package com.blood_donor.scheduler.properties;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.scheduler")
public class SchedulerProperties {
    @Min(value=1L, message="No-response timeout must be >= 1 minute")
    private @Min(value=1L, message="No-response timeout must be >= 1 minute") int noResponseTimeoutMinutes = 60;
    @Min(value=1L, message="Max notify donors must be >= 1")
    private @Min(value=1L, message="Max notify donors must be >= 1") int maxNotifyDonors = 10;
    @Min(value=1L, message="Check interval must be >= 1 minute")
    private @Min(value=1L, message="Check interval must be >= 1 minute") int expiryCheckIntervalMinutes = 5;

    public int getNoResponseTimeoutMinutes() {
        return this.noResponseTimeoutMinutes;
    }

    public int getMaxNotifyDonors() {
        return this.maxNotifyDonors;
    }

    public int getExpiryCheckIntervalMinutes() {
        return this.expiryCheckIntervalMinutes;
    }

    public void setNoResponseTimeoutMinutes(int noResponseTimeoutMinutes) {
        this.noResponseTimeoutMinutes = noResponseTimeoutMinutes;
    }

    public void setMaxNotifyDonors(int maxNotifyDonors) {
        this.maxNotifyDonors = maxNotifyDonors;
    }

    public void setExpiryCheckIntervalMinutes(int expiryCheckIntervalMinutes) {
        this.expiryCheckIntervalMinutes = expiryCheckIntervalMinutes;
    }
}

