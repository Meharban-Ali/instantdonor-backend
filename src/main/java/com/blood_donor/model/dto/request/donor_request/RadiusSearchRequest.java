/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.DecimalMax
 *  jakarta.validation.constraints.DecimalMin
 *  jakarta.validation.constraints.NotNull
 */
package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class RadiusSearchRequest {
    @NotNull(message="Latitude is required for radius search")
    @DecimalMin(value="-90.0", message="Latitude must be >= -90.0")
    @DecimalMax(value="90.0", message="Latitude must be <= 90.0")
    private @NotNull(message="Latitude is required for radius search") @DecimalMin(value="-90.0", message="Latitude must be >= -90.0") @DecimalMax(value="90.0", message="Latitude must be <= 90.0") Double latitude;
    @NotNull(message="Longitude is required for radius search")
    @DecimalMin(value="-180.0", message="Longitude must be >= -180.0")
    @DecimalMax(value="180.0", message="Longitude must be <= 180.0")
    private @NotNull(message="Longitude is required for radius search") @DecimalMin(value="-180.0", message="Longitude must be >= -180.0") @DecimalMax(value="180.0", message="Longitude must be <= 180.0") Double longitude;
    @DecimalMin(value="1.0", message="Radius must be >= 1 km")
    @DecimalMax(value="100.0", message="Radius must be <= 100 km")
    private @DecimalMin(value="1.0", message="Radius must be >= 1 km") @DecimalMax(value="100.0", message="Radius must be <= 100 km") Double radiusKm = 25.0;

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getRadiusKm() {
        return this.radiusKm;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RadiusSearchRequest)) {
            return false;
        }
        RadiusSearchRequest other = (RadiusSearchRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$latitude = this.getLatitude();
        Double other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        Double this$longitude = this.getLongitude();
        Double other$longitude = other.getLongitude();
        if (this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude)) {
            return false;
        }
        Double this$radiusKm = this.getRadiusKm();
        Double other$radiusKm = other.getRadiusKm();
        return !(this$radiusKm == null ? other$radiusKm != null : !((Object)this$radiusKm).equals(other$radiusKm));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RadiusSearchRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $radiusKm = this.getRadiusKm();
        result = result * 59 + ($radiusKm == null ? 43 : ((Object)$radiusKm).hashCode());
        return result;
    }

    public String toString() {
        return "RadiusSearchRequest(latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ", radiusKm=" + this.getRadiusKm() + ")";
    }
}

