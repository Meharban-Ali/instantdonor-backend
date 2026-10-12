package com.blood_donor.notification.service_Impl;

import com.blood_donor.notification.properties.TwilioProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsMessageBuilder {
    private static final Logger log = LoggerFactory.getLogger(SmsMessageBuilder.class);
    private final TwilioProperties twilioProperties;
    private static final String TRUNCATE_SUFFIX = "...";

    public String prepareMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            throw new IllegalArgumentException("SMS message cannot be empty");
        }
        int maxLength = this.twilioProperties.getMaxSmsLength();
        if (rawMessage.length() <= maxLength) {
            return rawMessage;
        }
        String truncated = rawMessage.substring(0, maxLength - TRUNCATE_SUFFIX.length()) + TRUNCATE_SUFFIX;
        log.debug("SMS truncated: original={}chars, truncated={}chars", (Object)rawMessage.length(), (Object)truncated.length());
        return truncated;
    }

    public String buildEmergencyMessage(String bloodGroup, String hospital, String city, String contactPhone) {
        String message = String.format("URGENT: %s blood needed at %s, %s. Please respond immediately. Call: %s", bloodGroup, hospital, city, contactPhone);
        return this.prepareMessage(message);
    }

    public SmsMessageBuilder(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }
}

