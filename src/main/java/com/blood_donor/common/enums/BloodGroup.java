/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonValue
 */
package com.blood_donor.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String displayName;
    private static final Map<BloodGroup, List<BloodGroup>> RECIPIENT_CACHE;

    private BloodGroup(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String toJsonValue() {
        return this.displayName;
    }

    @JsonCreator
    public static BloodGroup fromJson(String value) {
        return BloodGroup.fromDisplayNameOrThrow(value);
    }

    public static Optional<BloodGroup> fromDisplayName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(BloodGroup.values()).filter(bg -> bg.displayName.equalsIgnoreCase(displayName.trim())).findFirst();
    }

    public static BloodGroup fromDisplayNameOrThrow(String displayName) {
        return BloodGroup.fromDisplayName(displayName).orElseThrow(() -> new IllegalArgumentException("Invalid blood group: '" + displayName + "'. Valid values: " + Arrays.toString((Object[])BloodGroup.values())));
    }

    public static List<BloodGroup> getCompatibleDonors(BloodGroup recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient blood group cannot be null");
        }
        return switch (recipient) {
            default -> throw new IncompatibleClassChangeError();
            case A_POSITIVE -> List.of(A_POSITIVE, A_NEGATIVE, O_POSITIVE, O_NEGATIVE);
            case A_NEGATIVE -> List.of(A_NEGATIVE, O_NEGATIVE);
            case B_POSITIVE -> List.of(B_POSITIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE);
            case B_NEGATIVE -> List.of(B_NEGATIVE, O_NEGATIVE);
            case AB_POSITIVE -> List.of(A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, O_POSITIVE, O_NEGATIVE);
            case AB_NEGATIVE -> List.of(A_NEGATIVE, B_NEGATIVE, AB_NEGATIVE, O_NEGATIVE);
            case O_POSITIVE -> List.of(O_POSITIVE, O_NEGATIVE);
            case O_NEGATIVE -> List.of(O_NEGATIVE);
        };
    }

    public static List<BloodGroup> getCompatibleRecipients(BloodGroup donor) {
        if (donor == null) {
            throw new IllegalArgumentException("Donor blood group cannot be null");
        }
        return RECIPIENT_CACHE.get((Object)donor);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    static {
        RECIPIENT_CACHE = Arrays.stream(BloodGroup.values()).collect(Collectors.toMap(donor -> donor, donor -> Arrays.stream(BloodGroup.values()).filter(recipient -> BloodGroup.getCompatibleDonors(recipient).contains(donor)).toList()));
    }
}

