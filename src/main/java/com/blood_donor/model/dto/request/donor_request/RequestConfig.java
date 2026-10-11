/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Max
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotNull
 *  jakarta.validation.constraints.Size
 */
package com.blood_donor.model.dto.request.donor_request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RequestConfig {
    @NotNull(message="Blood group is required")
    private @NotNull(message="Blood group is required") BloodGroup bloodGroup;
    @Min(value=1L, message="At least 1 unit required")
    @Max(value=10L, message="Max 10 units per request")
    private @Min(value=1L, message="At least 1 unit required") @Max(value=10L, message="Max 10 units per request") Integer unitsRequired = 1;
    private UrgencyLevel urgency = UrgencyLevel.NORMAL;
    @Size(max=500, message="Additional info max 500 characters")
    private @Size(max=500, message="Additional info max 500 characters") String additionalInfo;

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public Integer getUnitsRequired() {
        return this.unitsRequired;
    }

    public UrgencyLevel getUrgency() {
        return this.urgency;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setUnitsRequired(Integer unitsRequired) {
        this.unitsRequired = unitsRequired;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RequestConfig)) {
            return false;
        }
        RequestConfig other = (RequestConfig)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$unitsRequired = this.getUnitsRequired();
        Integer other$unitsRequired = other.getUnitsRequired();
        if (this$unitsRequired == null ? other$unitsRequired != null : !((Object)this$unitsRequired).equals(other$unitsRequired)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        UrgencyLevel this$urgency = this.getUrgency();
        UrgencyLevel other$urgency = other.getUrgency();
        if (this$urgency == null ? other$urgency != null : !((Object)((Object)this$urgency)).equals((Object)other$urgency)) {
            return false;
        }
        String this$additionalInfo = this.getAdditionalInfo();
        String other$additionalInfo = other.getAdditionalInfo();
        return !(this$additionalInfo == null ? other$additionalInfo != null : !this$additionalInfo.equals(other$additionalInfo));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RequestConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $unitsRequired = this.getUnitsRequired();
        result = result * 59 + ($unitsRequired == null ? 43 : ((Object)$unitsRequired).hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        UrgencyLevel $urgency = this.getUrgency();
        result = result * 59 + ($urgency == null ? 43 : ((Object)((Object)$urgency)).hashCode());
        String $additionalInfo = this.getAdditionalInfo();
        result = result * 59 + ($additionalInfo == null ? 43 : $additionalInfo.hashCode());
        return result;
    }

    public String toString() {
        return "RequestConfig(bloodGroup=" + this.getBloodGroup() + ", unitsRequired=" + this.getUnitsRequired() + ", urgency=" + this.getUrgency() + ", additionalInfo=" + this.getAdditionalInfo() + ")";
    }
}

