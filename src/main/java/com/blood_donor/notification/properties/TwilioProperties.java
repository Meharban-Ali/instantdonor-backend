package com.blood_donor.notification.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="app.twilio")
public class TwilioProperties {
    private String accountSid = "";
    private String authToken = "";
    private String phoneNumber = "";
    private boolean enabled = false;
    private int maxSmsLength = 160;
    private String defaultCountryCode = "+91";

    public boolean isConfigured() {
        return this.enabled && this.accountSid != null && !this.accountSid.isBlank() && this.authToken != null && !this.authToken.isBlank() && this.phoneNumber != null && !this.phoneNumber.isBlank();
    }

    public String getAccountSid() {
        return this.accountSid;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getMaxSmsLength() {
        return this.maxSmsLength;
    }

    public String getDefaultCountryCode() {
        return this.defaultCountryCode;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setMaxSmsLength(int maxSmsLength) {
        this.maxSmsLength = maxSmsLength;
    }

    public void setDefaultCountryCode(String defaultCountryCode) {
        this.defaultCountryCode = defaultCountryCode;
    }
}

