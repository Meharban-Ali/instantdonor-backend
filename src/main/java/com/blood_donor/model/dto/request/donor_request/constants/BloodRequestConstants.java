package com.blood_donor.model.dto.request.donor_request.constants;

public final class BloodRequestConstants {
    public static final int MIN_UNITS = 1;
    public static final int MAX_UNITS = 10;
    public static final int MAX_PATIENT_NAME = 100;
    public static final int MAX_HOSPITAL_NAME = 200;
    public static final int MAX_CITY_LENGTH = 100;
    public static final int MAX_ADDITIONAL_INFO = 500;
    public static final int EXPIRY_HOURS_NORMAL = 48;
    public static final int EXPIRY_HOURS_URGENT = 24;
    public static final int EXPIRY_HOURS_CRITICAL = 6;

    private BloodRequestConstants() {
    }
}

