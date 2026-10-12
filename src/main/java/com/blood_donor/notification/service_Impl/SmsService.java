package com.blood_donor.notification.service_Impl;

import com.blood_donor.notification.properties.TwilioProperties;
import com.blood_donor.notification.service.ISmsService;
import com.blood_donor.notification.service_Impl.PhoneNumberFormatter;
import com.blood_donor.notification.service_Impl.SmsMessageBuilder;
import com.blood_donor.notification.service_Impl.TwilioClientFactory;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SmsService
implements ISmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    private final TwilioProperties twilioProperties;
    private final TwilioClientFactory twilioClientFactory;
    private final PhoneNumberFormatter phoneFormatter;
    private final SmsMessageBuilder messageBuilder;
    private static final int MAX_RETRY_ATTEMPTS = 2;
    private static final long RETRY_DELAY_MS = 1000L;

    @PostConstruct
    public void init() {
        boolean ready = this.twilioClientFactory.initialize();
        if (ready) {
            log.info("SMS service initialized successfully");
        } else {
            log.warn("SMS service not available \u2014 check Twilio config");
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Boolean> sendSms(String toPhone, String message) {
        String preparedMessage;
        String formattedPhone;
        if (!this.twilioClientFactory.isReady()) {
            log.info("SMS skipped (service disabled): to={}, preview={}", (Object)toPhone, (Object)this.truncatePreview(message));
            return CompletableFuture.completedFuture(false);
        }
        try {
            formattedPhone = this.phoneFormatter.format(toPhone);
        }
        catch (IllegalArgumentException e) {
            log.warn("SMS skipped \u2014 invalid phone: {}, error={}", (Object)toPhone, (Object)e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        try {
            preparedMessage = this.messageBuilder.prepareMessage(message);
        }
        catch (IllegalArgumentException e) {
            log.warn("SMS skipped \u2014 empty message: {}", (Object)e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        return this.sendWithRetry(formattedPhone, preparedMessage, 0);
    }

    private CompletableFuture<Boolean> sendWithRetry(String formattedPhone, String preparedMessage, int attemptNumber) {
        try {
            Message smsMessage = (Message)Message.creator((PhoneNumber)new PhoneNumber(formattedPhone), (PhoneNumber)new PhoneNumber(this.twilioProperties.getPhoneNumber()), (String)preparedMessage).create();
            log.info("SMS sent: to={}, sid={}, attempt={}", new Object[]{formattedPhone, smsMessage.getSid(), attemptNumber + 1});
            return CompletableFuture.completedFuture(true);
        }
        catch (Exception e) {
            if (attemptNumber < 2) {
                long delayMs = 1000L * (long)Math.pow(2.0, attemptNumber);
                log.warn("SMS attempt {} failed: to={}, error={}. Retrying asynchronously in {}ms...", new Object[]{attemptNumber + 1, formattedPhone, e.getMessage(), delayMs});
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                CompletableFuture.delayedExecutor(delayMs, java.util.concurrent.TimeUnit.MILLISECONDS).execute(() -> {
                    this.sendWithRetry(formattedPhone, preparedMessage, attemptNumber + 1)
                            .whenComplete((result, ex) -> {
                                if (ex != null) {
                                    future.complete(false);
                                } else {
                                    future.complete(result);
                                }
                            });
                });
                return future;
            }
            log.error("SMS failed after {} attempts: to={}, error={}", new Object[]{3, formattedPhone, e.getMessage(), e});
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Void> sendBulkSms(List<String> phoneNumbers, String message) {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            log.warn("Bulk SMS skipped \u2014 empty recipients");
            return CompletableFuture.completedFuture(null);
        }
        List<String> validPhones = phoneNumbers.stream().filter(this.phoneFormatter::isValidPhone).toList();
        if (validPhones.isEmpty()) {
            log.warn("Bulk SMS skipped \u2014 no valid phone numbers: count={}", (Object)phoneNumbers.size());
            return CompletableFuture.completedFuture(null);
        }
        log.info("Bulk SMS: total={}, valid={}, preview={}", new Object[]{phoneNumbers.size(), validPhones.size(), this.truncatePreview(message)});
        List<CompletableFuture<Boolean>> futures = validPhones.stream().map(phone -> this.sendSms(phone, message)).toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Bulk SMS execution failed with error: {}", ex.getMessage(), ex);
            }
            long successCount = futures.stream().filter(f -> {
                try {
                    return Boolean.TRUE.equals(f.get());
                }
                catch (Exception e) {
                    log.warn("Bulk SMS delivery tracking error for recipient: {}", e.getMessage());
                    return false;
                }
            }).count();
            long failCount = validPhones.size() - successCount;
            if (failCount > 0) {
                log.warn("Bulk SMS completed with partial failures: sent={}/{}, failed={}, preview={}", new Object[]{successCount, validPhones.size(), failCount, this.truncatePreview(message)});
            } else {
                log.info("Bulk SMS complete: sent={}/{}, preview={}", new Object[]{successCount, validPhones.size(), this.truncatePreview(message)});
            }
        });
    }

    @Override
    public boolean isSmsServiceAvailable() {
        return this.twilioClientFactory.isReady();
    }

    private String truncatePreview(String message) {
        if (message == null) {
            return "null";
        }
        return message.length() > 50 ? message.substring(0, 47) + "..." : message;
    }

    public SmsService(TwilioProperties twilioProperties, TwilioClientFactory twilioClientFactory, PhoneNumberFormatter phoneFormatter, SmsMessageBuilder messageBuilder) {
        this.twilioProperties = twilioProperties;
        this.twilioClientFactory = twilioClientFactory;
        this.phoneFormatter = phoneFormatter;
        this.messageBuilder = messageBuilder;
    }
}

