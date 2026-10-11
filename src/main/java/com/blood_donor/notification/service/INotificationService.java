/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.notification.service;

import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;

public interface INotificationService {
    public void sendEmergencyAlert(BloodRequest var1);

    public void sendDonorAcceptedConfirmation(DonorProfile var1, BloodRequest var2);

    public void sendRequestFulfilledNotification(BloodRequest var1);

    public void sendPasswordResetEmail(String var1, String var2, String var3);
}

