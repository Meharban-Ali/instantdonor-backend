package com.blood_donor.notification.service;

import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;

public interface INotificationService {
    public void sendEmergencyAlert(BloodRequest request);

    public void sendDonorAcceptedConfirmation(DonorProfile donor, BloodRequest request);

    public void sendRequestFulfilledNotification(BloodRequest request);

    public void sendPasswordResetEmail(String to, String name, String resetToken);
}

