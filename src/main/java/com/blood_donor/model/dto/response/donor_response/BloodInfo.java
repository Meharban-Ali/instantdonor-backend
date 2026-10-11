/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.model.dto.response.donor_response;

import com.blood_donor.common.enums.BloodGroup;

public class BloodInfo {
    private BloodGroup bloodGroup;
    private String bloodGroupDisplay;

    public static BloodInfoBuilder builder() {
        return new BloodInfoBuilder();
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getBloodGroupDisplay() {
        return this.bloodGroupDisplay;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setBloodGroupDisplay(String bloodGroupDisplay) {
        this.bloodGroupDisplay = bloodGroupDisplay;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BloodInfo)) {
            return false;
        }
        BloodInfo other = (BloodInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        String this$bloodGroupDisplay = this.getBloodGroupDisplay();
        String other$bloodGroupDisplay = other.getBloodGroupDisplay();
        return !(this$bloodGroupDisplay == null ? other$bloodGroupDisplay != null : !this$bloodGroupDisplay.equals(other$bloodGroupDisplay));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BloodInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $bloodGroupDisplay = this.getBloodGroupDisplay();
        result = result * 59 + ($bloodGroupDisplay == null ? 43 : $bloodGroupDisplay.hashCode());
        return result;
    }

    public String toString() {
        return "BloodInfo(bloodGroup=" + this.getBloodGroup() + ", bloodGroupDisplay=" + this.getBloodGroupDisplay() + ")";
    }

    public BloodInfo() {
    }

    public BloodInfo(BloodGroup bloodGroup, String bloodGroupDisplay) {
        this.bloodGroup = bloodGroup;
        this.bloodGroupDisplay = bloodGroupDisplay;
    }

    public static class BloodInfoBuilder {
        private BloodGroup bloodGroup;
        private String bloodGroupDisplay;

        BloodInfoBuilder() {
        }

        public BloodInfoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public BloodInfoBuilder bloodGroupDisplay(String bloodGroupDisplay) {
            this.bloodGroupDisplay = bloodGroupDisplay;
            return this;
        }

        public BloodInfo build() {
            return new BloodInfo(this.bloodGroup, this.bloodGroupDisplay);
        }

        public String toString() {
            return "BloodInfo.BloodInfoBuilder(bloodGroup=" + this.bloodGroup + ", bloodGroupDisplay=" + this.bloodGroupDisplay + ")";
        }
    }
}

