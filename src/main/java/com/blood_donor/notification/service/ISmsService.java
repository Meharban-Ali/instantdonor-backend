/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.notification.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ISmsService {
    public CompletableFuture<Boolean> sendSms(String var1, String var2);

    public CompletableFuture<Void> sendBulkSms(List<String> var1, String var2);

    public boolean isSmsServiceAvailable();
}

