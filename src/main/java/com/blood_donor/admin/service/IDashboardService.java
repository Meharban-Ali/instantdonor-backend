/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.admin.service;

import com.blood_donor.admin.dto.DashboardStatsDto;

public interface IDashboardService {
    public DashboardStatsDto getDashboardStats();

    public DashboardStatsDto refreshDashboardStats();
}

