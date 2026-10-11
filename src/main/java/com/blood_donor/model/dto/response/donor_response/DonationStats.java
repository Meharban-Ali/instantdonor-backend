/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.model.dto.response.donor_response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DonationStats {
    private Integer totalDonations;
    private BigDecimal reliabilityScore;
    private BigDecimal responseRate;
    private LocalDate lastDonationDate;
    private LocalDate nextEligibleDate;
    private boolean isEligible;

    public static DonationStatsBuilder builder() {
        return new DonationStatsBuilder();
    }

    public Integer getTotalDonations() {
        return this.totalDonations;
    }

    public BigDecimal getReliabilityScore() {
        return this.reliabilityScore;
    }

    public BigDecimal getResponseRate() {
        return this.responseRate;
    }

    public LocalDate getLastDonationDate() {
        return this.lastDonationDate;
    }

    public LocalDate getNextEligibleDate() {
        return this.nextEligibleDate;
    }

    public boolean isEligible() {
        return this.isEligible;
    }

    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public void setResponseRate(BigDecimal responseRate) {
        this.responseRate = responseRate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public void setNextEligibleDate(LocalDate nextEligibleDate) {
        this.nextEligibleDate = nextEligibleDate;
    }

    public void setEligible(boolean isEligible) {
        this.isEligible = isEligible;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DonationStats)) {
            return false;
        }
        DonationStats other = (DonationStats)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isEligible() != other.isEligible()) {
            return false;
        }
        Integer this$totalDonations = this.getTotalDonations();
        Integer other$totalDonations = other.getTotalDonations();
        if (this$totalDonations == null ? other$totalDonations != null : !((Object)this$totalDonations).equals(other$totalDonations)) {
            return false;
        }
        BigDecimal this$reliabilityScore = this.getReliabilityScore();
        BigDecimal other$reliabilityScore = other.getReliabilityScore();
        if (this$reliabilityScore == null ? other$reliabilityScore != null : !((Object)this$reliabilityScore).equals(other$reliabilityScore)) {
            return false;
        }
        BigDecimal this$responseRate = this.getResponseRate();
        BigDecimal other$responseRate = other.getResponseRate();
        if (this$responseRate == null ? other$responseRate != null : !((Object)this$responseRate).equals(other$responseRate)) {
            return false;
        }
        LocalDate this$lastDonationDate = this.getLastDonationDate();
        LocalDate other$lastDonationDate = other.getLastDonationDate();
        if (this$lastDonationDate == null ? other$lastDonationDate != null : !((Object)this$lastDonationDate).equals(other$lastDonationDate)) {
            return false;
        }
        LocalDate this$nextEligibleDate = this.getNextEligibleDate();
        LocalDate other$nextEligibleDate = other.getNextEligibleDate();
        return !(this$nextEligibleDate == null ? other$nextEligibleDate != null : !((Object)this$nextEligibleDate).equals(other$nextEligibleDate));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DonationStats;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isEligible() ? 79 : 97);
        Integer $totalDonations = this.getTotalDonations();
        result = result * 59 + ($totalDonations == null ? 43 : ((Object)$totalDonations).hashCode());
        BigDecimal $reliabilityScore = this.getReliabilityScore();
        result = result * 59 + ($reliabilityScore == null ? 43 : ((Object)$reliabilityScore).hashCode());
        BigDecimal $responseRate = this.getResponseRate();
        result = result * 59 + ($responseRate == null ? 43 : ((Object)$responseRate).hashCode());
        LocalDate $lastDonationDate = this.getLastDonationDate();
        result = result * 59 + ($lastDonationDate == null ? 43 : ((Object)$lastDonationDate).hashCode());
        LocalDate $nextEligibleDate = this.getNextEligibleDate();
        result = result * 59 + ($nextEligibleDate == null ? 43 : ((Object)$nextEligibleDate).hashCode());
        return result;
    }

    public String toString() {
        return "DonationStats(totalDonations=" + this.getTotalDonations() + ", reliabilityScore=" + this.getReliabilityScore() + ", responseRate=" + this.getResponseRate() + ", lastDonationDate=" + this.getLastDonationDate() + ", nextEligibleDate=" + this.getNextEligibleDate() + ", isEligible=" + this.isEligible() + ")";
    }

    public DonationStats() {
    }

    public DonationStats(Integer totalDonations, BigDecimal reliabilityScore, BigDecimal responseRate, LocalDate lastDonationDate, LocalDate nextEligibleDate, boolean isEligible) {
        this.totalDonations = totalDonations;
        this.reliabilityScore = reliabilityScore;
        this.responseRate = responseRate;
        this.lastDonationDate = lastDonationDate;
        this.nextEligibleDate = nextEligibleDate;
        this.isEligible = isEligible;
    }

    public static class DonationStatsBuilder {
        private Integer totalDonations;
        private BigDecimal reliabilityScore;
        private BigDecimal responseRate;
        private LocalDate lastDonationDate;
        private LocalDate nextEligibleDate;
        private boolean isEligible;

        DonationStatsBuilder() {
        }

        public DonationStatsBuilder totalDonations(Integer totalDonations) {
            this.totalDonations = totalDonations;
            return this;
        }

        public DonationStatsBuilder reliabilityScore(BigDecimal reliabilityScore) {
            this.reliabilityScore = reliabilityScore;
            return this;
        }

        public DonationStatsBuilder responseRate(BigDecimal responseRate) {
            this.responseRate = responseRate;
            return this;
        }

        public DonationStatsBuilder lastDonationDate(LocalDate lastDonationDate) {
            this.lastDonationDate = lastDonationDate;
            return this;
        }

        public DonationStatsBuilder nextEligibleDate(LocalDate nextEligibleDate) {
            this.nextEligibleDate = nextEligibleDate;
            return this;
        }

        public DonationStatsBuilder isEligible(boolean isEligible) {
            this.isEligible = isEligible;
            return this;
        }

        public DonationStats build() {
            return new DonationStats(this.totalDonations, this.reliabilityScore, this.responseRate, this.lastDonationDate, this.nextEligibleDate, this.isEligible);
        }

        public String toString() {
            return "DonationStats.DonationStatsBuilder(totalDonations=" + this.totalDonations + ", reliabilityScore=" + this.reliabilityScore + ", responseRate=" + this.responseRate + ", lastDonationDate=" + this.lastDonationDate + ", nextEligibleDate=" + this.nextEligibleDate + ", isEligible=" + this.isEligible + ")";
        }
    }
}

