package com.blood_donor.notification.service_Impl;

import com.blood_donor.notification.properties.TwilioProperties;
import com.twilio.Twilio;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TwilioClientFactory {
    private static final Logger log = LoggerFactory.getLogger(TwilioClientFactory.class);
    private final TwilioProperties properties;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public boolean initialize() {
        if (this.initialized.get()) {
            return true;
        }
        if (!this.properties.isConfigured()) {
            log.warn("Twilio not configured — SMS disabled. Set app.twilio.account-sid, auth-token, and phone-number or messaging-service-sid in application.properties");
            return false;
        }
        if (this.initialized.compareAndSet(false, true)) {
            try {
                Twilio.init(this.properties.getAccountSid(), this.properties.getAuthToken());
                String senderInfo = (this.properties.getMessagingServiceSid() != null && !this.properties.getMessagingServiceSid().isBlank())
                        ? "messagingServiceSid=" + this.properties.getMessagingServiceSid()
                        : "phoneNumber=" + com.blood_donor.utils.PiiMasker.maskPhone(this.properties.getPhoneNumber());
                log.info("Twilio initialized: {}", senderInfo);
                return true;
            }
            catch (Exception e) {
                this.initialized.set(false);
                log.error("Twilio initialization failed: {}", (Object)e.getMessage(), (Object)e);
                return false;
            }
        }
        return this.initialized.get();
    }

    public boolean isReady() {
        return this.initialized.get() && this.properties.isConfigured();
    }

    public TwilioClientFactory(TwilioProperties properties) {
        this.properties = properties;
    }
}

