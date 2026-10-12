package com.blood_donor.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Optional;

public enum Role {
    USER("User", "Regular registered user"),
    DONOR("Donor", "Blood donor"),
    ADMIN("Admin", "System administrator"),
    HOSPITAL("Hospital", "Hospital or blood bank");

    private final String displayName;
    private final String description;

    private Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @JsonValue
    public String toJsonValue() {
        return this.name();
    }

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        return Arrays.stream(Role.values()).filter(role -> role.name().equalsIgnoreCase(value.trim()) || role.displayName.equalsIgnoreCase(value.trim())).findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid role: '" + value + "'. Valid roles: " + Arrays.toString((Object[])Role.values())));
    }

    public static Optional<Role> fromStringSafe(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(Role.values()).filter(role -> role.name().equalsIgnoreCase(value.trim()) || role.displayName.equalsIgnoreCase(value.trim())).findFirst();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }
}

