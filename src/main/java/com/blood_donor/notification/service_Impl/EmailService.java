package com.blood_donor.notification.service_Impl;

import com.blood_donor.monitoring.CustomMetricsService;
import com.blood_donor.notification.properties.MailProperties;
import com.blood_donor.notification.service.IEmailService;
import com.blood_donor.notification.service_Impl.EmailMessageBuilder;
import com.blood_donor.notification.service_Impl.EmailValidator;
import com.blood_donor.utils.PiiMasker;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

@Service
public class EmailService
implements IEmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;
    private final EmailMessageBuilder messageBuilder;
    private final EmailValidator emailValidator;
    private final CustomMetricsService customMetricsService;
    private static final String TEMPLATE_PREFIX = "email/";

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean smtpAuth;

    public boolean isMailConfigured() {
        if (this.mailHost == null || this.mailHost.isBlank()) {
            return false;
        }
        if (this.smtpAuth && (this.mailUsername == null || this.mailUsername.isBlank() || this.mailPassword == null || this.mailPassword.isBlank())) {
            return false;
        }
        return true;
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Boolean> sendHtmlEmail(String to, String subject, String template, Map<String, Object> variables) {
        String maskedTo = PiiMasker.maskEmail(to);
        try {
            this.emailValidator.validateEmailRequest(to, subject, template);
        }
        catch (IllegalArgumentException e) {
            log.warn("HTML email skipped — invalid input: {}", e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        if (!this.isMailConfigured()) {
            log.warn("Email sending gracefully skipped — mail server not configured (host='{}', auth={})", this.mailHost, this.smtpAuth);
            return CompletableFuture.completedFuture(false);
        }
        log.info("Sending HTML email: to={}, subject={}, template={}", maskedTo, subject, template);
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = this.templateEngine.process(TEMPLATE_PREFIX + template, (IContext)context);
            return this.sendHtmlWithRetry(to, subject, htmlContent, 0);
        }
        catch (Exception e) {
            log.error("HTML email template rendering error: to={}, error={}", maskedTo, e.getMessage(), e);
            if (this.customMetricsService != null) {
                this.customMetricsService.recordEmailFailure();
            }
            return CompletableFuture.completedFuture(false);
        }
    }

    private CompletableFuture<Boolean> sendHtmlWithRetry(String to, String subject, String htmlContent, int attemptNumber) {
        String maskedTo = PiiMasker.maskEmail(to);
        try {
            MimeMessage message = this.messageBuilder.buildHtmlMessage(to, subject, htmlContent);
            this.mailSender.send(message);
            log.info("HTML email sent: to={}, subject={}", maskedTo, subject);
            if (this.customMetricsService != null) {
                this.customMetricsService.recordEmailSuccess();
            }
            return CompletableFuture.completedFuture(true);
        }
        catch (Exception e) {
            int maxAttempts = Math.max(1, this.mailProperties.getMaxRetryAttempts());
            if (attemptNumber < maxAttempts - 1) {
                long delayMs = (long) this.mailProperties.getRetryDelayMs() * (1L << attemptNumber);
                log.warn("HTML email attempt {} failed: to={}, error={}. Retrying asynchronously in {}ms...", attemptNumber + 1, maskedTo, e.getMessage(), delayMs);
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS).execute(() -> {
                    this.sendHtmlWithRetry(to, subject, htmlContent, attemptNumber + 1)
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
            log.error("HTML email failed after {} attempts: to={}, error={}", maxAttempts, maskedTo, e.getMessage(), e);
            if (this.customMetricsService != null) {
                this.customMetricsService.recordEmailFailure();
            }
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Boolean> sendSimpleEmail(String to, String subject, String text) {
        String maskedTo = PiiMasker.maskEmail(to);
        if (!this.emailValidator.isValidEmail(to)) {
            log.warn("Simple email skipped — invalid email: {}", maskedTo);
            return CompletableFuture.completedFuture(false);
        }
        if (!this.emailValidator.isValidSubject(subject)) {
            log.warn("Simple email skipped — invalid subject");
            return CompletableFuture.completedFuture(false);
        }
        if (text == null || text.isBlank()) {
            log.warn("Simple email skipped — empty text");
            return CompletableFuture.completedFuture(false);
        }
        if (!this.isMailConfigured()) {
            log.warn("Email sending gracefully skipped — mail server not configured (host='{}', auth={})", this.mailHost, this.smtpAuth);
            return CompletableFuture.completedFuture(false);
        }
        log.info("Sending simple email: to={}, subject={}", maskedTo, subject);
        return this.sendSimpleWithRetry(to, subject, text, 0);
    }

    private CompletableFuture<Boolean> sendSimpleWithRetry(String to, String subject, String text, int attemptNumber) {
        String maskedTo = PiiMasker.maskEmail(to);
        try {
            SimpleMailMessage message = this.messageBuilder.buildSimpleMessage(to, subject, text);
            this.mailSender.send(message);
            log.info("Simple email sent: to={}", maskedTo);
            if (this.customMetricsService != null) {
                this.customMetricsService.recordEmailSuccess();
            }
            return CompletableFuture.completedFuture(true);
        }
        catch (Exception e) {
            int maxAttempts = Math.max(1, this.mailProperties.getMaxRetryAttempts());
            if (attemptNumber < maxAttempts - 1) {
                long delayMs = (long) this.mailProperties.getRetryDelayMs() * (1L << attemptNumber);
                log.warn("Simple email attempt {} failed: to={}, error={}. Retrying asynchronously in {}ms...", attemptNumber + 1, maskedTo, e.getMessage(), delayMs);
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS).execute(() -> {
                    this.sendSimpleWithRetry(to, subject, text, attemptNumber + 1)
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
            log.error("Simple email failed after {} attempts: to={}, error={}", maxAttempts, maskedTo, e.getMessage(), e);
            if (this.customMetricsService != null) {
                this.customMetricsService.recordEmailFailure();
            }
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Void> sendBulkHtmlEmail(List<String> recipients, String subject, String template, Map<String, Object> variables) {
        if (recipients == null || recipients.isEmpty()) {
            log.warn("Bulk email skipped — empty recipients");
            return CompletableFuture.completedFuture(null);
        }
        List<String> validRecipients = recipients.stream().filter(this.emailValidator::isValidEmail).toList();
        if (validRecipients.isEmpty()) {
            log.warn("Bulk email skipped — no valid recipients");
            return CompletableFuture.completedFuture(null);
        }
        log.info("Bulk HTML email: total={}, valid={}, template={}", recipients.size(), validRecipients.size(), template);
        List<CompletableFuture<Boolean>> futures = validRecipients.stream().map(email -> this.sendHtmlEmail(email, subject, template, variables)).toList();
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((result, ex) -> {
            long successCount = futures.stream().filter(f -> {
                try {
                    return (Boolean)f.get();
                }
                catch (Exception e) {
                    return false;
                }
            }).count();
            log.info("Bulk email complete: sent={}/{}, template={}", successCount, validRecipients.size(), template);
        });
    }

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, MailProperties mailProperties, EmailMessageBuilder messageBuilder, EmailValidator emailValidator, @Autowired(required = false) CustomMetricsService customMetricsService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
        this.messageBuilder = messageBuilder;
        this.emailValidator = emailValidator;
        this.customMetricsService = customMetricsService;
    }
}


