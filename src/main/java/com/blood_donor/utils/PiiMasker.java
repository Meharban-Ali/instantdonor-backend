package com.blood_donor.utils;

public class PiiMasker {

    private PiiMasker() {}

    public static String maskPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "N/A";
        }
        String trimmed = phone.trim();
        if (trimmed.length() <= 5) {
            return "***";
        }
        return trimmed.substring(0, 2) + "*".repeat(trimmed.length() - 5) + trimmed.substring(trimmed.length() - 3);
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "N/A";
        }
        String trimmed = email.trim();
        int atIndex = trimmed.indexOf('@');
        if (atIndex <= 1) {
            return "***" + (atIndex >= 0 ? trimmed.substring(atIndex) : "");
        }
        String name = trimmed.substring(0, atIndex);
        String domain = trimmed.substring(atIndex);
        if (name.length() <= 2) {
            return name.charAt(0) + "*" + domain;
        }
        return name.charAt(0) + "*".repeat(name.length() - 2) + name.charAt(name.length() - 1) + domain;
    }

    public static String maskToken(String token) {
        if (token == null || token.isBlank()) {
            return "N/A";
        }
        if (token.length() <= 10) {
            return "***";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
