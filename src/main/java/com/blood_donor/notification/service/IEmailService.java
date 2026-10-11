/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.notification.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IEmailService {
    public CompletableFuture<Boolean> sendHtmlEmail(String var1, String var2, String var3, Map<String, Object> var4);

    public CompletableFuture<Boolean> sendSimpleEmail(String var1, String var2, String var3);

    public CompletableFuture<Void> sendBulkHtmlEmail(List<String> var1, String var2, String var3, Map<String, Object> var4);
}

