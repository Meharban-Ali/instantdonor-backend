package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class HospitalInfo {
    @NotBlank(message="Hospital name is required")
    @Size(max=200, message="Hospital name max 200 characters")
    private @NotBlank(message="Hospital name is required") @Size(max=200, message="Hospital name max 200 characters") String hospitalName;
    @NotBlank(message="City is required")
    @Size(max=100, message="City max 100 characters")
    private @NotBlank(message="City is required") @Size(max=100, message="City max 100 characters") String city;
    @DecimalMin(value="-90.0", message="Latitude must be >= -90.0")
    @DecimalMax(value="90.0", message="Latitude must be <= 90.0")
    private @DecimalMin(value="-90.0", message="Latitude must be >= -90.0") @DecimalMax(value="90.0", message="Latitude must be <= 90.0") BigDecimal latitude;
    @DecimalMin(value="-180.0", message="Longitude must be >= -180.0")
    @DecimalMax(value="180.0", message="Longitude must be <= 180.0")
    private @DecimalMin(value="-180.0", message="Longitude must be >= -180.0") @DecimalMax(value="180.0", message="Longitude must be <= 180.0") BigDecimal longitude;

    public boolean hasCoordinates() {
        return this.latitude != null && this.longitude != null;
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
        if (!(o instanceof HospitalInfo)) {
            return false;
        }
        HospitalInfo other = (HospitalInfo)o;
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
        return other instanceof HospitalInfo;
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
        return "HospitalInfo(hospitalName=" + this.getHospitalName() + ", city=" + this.getCity() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ")";
    }
}

