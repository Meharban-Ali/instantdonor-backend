package com.blood_donor.notification.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ISmsService {
    public CompletableFuture<Boolean> sendSms(String to, String message);

    public CompletableFuture<Void> sendBulkSms(List<String> recipients, String message);

    public boolean isSmsServiceAvailable();
}

