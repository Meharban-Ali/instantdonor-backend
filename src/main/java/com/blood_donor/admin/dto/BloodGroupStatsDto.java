package com.blood_donor.admin.dto;

import com.blood_donor.common.enums.BloodGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class BloodGroupStatsDto {
    private BloodGroup bloodGroup;
    private String bloodGroupDisplay;
    private long donorCount;
    private long availableCount;
    private long pendingRequests;
    private Double supplyDemandRatio;

    public boolean isInShortage() {
        return this.supplyDemandRatio != null && this.supplyDemandRatio < 1.0;
    }

    public static BloodGroupStatsDtoBuilder builder() {
        return new BloodGroupStatsDtoBuilder();
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getBloodGroupDisplay() {
        return this.bloodGroupDisplay;
    }

    public long getDonorCount() {
        return this.donorCount;
    }

    public long getAvailableCount() {
        return this.availableCount;
    }

    public long getPendingRequests() {
        return this.pendingRequests;
    }

    public Double getSupplyDemandRatio() {
        return this.supplyDemandRatio;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setBloodGroupDisplay(String bloodGroupDisplay) {
        this.bloodGroupDisplay = bloodGroupDisplay;
    }

    public void setDonorCount(long donorCount) {
        this.donorCount = donorCount;
    }

    public void setAvailableCount(long availableCount) {
        this.availableCount = availableCount;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public void setSupplyDemandRatio(Double supplyDemandRatio) {
        this.supplyDemandRatio = supplyDemandRatio;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BloodGroupStatsDto)) {
            return false;
        }
        BloodGroupStatsDto other = (BloodGroupStatsDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getDonorCount() != other.getDonorCount()) {
            return false;
        }
        if (this.getAvailableCount() != other.getAvailableCount()) {
            return false;
        }
        if (this.getPendingRequests() != other.getPendingRequests()) {
            return false;
        }
        Double this$supplyDemandRatio = this.getSupplyDemandRatio();
        Double other$supplyDemandRatio = other.getSupplyDemandRatio();
        if (this$supplyDemandRatio == null ? other$supplyDemandRatio != null : !((Object)this$supplyDemandRatio).equals(other$supplyDemandRatio)) {
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
        return other instanceof BloodGroupStatsDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $donorCount = this.getDonorCount();
        result = result * 59 + (int)($donorCount >>> 32 ^ $donorCount);
        long $availableCount = this.getAvailableCount();
        result = result * 59 + (int)($availableCount >>> 32 ^ $availableCount);
        long $pendingRequests = this.getPendingRequests();
        result = result * 59 + (int)($pendingRequests >>> 32 ^ $pendingRequests);
        Double $supplyDemandRatio = this.getSupplyDemandRatio();
        result = result * 59 + ($supplyDemandRatio == null ? 43 : ((Object)$supplyDemandRatio).hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $bloodGroupDisplay = this.getBloodGroupDisplay();
        result = result * 59 + ($bloodGroupDisplay == null ? 43 : $bloodGroupDisplay.hashCode());
        return result;
    }

    public String toString() {
        return "BloodGroupStatsDto(bloodGroup=" + this.getBloodGroup() + ", bloodGroupDisplay=" + this.getBloodGroupDisplay() + ", donorCount=" + this.getDonorCount() + ", availableCount=" + this.getAvailableCount() + ", pendingRequests=" + this.getPendingRequests() + ", supplyDemandRatio=" + this.getSupplyDemandRatio() + ")";
    }

    public BloodGroupStatsDto() {
    }

    public BloodGroupStatsDto(BloodGroup bloodGroup, String bloodGroupDisplay, long donorCount, long availableCount, long pendingRequests, Double supplyDemandRatio) {
        this.bloodGroup = bloodGroup;
        this.bloodGroupDisplay = bloodGroupDisplay;
        this.donorCount = donorCount;
        this.availableCount = availableCount;
        this.pendingRequests = pendingRequests;
        this.supplyDemandRatio = supplyDemandRatio;
    }

    public static class BloodGroupStatsDtoBuilder {
        private BloodGroup bloodGroup;
        private String bloodGroupDisplay;
        private long donorCount;
        private long availableCount;
        private long pendingRequests;
        private Double supplyDemandRatio;

        BloodGroupStatsDtoBuilder() {
        }

        public BloodGroupStatsDtoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public BloodGroupStatsDtoBuilder bloodGroupDisplay(String bloodGroupDisplay) {
            this.bloodGroupDisplay = bloodGroupDisplay;
            return this;
        }

        public BloodGroupStatsDtoBuilder donorCount(long donorCount) {
            this.donorCount = donorCount;
            return this;
        }

        public BloodGroupStatsDtoBuilder availableCount(long availableCount) {
            this.availableCount = availableCount;
            return this;
        }

        public BloodGroupStatsDtoBuilder pendingRequests(long pendingRequests) {
            this.pendingRequests = pendingRequests;
            return this;
        }

        public BloodGroupStatsDtoBuilder supplyDemandRatio(Double supplyDemandRatio) {
            this.supplyDemandRatio = supplyDemandRatio;
            return this;
        }

        public BloodGroupStatsDto build() {
            return new BloodGroupStatsDto(this.bloodGroup, this.bloodGroupDisplay, this.donorCount, this.availableCount, this.pendingRequests, this.supplyDemandRatio);
        }

        public String toString() {
            return "BloodGroupStatsDto.BloodGroupStatsDtoBuilder(bloodGroup=" + this.bloodGroup + ", bloodGroupDisplay=" + this.bloodGroupDisplay + ", donorCount=" + this.donorCount + ", availableCount=" + this.availableCount + ", pendingRequests=" + this.pendingRequests + ", supplyDemandRatio=" + this.supplyDemandRatio + ")";
        }
    }
}

