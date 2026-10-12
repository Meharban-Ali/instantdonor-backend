package com.blood_donor.model.dto.request.donor_request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.request.donor_request.LocationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;

public class DonorRegisterRequest {
    @NotNull(message="Blood group is required")
    private BloodGroup bloodGroup;

    @Valid
    @NotNull(message="Location is required")
    private LocationRequest location;

    @PastOrPresent(message="Last donation date cannot be in future")
    private LocalDate lastDonationDate;

    public String getCity() {
        return this.location != null ? this.location.getCity() : null;
    }

    public String getState() {
        return this.location != null ? this.location.getState() : null;
    }

    public BigDecimal getLatitude() {
        return this.location != null ? this.location.getLatitude() : null;
    }

    public BigDecimal getLongitude() {
        return this.location != null ? this.location.getLongitude() : null;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public LocationRequest getLocation() {
        return this.location;
    }

    public LocalDate getLastDonationDate() {
        return this.lastDonationDate;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DonorRegisterRequest)) {
            return false;
        }
        DonorRegisterRequest other = (DonorRegisterRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        LocationRequest this$location = this.getLocation();
        LocationRequest other$location = other.getLocation();
        if (this$location == null ? other$location != null : !((Object)this$location).equals(other$location)) {
            return false;
        }
        LocalDate this$lastDonationDate = this.getLastDonationDate();
        LocalDate other$lastDonationDate = other.getLastDonationDate();
        return !(this$lastDonationDate == null ? other$lastDonationDate != null : !((Object)this$lastDonationDate).equals(other$lastDonationDate));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DonorRegisterRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        LocationRequest $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : ((Object)$location).hashCode());
        LocalDate $lastDonationDate = this.getLastDonationDate();
        result = result * 59 + ($lastDonationDate == null ? 43 : ((Object)$lastDonationDate).hashCode());
        return result;
    }

    public String toString() {
        return "DonorRegisterRequest(bloodGroup=" + this.getBloodGroup() + ", location=" + this.getLocation() + ", lastDonationDate=" + this.getLastDonationDate() + ")";
    }
}

