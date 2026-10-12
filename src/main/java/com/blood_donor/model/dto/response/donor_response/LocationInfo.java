package com.blood_donor.model.dto.response.donor_response;

import java.math.BigDecimal;

public class LocationInfo {
    private String city;
    private String state;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public static LocationInfoBuilder builder() {
        return new LocationInfoBuilder();
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocationInfo)) {
            return false;
        }
        LocationInfo other = (LocationInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        String this$state = this.getState();
        String other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        BigDecimal this$latitude = this.getLatitude();
        BigDecimal other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        BigDecimal this$longitude = this.getLongitude();
        BigDecimal other$longitude = other.getLongitude();
        return !(this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LocationInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        BigDecimal $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        BigDecimal $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        return result;
    }

    public String toString() {
        return "LocationInfo(city=" + this.getCity() + ", state=" + this.getState() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ")";
    }

    public LocationInfo() {
    }

    public LocationInfo(String city, String state, BigDecimal latitude, BigDecimal longitude) {
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static class LocationInfoBuilder {
        private String city;
        private String state;
        private BigDecimal latitude;
        private BigDecimal longitude;

        LocationInfoBuilder() {
        }

        public LocationInfoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public LocationInfoBuilder state(String state) {
            this.state = state;
            return this;
        }

        public LocationInfoBuilder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public LocationInfoBuilder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public LocationInfo build() {
            return new LocationInfo(this.city, this.state, this.latitude, this.longitude);
        }

        public String toString() {
            return "LocationInfo.LocationInfoBuilder(city=" + this.city + ", state=" + this.state + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ")";
        }
    }
}

