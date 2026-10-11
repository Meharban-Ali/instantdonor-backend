/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 */
package com.blood_donor.model.dto.response.donor_response;

import com.blood_donor.model.dto.response.donor_response.EligibilityStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class EligibilityResponse {
    private boolean eligible;
    private EligibilityStatus status;
    private LocalDate lastDonationDate;
    private LocalDate nextEligibleDate;
    private long daysRemaining;
    private String message;

    public static EligibilityResponse neverDonated() {
        return EligibilityResponse.builder().eligible(true).status(EligibilityStatus.NEVER_DONATED).daysRemaining(0L).message("Welcome! You are eligible to donate blood for the first time!").build();
    }

    public static EligibilityResponse eligible(LocalDate lastDonationDate) {
        return EligibilityResponse.builder().eligible(true).status(EligibilityStatus.ELIGIBLE).lastDonationDate(lastDonationDate).daysRemaining(0L).message("You are eligible to donate blood!").build();
    }

    public static EligibilityResponse notEligible(LocalDate lastDonationDate, LocalDate nextEligibleDate) {
        long days = Math.max(0L, (long)LocalDate.now().until(nextEligibleDate).getDays());
        return EligibilityResponse.builder().eligible(false).status(EligibilityStatus.NOT_ELIGIBLE).lastDonationDate(lastDonationDate).nextEligibleDate(nextEligibleDate).daysRemaining(days).message("You can donate after " + days + " more days. Next eligible date: " + nextEligibleDate).build();
    }

    private static long $default$daysRemaining() {
        return 0L;
    }

    public static EligibilityResponseBuilder builder() {
        return new EligibilityResponseBuilder();
    }

    public boolean isEligible() {
        return this.eligible;
    }

    public EligibilityStatus getStatus() {
        return this.status;
    }

    public LocalDate getLastDonationDate() {
        return this.lastDonationDate;
    }

    public LocalDate getNextEligibleDate() {
        return this.nextEligibleDate;
    }

    public long getDaysRemaining() {
        return this.daysRemaining;
    }

    public String getMessage() {
        return this.message;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public void setStatus(EligibilityStatus status) {
        this.status = status;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public void setNextEligibleDate(LocalDate nextEligibleDate) {
        this.nextEligibleDate = nextEligibleDate;
    }

    public void setDaysRemaining(long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EligibilityResponse)) {
            return false;
        }
        EligibilityResponse other = (EligibilityResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isEligible() != other.isEligible()) {
            return false;
        }
        if (this.getDaysRemaining() != other.getDaysRemaining()) {
            return false;
        }
        EligibilityStatus this$status = this.getStatus();
        EligibilityStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
            return false;
        }
        LocalDate this$lastDonationDate = this.getLastDonationDate();
        LocalDate other$lastDonationDate = other.getLastDonationDate();
        if (this$lastDonationDate == null ? other$lastDonationDate != null : !((Object)this$lastDonationDate).equals(other$lastDonationDate)) {
            return false;
        }
        LocalDate this$nextEligibleDate = this.getNextEligibleDate();
        LocalDate other$nextEligibleDate = other.getNextEligibleDate();
        if (this$nextEligibleDate == null ? other$nextEligibleDate != null : !((Object)this$nextEligibleDate).equals(other$nextEligibleDate)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EligibilityResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isEligible() ? 79 : 97);
        long $daysRemaining = this.getDaysRemaining();
        result = result * 59 + (int)($daysRemaining >>> 32 ^ $daysRemaining);
        EligibilityStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
        LocalDate $lastDonationDate = this.getLastDonationDate();
        result = result * 59 + ($lastDonationDate == null ? 43 : ((Object)$lastDonationDate).hashCode());
        LocalDate $nextEligibleDate = this.getNextEligibleDate();
        result = result * 59 + ($nextEligibleDate == null ? 43 : ((Object)$nextEligibleDate).hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "EligibilityResponse(eligible=" + this.isEligible() + ", status=" + this.getStatus() + ", lastDonationDate=" + this.getLastDonationDate() + ", nextEligibleDate=" + this.getNextEligibleDate() + ", daysRemaining=" + this.getDaysRemaining() + ", message=" + this.getMessage() + ")";
    }

    public EligibilityResponse() {
        this.daysRemaining = EligibilityResponse.$default$daysRemaining();
    }

    public EligibilityResponse(boolean eligible, EligibilityStatus status, LocalDate lastDonationDate, LocalDate nextEligibleDate, long daysRemaining, String message) {
        this.eligible = eligible;
        this.status = status;
        this.lastDonationDate = lastDonationDate;
        this.nextEligibleDate = nextEligibleDate;
        this.daysRemaining = daysRemaining;
        this.message = message;
    }

    public static class EligibilityResponseBuilder {
        private boolean eligible;
        private EligibilityStatus status;
        private LocalDate lastDonationDate;
        private LocalDate nextEligibleDate;
        private boolean daysRemaining$set;
        private long daysRemaining$value;
        private String message;

        EligibilityResponseBuilder() {
        }

        public EligibilityResponseBuilder eligible(boolean eligible) {
            this.eligible = eligible;
            return this;
        }

        public EligibilityResponseBuilder status(EligibilityStatus status) {
            this.status = status;
            return this;
        }

        public EligibilityResponseBuilder lastDonationDate(LocalDate lastDonationDate) {
            this.lastDonationDate = lastDonationDate;
            return this;
        }

        public EligibilityResponseBuilder nextEligibleDate(LocalDate nextEligibleDate) {
            this.nextEligibleDate = nextEligibleDate;
            return this;
        }

        public EligibilityResponseBuilder daysRemaining(long daysRemaining) {
            this.daysRemaining$value = daysRemaining;
            this.daysRemaining$set = true;
            return this;
        }

        public EligibilityResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public EligibilityResponse build() {
            long daysRemaining$value = this.daysRemaining$value;
            if (!this.daysRemaining$set) {
                daysRemaining$value = EligibilityResponse.$default$daysRemaining();
            }
            return new EligibilityResponse(this.eligible, this.status, this.lastDonationDate, this.nextEligibleDate, daysRemaining$value, this.message);
        }

        public String toString() {
            return "EligibilityResponse.EligibilityResponseBuilder(eligible=" + this.eligible + ", status=" + this.status + ", lastDonationDate=" + this.lastDonationDate + ", nextEligibleDate=" + this.nextEligibleDate + ", daysRemaining$value=" + this.daysRemaining$value + ", message=" + this.message + ")";
        }
    }
}

