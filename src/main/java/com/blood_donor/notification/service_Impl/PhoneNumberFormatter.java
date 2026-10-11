/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.notification.service_Impl;

import com.blood_donor.notification.properties.TwilioProperties;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {
    private static final Logger log = LoggerFactory.getLogger(PhoneNumberFormatter.class);
    private final TwilioProperties twilioProperties;
    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");
    private static final Pattern INDIAN_10_DIGIT = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern INDIAN_11_DIGIT = Pattern.compile("^0[6-9]\\d{9}$");

    public String format(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        String cleaned = phone.trim().replaceAll("[\\s\\-\\(\\)]", "");
        if (cleaned.startsWith("+")) {
            if (E164_PATTERN.matcher(cleaned).matches()) {
                return cleaned;
            }
            throw new IllegalArgumentException("Invalid E.164 format: " + phone);
        }
        if (INDIAN_10_DIGIT.matcher(cleaned).matches()) {
            return this.twilioProperties.getDefaultCountryCode() + cleaned;
        }
        if (INDIAN_11_DIGIT.matcher(cleaned).matches()) {
            return this.twilioProperties.getDefaultCountryCode() + cleaned.substring(1);
        }
        String result = this.twilioProperties.getDefaultCountryCode() + cleaned;
        if (!E164_PATTERN.matcher(result).matches()) {
            throw new IllegalArgumentException("Cannot format phone number to E.164: " + phone);
        }
        return result;
    }

    public boolean isValidPhone(String phone) {
        try {
            this.format(phone);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }

    public PhoneNumberFormatter(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
    }
}

