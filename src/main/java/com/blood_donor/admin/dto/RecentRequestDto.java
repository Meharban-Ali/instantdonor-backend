/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 */
package com.blood_donor.admin.dto;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RecentRequestDto {
    private Long requestId;
    private String patientName;
    private BloodGroup bloodGroup;
    private String bloodGroupDisplay;
    private UrgencyLevel urgency;
    private String city;
    private String status;
    private LocalDateTime createdAt;
    private Long minutesAgo;
    private long donorsNotified;
    private boolean hasResponse;

    public String getUrgencyColor() {
        if (this.urgency == null) {
            return "green";
        }
        return switch (this.urgency) {
            case UrgencyLevel.CRITICAL -> "red";
            case UrgencyLevel.URGENT -> "orange";
            default -> "green";
        };
    }

    public static RecentRequestDtoBuilder builder() {
        return new RecentRequestDtoBuilder();
    }

    public Long getRequestId() {
        return this.requestId;
    }

    public String getPatientName() {
        return this.patientName;
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

    public String getCity() {
        return this.city;
    }

    public String getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Long getMinutesAgo() {
        return this.minutesAgo;
    }

    public long getDonorsNotified() {
        return this.donorsNotified;
    }

    public boolean isHasResponse() {
        return this.hasResponse;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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

    public void setCity(String city) {
        this.city = city;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMinutesAgo(Long minutesAgo) {
        this.minutesAgo = minutesAgo;
    }

    public void setDonorsNotified(long donorsNotified) {
        this.donorsNotified = donorsNotified;
    }

    public void setHasResponse(boolean hasResponse) {
        this.hasResponse = hasResponse;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RecentRequestDto)) {
            return false;
        }
        RecentRequestDto other = (RecentRequestDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getDonorsNotified() != other.getDonorsNotified()) {
            return false;
        }
        if (this.isHasResponse() != other.isHasResponse()) {
            return false;
        }
        Long this$requestId = this.getRequestId();
        Long other$requestId = other.getRequestId();
        if (this$requestId == null ? other$requestId != null : !((Object)this$requestId).equals(other$requestId)) {
            return false;
        }
        Long this$minutesAgo = this.getMinutesAgo();
        Long other$minutesAgo = other.getMinutesAgo();
        if (this$minutesAgo == null ? other$minutesAgo != null : !((Object)this$minutesAgo).equals(other$minutesAgo)) {
            return false;
        }
        String this$patientName = this.getPatientName();
        String other$patientName = other.getPatientName();
        if (this$patientName == null ? other$patientName != null : !this$patientName.equals(other$patientName)) {
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
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RecentRequestDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $donorsNotified = this.getDonorsNotified();
        result = result * 59 + (int)($donorsNotified >>> 32 ^ $donorsNotified);
        result = result * 59 + (this.isHasResponse() ? 79 : 97);
        Long $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : ((Object)$requestId).hashCode());
        Long $minutesAgo = this.getMinutesAgo();
        result = result * 59 + ($minutesAgo == null ? 43 : ((Object)$minutesAgo).hashCode());
        String $patientName = this.getPatientName();
        result = result * 59 + ($patientName == null ? 43 : $patientName.hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $bloodGroupDisplay = this.getBloodGroupDisplay();
        result = result * 59 + ($bloodGroupDisplay == null ? 43 : $bloodGroupDisplay.hashCode());
        UrgencyLevel $urgency = this.getUrgency();
        result = result * 59 + ($urgency == null ? 43 : ((Object)((Object)$urgency)).hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "RecentRequestDto(requestId=" + this.getRequestId() + ", patientName=" + this.getPatientName() + ", bloodGroup=" + this.getBloodGroup() + ", bloodGroupDisplay=" + this.getBloodGroupDisplay() + ", urgency=" + this.getUrgency() + ", city=" + this.getCity() + ", status=" + this.getStatus() + ", createdAt=" + this.getCreatedAt() + ", minutesAgo=" + this.getMinutesAgo() + ", donorsNotified=" + this.getDonorsNotified() + ", hasResponse=" + this.isHasResponse() + ")";
    }

    public RecentRequestDto() {
    }

    public RecentRequestDto(Long requestId, String patientName, BloodGroup bloodGroup, String bloodGroupDisplay, UrgencyLevel urgency, String city, String status, LocalDateTime createdAt, Long minutesAgo, long donorsNotified, boolean hasResponse) {
        this.requestId = requestId;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.bloodGroupDisplay = bloodGroupDisplay;
        this.urgency = urgency;
        this.city = city;
        this.status = status;
        this.createdAt = createdAt;
        this.minutesAgo = minutesAgo;
        this.donorsNotified = donorsNotified;
        this.hasResponse = hasResponse;
    }

    public static class RecentRequestDtoBuilder {
        private Long requestId;
        private String patientName;
        private BloodGroup bloodGroup;
        private String bloodGroupDisplay;
        private UrgencyLevel urgency;
        private String city;
        private String status;
        private LocalDateTime createdAt;
        private Long minutesAgo;
        private long donorsNotified;
        private boolean hasResponse;

        RecentRequestDtoBuilder() {
        }

        public RecentRequestDtoBuilder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public RecentRequestDtoBuilder patientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public RecentRequestDtoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public RecentRequestDtoBuilder bloodGroupDisplay(String bloodGroupDisplay) {
            this.bloodGroupDisplay = bloodGroupDisplay;
            return this;
        }

        public RecentRequestDtoBuilder urgency(UrgencyLevel urgency) {
            this.urgency = urgency;
            return this;
        }

        public RecentRequestDtoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public RecentRequestDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public RecentRequestDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RecentRequestDtoBuilder minutesAgo(Long minutesAgo) {
            this.minutesAgo = minutesAgo;
            return this;
        }

        public RecentRequestDtoBuilder donorsNotified(long donorsNotified) {
            this.donorsNotified = donorsNotified;
            return this;
        }

        public RecentRequestDtoBuilder hasResponse(boolean hasResponse) {
            this.hasResponse = hasResponse;
            return this;
        }

        public RecentRequestDto build() {
            return new RecentRequestDto(this.requestId, this.patientName, this.bloodGroup, this.bloodGroupDisplay, this.urgency, this.city, this.status, this.createdAt, this.minutesAgo, this.donorsNotified, this.hasResponse);
        }

        public String toString() {
            return "RecentRequestDto.RecentRequestDtoBuilder(requestId=" + this.requestId + ", patientName=" + this.patientName + ", bloodGroup=" + this.bloodGroup + ", bloodGroupDisplay=" + this.bloodGroupDisplay + ", urgency=" + this.urgency + ", city=" + this.city + ", status=" + this.status + ", createdAt=" + this.createdAt + ", minutesAgo=" + this.minutesAgo + ", donorsNotified=" + this.donorsNotified + ", hasResponse=" + this.hasResponse + ")";
        }
    }
}

