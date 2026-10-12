package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.NotNull;

public class AvailabilityUpdateRequest {
    @NotNull(message="Availability status is required")
    private @NotNull(message="Availability status is required") Boolean isAvailable;

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AvailabilityUpdateRequest)) {
            return false;
        }
        AvailabilityUpdateRequest other = (AvailabilityUpdateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Boolean this$isAvailable = this.getIsAvailable();
        Boolean other$isAvailable = other.getIsAvailable();
        return !(this$isAvailable == null ? other$isAvailable != null : !((Object)this$isAvailable).equals(other$isAvailable));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AvailabilityUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Boolean $isAvailable = this.getIsAvailable();
        result = result * 59 + ($isAvailable == null ? 43 : ((Object)$isAvailable).hashCode());
        return result;
    }

    public String toString() {
        return "AvailabilityUpdateRequest(isAvailable=" + this.getIsAvailable() + ")";
    }
}

