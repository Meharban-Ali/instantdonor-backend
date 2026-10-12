package com.blood_donor.notification.service_Impl;

import com.blood_donor.notification.properties.MailProperties;
import com.blood_donor.notification.service.IEmailService;
import com.blood_donor.notification.service_Impl.EmailMessageBuilder;
import com.blood_donor.notification.service_Impl.EmailValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final String TEMPLATE_PREFIX = "email/";

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Boolean> sendHtmlEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            this.emailValidator.validateEmailRequest(to, subject, template);
        }
        catch (IllegalArgumentException e) {
            log.warn("HTML email skipped \u2014 invalid input: {}", (Object)e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        log.info("Sending HTML email: to={}, subject={}, template={}", new Object[]{to, subject, template});
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = this.templateEngine.process(TEMPLATE_PREFIX + template, (IContext)context);
            MimeMessage message = this.messageBuilder.buildHtmlMessage(to, subject, htmlContent);
            this.mailSender.send(message);
            log.info("HTML email sent: to={}, subject={}", (Object)to, (Object)subject);
            return CompletableFuture.completedFuture(true);
        }
        catch (UnsupportedEncodingException e) {
            log.error("Email encoding error: to={}, error={}", (Object)to, (Object)e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
        catch (MessagingException e) {
            log.error("HTML email messaging failed: to={}, error={}", new Object[]{to, e.getMessage(), e});
            return CompletableFuture.completedFuture(false);
        }
        catch (Exception e) {
            log.error("HTML email unexpected error: to={}, error={}", new Object[]{to, e.getMessage(), e});
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Boolean> sendSimpleEmail(String to, String subject, String text) {
        if (!this.emailValidator.isValidEmail(to)) {
            log.warn("Simple email skipped \u2014 invalid email: {}", (Object)to);
            return CompletableFuture.completedFuture(false);
        }
        if (!this.emailValidator.isValidSubject(subject)) {
            log.warn("Simple email skipped \u2014 invalid subject");
            return CompletableFuture.completedFuture(false);
        }
        if (text == null || text.isBlank()) {
            log.warn("Simple email skipped \u2014 empty text");
            return CompletableFuture.completedFuture(false);
        }
        log.info("Sending simple email: to={}, subject={}", (Object)to, (Object)subject);
        try {
            SimpleMailMessage message = this.messageBuilder.buildSimpleMessage(to, subject, text);
            this.mailSender.send(message);
            log.info("Simple email sent: to={}", (Object)to);
            return CompletableFuture.completedFuture(true);
        }
        catch (Exception e) {
            log.error("Simple email failed: to={}, error={}", new Object[]{to, e.getMessage(), e});
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public CompletableFuture<Void> sendBulkHtmlEmail(List<String> recipients, String subject, String template, Map<String, Object> variables) {
        if (recipients == null || recipients.isEmpty()) {
            log.warn("Bulk email skipped \u2014 empty recipients");
            return CompletableFuture.completedFuture(null);
        }
        List<String> validRecipients = recipients.stream().filter(this.emailValidator::isValidEmail).toList();
        if (validRecipients.isEmpty()) {
            log.warn("Bulk email skipped \u2014 no valid recipients");
            return CompletableFuture.completedFuture(null);
        }
        log.info("Bulk HTML email: total={}, valid={}, template={}", new Object[]{recipients.size(), validRecipients.size(), template});
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
            log.info("Bulk email complete: sent={}/{}, template={}", new Object[]{successCount, validRecipients.size(), template});
        });
    }

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, MailProperties mailProperties, EmailMessageBuilder messageBuilder, EmailValidator emailValidator) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
        this.messageBuilder = messageBuilder;
        this.emailValidator = emailValidator;
    }
}

