package com.blood_donor.common.urgencyRequest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum UrgencyLevel {
    NORMAL("Normal", "Standard blood request"),
    URGENT("Urgent", "Needed within 24 hours"),
    CRITICAL("Critical", "Immediately required");

    private final String displayName;
    private final String description;

    private UrgencyLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @JsonValue
    public String toJsonValue() {
        return this.name();
    }

    @JsonCreator
    public static UrgencyLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UrgencyLevel cannot be null or empty");
        }
        return Arrays.stream(UrgencyLevel.values()).filter(u -> u.name().equalsIgnoreCase(value.trim()) || u.displayName.equalsIgnoreCase(value.trim())).findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid urgency: '" + value + "'. Valid values: " + Arrays.toString((Object[])UrgencyLevel.values())));
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }
}

