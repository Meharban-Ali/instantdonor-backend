package com.blood_donor.model.dto.request.donor_request;

public class DonorPreferencesRequest {
    private Boolean smsAlertsEnabled;
    private Boolean emailAlertsEnabled;

    public Boolean getSmsAlertsEnabled() {
        return this.smsAlertsEnabled;
    }

    public void setSmsAlertsEnabled(Boolean smsAlertsEnabled) {
        this.smsAlertsEnabled = smsAlertsEnabled;
    }

    public Boolean getEmailAlertsEnabled() {
        return this.emailAlertsEnabled;
    }

    public void setEmailAlertsEnabled(Boolean emailAlertsEnabled) {
        this.emailAlertsEnabled = emailAlertsEnabled;
    }

    public DonorPreferencesRequest() {
    }

    public DonorPreferencesRequest(Boolean smsAlertsEnabled, Boolean emailAlertsEnabled) {
        this.smsAlertsEnabled = smsAlertsEnabled;
        this.emailAlertsEnabled = emailAlertsEnabled;
    }
}
