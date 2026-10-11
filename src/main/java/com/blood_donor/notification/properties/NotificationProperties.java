/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Max
 *  jakarta.validation.constraints.Min
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 *  org.springframework.validation.annotation.Validated
 */
package com.blood_donor.notification.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.notification")
public class NotificationProperties {
    private boolean enabled = true;
    @Min(value=1L)
    @Max(value=50L)
    private @Min(value=1L) @Max(value=50L) int maxDonorsToNotify = 10;
    @Min(value=1L)
    @Max(value=200L)
    private @Min(value=1L) @Max(value=200L) double searchRadiusKm = 25.0;
    private boolean fallbackEnabled = true;
    private String emailSubjectPrefix = "[Blood Donor Network]";

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMaxDonorsToNotify() {
        return this.maxDonorsToNotify;
    }

    public double getSearchRadiusKm() {
        return this.searchRadiusKm;
    }

    public boolean isFallbackEnabled() {
        return this.fallbackEnabled;
    }

    public String getEmailSubjectPrefix() {
        return this.emailSubjectPrefix;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMaxDonorsToNotify(int maxDonorsToNotify) {
        this.maxDonorsToNotify = maxDonorsToNotify;
    }

    public void setSearchRadiusKm(double searchRadiusKm) {
        this.searchRadiusKm = searchRadiusKm;
    }

    public void setFallbackEnabled(boolean fallbackEnabled) {
        this.fallbackEnabled = fallbackEnabled;
    }

    public void setEmailSubjectPrefix(String emailSubjectPrefix) {
        this.emailSubjectPrefix = emailSubjectPrefix;
    }
}

