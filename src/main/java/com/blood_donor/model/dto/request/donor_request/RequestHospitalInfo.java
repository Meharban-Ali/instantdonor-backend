package com.blood_donor.model.dto.request.donor_request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestHospitalInfo {
    private String hospitalName;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public static RequestHospitalInfoBuilder builder() {
        return new RequestHospitalInfoBuilder();
    }

    public String getHospitalName() {
        return this.hospitalName;
    }

    public String getCity() {
        return this.city;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setCity(String city) {
        this.city = city;
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
        if (!(o instanceof RequestHospitalInfo)) {
            return false;
        }
        RequestHospitalInfo other = (RequestHospitalInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$hospitalName = this.getHospitalName();
        String other$hospitalName = other.getHospitalName();
        if (this$hospitalName == null ? other$hospitalName != null : !this$hospitalName.equals(other$hospitalName)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
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
        return other instanceof RequestHospitalInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $hospitalName = this.getHospitalName();
        result = result * 59 + ($hospitalName == null ? 43 : $hospitalName.hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        BigDecimal $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        BigDecimal $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        return result;
    }

    public String toString() {
        return "RequestHospitalInfo(hospitalName=" + this.getHospitalName() + ", city=" + this.getCity() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ")";
    }

    public RequestHospitalInfo() {
    }

    public RequestHospitalInfo(String hospitalName, String city, BigDecimal latitude, BigDecimal longitude) {
        this.hospitalName = hospitalName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static class RequestHospitalInfoBuilder {
        private String hospitalName;
        private String city;
        private BigDecimal latitude;
        private BigDecimal longitude;

        RequestHospitalInfoBuilder() {
        }

        public RequestHospitalInfoBuilder hospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
            return this;
        }

        public RequestHospitalInfoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public RequestHospitalInfoBuilder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public RequestHospitalInfoBuilder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public RequestHospitalInfo build() {
            return new RequestHospitalInfo(this.hospitalName, this.city, this.latitude, this.longitude);
        }

        public String toString() {
            return "RequestHospitalInfo.RequestHospitalInfoBuilder(hospitalName=" + this.hospitalName + ", city=" + this.city + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ")";
        }
    }
}

