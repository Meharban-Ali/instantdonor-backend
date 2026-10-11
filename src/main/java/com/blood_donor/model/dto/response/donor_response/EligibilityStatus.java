/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonValue
 */
package com.blood_donor.model.dto.response.donor_response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EligibilityStatus {
    ELIGIBLE("eligible", "You can donate blood"),
    NOT_ELIGIBLE("not_eligible", "Cannot donate yet"),
    NEVER_DONATED("never_donated", "First time donor");

    private final String code;
    private final String defaultMessage;

    @JsonValue
    public String getCode() {
        return this.code;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    private EligibilityStatus(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
}

