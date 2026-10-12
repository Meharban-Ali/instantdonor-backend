package com.blood_donor.notification.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IEmailService {
    public CompletableFuture<Boolean> sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel);

    public CompletableFuture<Boolean> sendSimpleEmail(String to, String subject, String text);

    public CompletableFuture<Void> sendBulkHtmlEmail(List<String> recipients, String subject, String templateName, Map<String, Object> templateModel);
}

