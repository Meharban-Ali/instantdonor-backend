package com.blood_donor.model.dto.request.donor_request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.request.donor_request.AvailabilityUpdateRequest;
import com.blood_donor.model.dto.request.donor_request.LocationUpdateRequest;
import jakarta.validation.Valid;

public class DonorUpdateRequest {
    private BloodGroup bloodGroup;
    @Valid
    private LocationUpdateRequest location;
    @Valid
    private AvailabilityUpdateRequest availability;

    public boolean hasLocationUpdate() {
        return this.location != null;
    }

    public boolean hasAvailabilityUpdate() {
        return this.availability != null;
    }

    public boolean hasBloodGroupUpdate() {
        return this.bloodGroup != null;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public LocationUpdateRequest getLocation() {
        return this.location;
    }

    public AvailabilityUpdateRequest getAvailability() {
        return this.availability;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setLocation(LocationUpdateRequest location) {
        this.location = location;
    }

    public void setAvailability(AvailabilityUpdateRequest availability) {
        this.availability = availability;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DonorUpdateRequest)) {
            return false;
        }
        DonorUpdateRequest other = (DonorUpdateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        LocationUpdateRequest this$location = this.getLocation();
        LocationUpdateRequest other$location = other.getLocation();
        if (this$location == null ? other$location != null : !((Object)this$location).equals(other$location)) {
            return false;
        }
        AvailabilityUpdateRequest this$availability = this.getAvailability();
        AvailabilityUpdateRequest other$availability = other.getAvailability();
        return !(this$availability == null ? other$availability != null : !((Object)this$availability).equals(other$availability));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DonorUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        LocationUpdateRequest $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : ((Object)$location).hashCode());
        AvailabilityUpdateRequest $availability = this.getAvailability();
        result = result * 59 + ($availability == null ? 43 : ((Object)$availability).hashCode());
        return result;
    }

    public String toString() {
        return "DonorUpdateRequest(bloodGroup=" + this.getBloodGroup() + ", location=" + this.getLocation() + ", availability=" + this.getAvailability() + ")";
    }
}

