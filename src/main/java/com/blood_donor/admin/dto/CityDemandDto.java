package com.blood_donor.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CityDemandDto {
    private String city;
    private String state;
    private long activeRequests;
    private long fulfilledRequests;
    private long availableDonors;
    private Double fulfillmentRate;
    private long criticalRequests;

    public String getDemandLevel() {
        if (this.activeRequests >= 10L) {
            return "HIGH";
        }
        if (this.activeRequests >= 5L) {
            return "MEDIUM";
        }
        return "LOW";
    }

    public static CityDemandDtoBuilder builder() {
        return new CityDemandDtoBuilder();
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public long getActiveRequests() {
        return this.activeRequests;
    }

    public long getFulfilledRequests() {
        return this.fulfilledRequests;
    }

    public long getAvailableDonors() {
        return this.availableDonors;
    }

    public Double getFulfillmentRate() {
        return this.fulfillmentRate;
    }

    public long getCriticalRequests() {
        return this.criticalRequests;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setActiveRequests(long activeRequests) {
        this.activeRequests = activeRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public void setAvailableDonors(long availableDonors) {
        this.availableDonors = availableDonors;
    }

    public void setFulfillmentRate(Double fulfillmentRate) {
        this.fulfillmentRate = fulfillmentRate;
    }

    public void setCriticalRequests(long criticalRequests) {
        this.criticalRequests = criticalRequests;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CityDemandDto)) {
            return false;
        }
        CityDemandDto other = (CityDemandDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getActiveRequests() != other.getActiveRequests()) {
            return false;
        }
        if (this.getFulfilledRequests() != other.getFulfilledRequests()) {
            return false;
        }
        if (this.getAvailableDonors() != other.getAvailableDonors()) {
            return false;
        }
        if (this.getCriticalRequests() != other.getCriticalRequests()) {
            return false;
        }
        Double this$fulfillmentRate = this.getFulfillmentRate();
        Double other$fulfillmentRate = other.getFulfillmentRate();
        if (this$fulfillmentRate == null ? other$fulfillmentRate != null : !((Object)this$fulfillmentRate).equals(other$fulfillmentRate)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        String this$state = this.getState();
        String other$state = other.getState();
        return !(this$state == null ? other$state != null : !this$state.equals(other$state));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CityDemandDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $activeRequests = this.getActiveRequests();
        result = result * 59 + (int)($activeRequests >>> 32 ^ $activeRequests);
        long $fulfilledRequests = this.getFulfilledRequests();
        result = result * 59 + (int)($fulfilledRequests >>> 32 ^ $fulfilledRequests);
        long $availableDonors = this.getAvailableDonors();
        result = result * 59 + (int)($availableDonors >>> 32 ^ $availableDonors);
        long $criticalRequests = this.getCriticalRequests();
        result = result * 59 + (int)($criticalRequests >>> 32 ^ $criticalRequests);
        Double $fulfillmentRate = this.getFulfillmentRate();
        result = result * 59 + ($fulfillmentRate == null ? 43 : ((Object)$fulfillmentRate).hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        return result;
    }

    public String toString() {
        return "CityDemandDto(city=" + this.getCity() + ", state=" + this.getState() + ", activeRequests=" + this.getActiveRequests() + ", fulfilledRequests=" + this.getFulfilledRequests() + ", availableDonors=" + this.getAvailableDonors() + ", fulfillmentRate=" + this.getFulfillmentRate() + ", criticalRequests=" + this.getCriticalRequests() + ")";
    }

    public CityDemandDto() {
    }

    public CityDemandDto(String city, String state, long activeRequests, long fulfilledRequests, long availableDonors, Double fulfillmentRate, long criticalRequests) {
        this.city = city;
        this.state = state;
        this.activeRequests = activeRequests;
        this.fulfilledRequests = fulfilledRequests;
        this.availableDonors = availableDonors;
        this.fulfillmentRate = fulfillmentRate;
        this.criticalRequests = criticalRequests;
    }

    public static class CityDemandDtoBuilder {
        private String city;
        private String state;
        private long activeRequests;
        private long fulfilledRequests;
        private long availableDonors;
        private Double fulfillmentRate;
        private long criticalRequests;

        CityDemandDtoBuilder() {
        }

        public CityDemandDtoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public CityDemandDtoBuilder state(String state) {
            this.state = state;
            return this;
        }

        public CityDemandDtoBuilder activeRequests(long activeRequests) {
            this.activeRequests = activeRequests;
            return this;
        }

        public CityDemandDtoBuilder fulfilledRequests(long fulfilledRequests) {
            this.fulfilledRequests = fulfilledRequests;
            return this;
        }

        public CityDemandDtoBuilder availableDonors(long availableDonors) {
            this.availableDonors = availableDonors;
            return this;
        }

        public CityDemandDtoBuilder fulfillmentRate(Double fulfillmentRate) {
            this.fulfillmentRate = fulfillmentRate;
            return this;
        }

        public CityDemandDtoBuilder criticalRequests(long criticalRequests) {
            this.criticalRequests = criticalRequests;
            return this;
        }

        public CityDemandDto build() {
            return new CityDemandDto(this.city, this.state, this.activeRequests, this.fulfilledRequests, this.availableDonors, this.fulfillmentRate, this.criticalRequests);
        }

        public String toString() {
            return "CityDemandDto.CityDemandDtoBuilder(city=" + this.city + ", state=" + this.state + ", activeRequests=" + this.activeRequests + ", fulfilledRequests=" + this.fulfilledRequests + ", availableDonors=" + this.availableDonors + ", fulfillmentRate=" + this.fulfillmentRate + ", criticalRequests=" + this.criticalRequests + ")";
        }
    }
}

