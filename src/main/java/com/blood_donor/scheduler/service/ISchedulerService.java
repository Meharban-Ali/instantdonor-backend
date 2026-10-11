/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.scheduler.service;

import java.time.LocalDateTime;

public interface ISchedulerService {
    public int expireOldRequests();

    public int markNoResponseDonors();

    public int cleanOldRequests(LocalDateTime var1);
}

