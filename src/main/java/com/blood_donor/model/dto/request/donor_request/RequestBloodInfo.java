package com.blood_donor.model.dto.request.donor_request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestBloodInfo {
    private BloodGroup bloodGroup;
    private String bloodGroupDisplay;
    private UrgencyLevel urgency;
    private Integer unitsRequired;
    private String additionalInfo;

    public static RequestBloodInfoBuilder builder() {
        return new RequestBloodInfoBuilder();
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getBloodGroupDisplay() {
        return this.bloodGroupDisplay;
    }

    public UrgencyLevel getUrgency() {
        return this.urgency;
    }

    public Integer getUnitsRequired() {
        return this.unitsRequired;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setBloodGroupDisplay(String bloodGroupDisplay) {
        this.bloodGroupDisplay = bloodGroupDisplay;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public void setUnitsRequired(Integer unitsRequired) {
        this.unitsRequired = unitsRequired;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RequestBloodInfo)) {
            return false;
        }
        RequestBloodInfo other = (RequestBloodInfo)o;
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
        String this$bloodGroupDisplay = this.getBloodGroupDisplay();
        String other$bloodGroupDisplay = other.getBloodGroupDisplay();
        if (this$bloodGroupDisplay == null ? other$bloodGroupDisplay != null : !this$bloodGroupDisplay.equals(other$bloodGroupDisplay)) {
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
        return other instanceof RequestBloodInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $unitsRequired = this.getUnitsRequired();
        result = result * 59 + ($unitsRequired == null ? 43 : ((Object)$unitsRequired).hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $bloodGroupDisplay = this.getBloodGroupDisplay();
        result = result * 59 + ($bloodGroupDisplay == null ? 43 : $bloodGroupDisplay.hashCode());
        UrgencyLevel $urgency = this.getUrgency();
        result = result * 59 + ($urgency == null ? 43 : ((Object)((Object)$urgency)).hashCode());
        String $additionalInfo = this.getAdditionalInfo();
        result = result * 59 + ($additionalInfo == null ? 43 : $additionalInfo.hashCode());
        return result;
    }

    public String toString() {
        return "RequestBloodInfo(bloodGroup=" + this.getBloodGroup() + ", bloodGroupDisplay=" + this.getBloodGroupDisplay() + ", urgency=" + this.getUrgency() + ", unitsRequired=" + this.getUnitsRequired() + ", additionalInfo=" + this.getAdditionalInfo() + ")";
    }

    public RequestBloodInfo() {
    }

    public RequestBloodInfo(BloodGroup bloodGroup, String bloodGroupDisplay, UrgencyLevel urgency, Integer unitsRequired, String additionalInfo) {
        this.bloodGroup = bloodGroup;
        this.bloodGroupDisplay = bloodGroupDisplay;
        this.urgency = urgency;
        this.unitsRequired = unitsRequired;
        this.additionalInfo = additionalInfo;
    }

    public static class RequestBloodInfoBuilder {
        private BloodGroup bloodGroup;
        private String bloodGroupDisplay;
        private UrgencyLevel urgency;
        private Integer unitsRequired;
        private String additionalInfo;

        RequestBloodInfoBuilder() {
        }

        public RequestBloodInfoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public RequestBloodInfoBuilder bloodGroupDisplay(String bloodGroupDisplay) {
            this.bloodGroupDisplay = bloodGroupDisplay;
            return this;
        }

        public RequestBloodInfoBuilder urgency(UrgencyLevel urgency) {
            this.urgency = urgency;
            return this;
        }

        public RequestBloodInfoBuilder unitsRequired(Integer unitsRequired) {
            this.unitsRequired = unitsRequired;
            return this;
        }

        public RequestBloodInfoBuilder additionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public RequestBloodInfo build() {
            return new RequestBloodInfo(this.bloodGroup, this.bloodGroupDisplay, this.urgency, this.unitsRequired, this.additionalInfo);
        }

        public String toString() {
            return "RequestBloodInfo.RequestBloodInfoBuilder(bloodGroup=" + this.bloodGroup + ", bloodGroupDisplay=" + this.bloodGroupDisplay + ", urgency=" + this.urgency + ", unitsRequired=" + this.unitsRequired + ", additionalInfo=" + this.additionalInfo + ")";
        }
    }
}

