package com.blood_donor.notification.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.mail")
public class MailProperties {
    @NotBlank(message="Mail from-name is required")
    private @NotBlank(message="Mail from-name is required") String fromName = "Blood Donor Network";
    @NotBlank(message="Mail from-address is required")
    @Email(message="Mail from-address must be valid email")
    private @NotBlank(message="Mail from-address is required") @Email(message="Mail from-address must be valid email") String fromAddress = "noreply@blooddonor.com";
    private String templatePrefix = "email/";
    private int maxRetryAttempts = 3;
    private int retryDelayMs = 1000;

    public String getFromName() {
        return this.fromName;
    }

    public String getFromAddress() {
        return this.fromAddress;
    }

    public String getTemplatePrefix() {
        return this.templatePrefix;
    }

    public int getMaxRetryAttempts() {
        return this.maxRetryAttempts;
    }

    public int getRetryDelayMs() {
        return this.retryDelayMs;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public void setTemplatePrefix(String templatePrefix) {
        this.templatePrefix = templatePrefix;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public void setRetryDelayMs(int retryDelayMs) {
        this.retryDelayMs = retryDelayMs;
    }
}

