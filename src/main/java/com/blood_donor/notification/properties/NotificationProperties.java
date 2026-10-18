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
    @Max(value=50L)
    private @Min(value=1L) @Max(value=50L) int maxSmsRecipients = 20;
    @Min(value=1L)
    @Max(value=200L)
    private @Min(value=1L) @Max(value=200L) double searchRadiusKm = 25.0;
    private boolean fallbackEnabled = true;
    private String emailSubjectPrefix = "[Blood Donor Network]";
    @Min(value=1L)
    private int maxSmsPerDonorPerDay = 3;
    @Min(value=1L)
    private int globalDailySmsCap = 500;

    public int getMaxSmsRecipients() {
        return this.maxSmsRecipients;
    }

    public void setMaxSmsRecipients(int maxSmsRecipients) {
        this.maxSmsRecipients = maxSmsRecipients;
    }

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

    public int getMaxSmsPerDonorPerDay() {
        return this.maxSmsPerDonorPerDay;
    }

    public int getGlobalDailySmsCap() {
        return this.globalDailySmsCap;
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

    public void setMaxSmsPerDonorPerDay(int maxSmsPerDonorPerDay) {
        this.maxSmsPerDonorPerDay = maxSmsPerDonorPerDay;
    }

    public void setGlobalDailySmsCap(int globalDailySmsCap) {
        this.globalDailySmsCap = globalDailySmsCap;
    }
}

