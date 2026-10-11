/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonValue
 */
package com.blood_donor.model.dto.response.donor_response.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseStatus {
    NOTIFIED("notified", "Notification sent, awaiting response", false),
    ACCEPTED("accepted", "Donor accepted the request", true),
    REJECTED("rejected", "Donor rejected the request", true),
    NO_RESPONSE("no_response", "No response received within timeout", true),
    CANCELLED("cancelled", "Request was cancelled", true);

    private final String code;
    private final String description;
    private final boolean terminal;

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static ResponseStatus fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("ResponseStatus code cannot be null or empty");
        }
        for (ResponseStatus status : ResponseStatus.values()) {
            if (!status.code.equalsIgnoreCase(code.trim())) continue;
            return status;
        }
        throw new IllegalArgumentException("Invalid ResponseStatus: '" + code + "'");
    }

    public boolean isPending() {
        return this == NOTIFIED;
    }

    public boolean isPositive() {
        return this == ACCEPTED;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isTerminal() {
        return this.terminal;
    }

    private ResponseStatus(String code, String description, boolean terminal) {
        this.code = code;
        this.description = description;
        this.terminal = terminal;
    }
}

