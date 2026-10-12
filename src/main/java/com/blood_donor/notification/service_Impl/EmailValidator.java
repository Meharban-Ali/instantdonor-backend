package com.blood_donor.notification.service_Impl;

import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    private static final Logger log = LoggerFactory.getLogger(EmailValidator.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final int MAX_SUBJECT_LENGTH = 255;
    private static final int MAX_EMAIL_LENGTH = 254;

    public boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            log.debug("Email validation failed: null or empty");
            return false;
        }
        if (email.length() > 254) {
            log.debug("Email validation failed: too long ({} chars)", (Object)email.length());
            return false;
        }
        boolean valid = EMAIL_PATTERN.matcher(email.trim()).matches();
        if (!valid) {
            log.debug("Email validation failed: invalid format: {}", (Object)email);
        }
        return valid;
    }

    public boolean isValidSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            return false;
        }
        return subject.length() <= 255;
    }

    public void validateEmailRequest(String to, String subject, String template) {
        if (!this.isValidEmail(to)) {
            throw new IllegalArgumentException("Invalid recipient email address: " + to);
        }
        if (!this.isValidSubject(subject)) {
            throw new IllegalArgumentException("Invalid email subject: " + subject);
        }
        if (template == null || template.isBlank()) {
            throw new IllegalArgumentException("Email template cannot be empty");
        }
    }
}

