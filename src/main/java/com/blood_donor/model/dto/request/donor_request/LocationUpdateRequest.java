/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.DecimalMax
 *  jakarta.validation.constraints.DecimalMin
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotNull
 *  jakarta.validation.constraints.Size
 */
package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class LocationUpdateRequest {
    @NotBlank(message="City is required")
    @Size(max=100, message="City max 100 characters")
    private @NotBlank(message="City is required") @Size(max=100, message="City max 100 characters") String city;
    @NotBlank(message="State is required")
    @Size(max=100, message="State max 100 characters")
    private @NotBlank(message="State is required") @Size(max=100, message="State max 100 characters") String state;
    @NotNull(message="Latitude is required")
    @DecimalMin(value="-90.0", message="Latitude must be >= -90.0")
    @DecimalMax(value="90.0", message="Latitude must be <= 90.0")
    private @NotNull(message="Latitude is required") @DecimalMin(value="-90.0", message="Latitude must be >= -90.0") @DecimalMax(value="90.0", message="Latitude must be <= 90.0") BigDecimal latitude;
    @NotNull(message="Longitude is required")
    @DecimalMin(value="-180.0", message="Longitude must be >= -180.0")
    @DecimalMax(value="180.0", message="Longitude must be <= 180.0")
    private @NotNull(message="Longitude is required") @DecimalMin(value="-180.0", message="Longitude must be >= -180.0") @DecimalMax(value="180.0", message="Longitude must be <= 180.0") BigDecimal longitude;

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
        if (!(o instanceof LocationUpdateRequest)) {
            return false;
        }
        LocationUpdateRequest other = (LocationUpdateRequest)o;
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
        return other instanceof LocationUpdateRequest;
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
        return "LocationUpdateRequest(city=" + this.getCity() + ", state=" + this.getState() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ")";
    }
}

