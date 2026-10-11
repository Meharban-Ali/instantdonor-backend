/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.model.dto.stats;

public class PublicStatsDto {
    private long totalDonors;
    private long availableDonors;
    private long totalDonations;
    private long activeRequests;
    private long fulfilledRequests;
    private double fulfillmentRate;

    public static PublicStatsDtoBuilder builder() {
        return new PublicStatsDtoBuilder();
    }

    public long getTotalDonors() {
        return this.totalDonors;
    }

    public long getAvailableDonors() {
        return this.availableDonors;
    }

    public long getTotalDonations() {
        return this.totalDonations;
    }

    public long getActiveRequests() {
        return this.activeRequests;
    }

    public long getFulfilledRequests() {
        return this.fulfilledRequests;
    }

    public double getFulfillmentRate() {
        return this.fulfillmentRate;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public void setAvailableDonors(long availableDonors) {
        this.availableDonors = availableDonors;
    }

    public void setTotalDonations(long totalDonations) {
        this.totalDonations = totalDonations;
    }

    public void setActiveRequests(long activeRequests) {
        this.activeRequests = activeRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public void setFulfillmentRate(double fulfillmentRate) {
        this.fulfillmentRate = fulfillmentRate;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PublicStatsDto)) {
            return false;
        }
        PublicStatsDto other = (PublicStatsDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getTotalDonors() != other.getTotalDonors()) {
            return false;
        }
        if (this.getAvailableDonors() != other.getAvailableDonors()) {
            return false;
        }
        if (this.getTotalDonations() != other.getTotalDonations()) {
            return false;
        }
        if (this.getActiveRequests() != other.getActiveRequests()) {
            return false;
        }
        if (this.getFulfilledRequests() != other.getFulfilledRequests()) {
            return false;
        }
        return Double.compare(this.getFulfillmentRate(), other.getFulfillmentRate()) == 0;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PublicStatsDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $totalDonors = this.getTotalDonors();
        result = result * 59 + (int)($totalDonors >>> 32 ^ $totalDonors);
        long $availableDonors = this.getAvailableDonors();
        result = result * 59 + (int)($availableDonors >>> 32 ^ $availableDonors);
        long $totalDonations = this.getTotalDonations();
        result = result * 59 + (int)($totalDonations >>> 32 ^ $totalDonations);
        long $activeRequests = this.getActiveRequests();
        result = result * 59 + (int)($activeRequests >>> 32 ^ $activeRequests);
        long $fulfilledRequests = this.getFulfilledRequests();
        result = result * 59 + (int)($fulfilledRequests >>> 32 ^ $fulfilledRequests);
        long $fulfillmentRate = Double.doubleToLongBits(this.getFulfillmentRate());
        result = result * 59 + (int)($fulfillmentRate >>> 32 ^ $fulfillmentRate);
        return result;
    }

    public String toString() {
        return "PublicStatsDto(totalDonors=" + this.getTotalDonors() + ", availableDonors=" + this.getAvailableDonors() + ", totalDonations=" + this.getTotalDonations() + ", activeRequests=" + this.getActiveRequests() + ", fulfilledRequests=" + this.getFulfilledRequests() + ", fulfillmentRate=" + this.getFulfillmentRate() + ")";
    }

    public PublicStatsDto() {
    }

    public PublicStatsDto(long totalDonors, long availableDonors, long totalDonations, long activeRequests, long fulfilledRequests, double fulfillmentRate) {
        this.totalDonors = totalDonors;
        this.availableDonors = availableDonors;
        this.totalDonations = totalDonations;
        this.activeRequests = activeRequests;
        this.fulfilledRequests = fulfilledRequests;
        this.fulfillmentRate = fulfillmentRate;
    }

    public static class PublicStatsDtoBuilder {
        private long totalDonors;
        private long availableDonors;
        private long totalDonations;
        private long activeRequests;
        private long fulfilledRequests;
        private double fulfillmentRate;

        PublicStatsDtoBuilder() {
        }

        public PublicStatsDtoBuilder totalDonors(long totalDonors) {
            this.totalDonors = totalDonors;
            return this;
        }

        public PublicStatsDtoBuilder availableDonors(long availableDonors) {
            this.availableDonors = availableDonors;
            return this;
        }

        public PublicStatsDtoBuilder totalDonations(long totalDonations) {
            this.totalDonations = totalDonations;
            return this;
        }

        public PublicStatsDtoBuilder activeRequests(long activeRequests) {
            this.activeRequests = activeRequests;
            return this;
        }

        public PublicStatsDtoBuilder fulfilledRequests(long fulfilledRequests) {
            this.fulfilledRequests = fulfilledRequests;
            return this;
        }

        public PublicStatsDtoBuilder fulfillmentRate(double fulfillmentRate) {
            this.fulfillmentRate = fulfillmentRate;
            return this;
        }

        public PublicStatsDto build() {
            return new PublicStatsDto(this.totalDonors, this.availableDonors, this.totalDonations, this.activeRequests, this.fulfilledRequests, this.fulfillmentRate);
        }

        public String toString() {
            return "PublicStatsDto.PublicStatsDtoBuilder(totalDonors=" + this.totalDonors + ", availableDonors=" + this.availableDonors + ", totalDonations=" + this.totalDonations + ", activeRequests=" + this.activeRequests + ", fulfilledRequests=" + this.fulfilledRequests + ", fulfillmentRate=" + this.fulfillmentRate + ")";
        }
    }
}

