/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonValue
 */
package com.blood_donor.model.dto.request.blood_request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Optional;

public enum BloodRequestStatus {
    PENDING("Pending", "Request created, seeking donors"),
    MATCHING("Matching", "AI matching in progress"),
    ACTIVE("Active", "Request is actively being processed"),
    FULFILLED("Fulfilled", "Blood found and arranged"),
    EXPIRED("Expired", "Request expired without fulfillment"),
    CANCELLED("Cancelled", "Request cancelled by user");

    private final String displayName;
    private final String description;

    private BloodRequestStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @JsonValue
    public String toJsonValue() {
        return this.name();
    }

    @JsonCreator
    public static BloodRequestStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BloodRequestStatus cannot be null or empty");
        }
        return Arrays.stream(BloodRequestStatus.values()).filter(status -> status.name().equalsIgnoreCase(value.trim()) || status.displayName.equalsIgnoreCase(value.trim())).findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid status: '" + value + "'. Valid values: " + Arrays.toString((Object[])BloodRequestStatus.values())));
    }

    public static Optional<BloodRequestStatus> fromStringSafe(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(BloodRequestStatus.values()).filter(status -> status.name().equalsIgnoreCase(value.trim()) || status.displayName.equalsIgnoreCase(value.trim())).findFirst();
    }

    public boolean isActive() {
        return this == PENDING || this == MATCHING || this == ACTIVE;
    }

    public boolean isTerminal() {
        return this == FULFILLED || this == EXPIRED || this == CANCELLED;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }
}

