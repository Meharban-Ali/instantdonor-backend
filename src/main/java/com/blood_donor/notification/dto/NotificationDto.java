/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  io.swagger.v3.oas.annotations.media.Schema
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotNull
 *  jakarta.validation.constraints.PastOrPresent
 */
package com.blood_donor.notification.dto;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorRequestResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@JsonDeserialize(builder=NotificationDto.NotificationDtoBuilder.class)
@JsonIgnoreProperties(ignoreUnknown=true)
@Schema(description="Data Transfer Object representing a pending/received notification for a donor")
public final class NotificationDto {
    @NotNull(message="Notification ID is required")
    @Schema(description="Unique identifier of the notification tracking response", example="1")
    private final Long id;

    @NotNull(message="Blood request ID is required")
    @Schema(description="ID of the associated emergency blood request", example="101")
    private final Long requestId;

    @NotBlank(message="Patient name is required")
    @Schema(description="Name of the patient needing blood", example="Rahul Sharma")
    private final String patientName;

    @NotNull(message="Blood group is required")
    @Schema(description="Required blood group", example="A_POSITIVE")
    private final BloodGroup bloodGroup;

    @NotBlank(message="Hospital name is required")
    @Schema(description="Name of the hospital where blood is required", example="City Hospital")
    private final String hospitalName;

    @NotBlank(message="City is required")
    @Schema(description="City where the hospital is located", example="Delhi")
    private final String city;

    @NotNull(message="Urgency level is required")
    @Schema(description="Urgency level of the blood request", example="CRITICAL")
    private final UrgencyLevel urgency;

    @NotNull(message="Notification status is required")
    @Schema(description="Status of the donor notification response", example="notified")
    private final ResponseStatus status;

    @NotNull(message="Notification timestamp is required")
    @PastOrPresent(message="Notification timestamp cannot be in the future")
    @Schema(description="Timestamp when the notification was dispatched", example="2026-06-03T21:20:00")
    private final LocalDateTime notifiedAt;

    public static NotificationDto fromEntity(DonorRequestResponse record) {
        if (record == null) {
            return null;
        }
        BloodRequest request = record.getRequest();
        return NotificationDto.builder().id(record.getId()).requestId(request != null ? request.getId() : null).patientName(request != null ? request.getPatientName() : null).bloodGroup(request != null ? request.getBloodGroup() : null).hospitalName(request != null ? request.getHospitalName() : null).city(request != null ? request.getCity() : null).urgency(request != null ? request.getUrgency() : null).status(record.getStatus()).notifiedAt(record.getNotifiedAt()).build();
    }

    NotificationDto(Long id, Long requestId, String patientName, BloodGroup bloodGroup, String hospitalName, String city, UrgencyLevel urgency, ResponseStatus status, LocalDateTime notifiedAt) {
        this.id = id;
        this.requestId = requestId;
        this.patientName = patientName;
        this.bloodGroup = bloodGroup;
        this.hospitalName = hospitalName;
        this.city = city;
        this.urgency = urgency;
        this.status = status;
        this.notifiedAt = notifiedAt;
    }

    public static NotificationDtoBuilder builder() {
        return new NotificationDtoBuilder();
    }

    public NotificationDtoBuilder toBuilder() {
        return new NotificationDtoBuilder().id(this.id).requestId(this.requestId).patientName(this.patientName).bloodGroup(this.bloodGroup).hospitalName(this.hospitalName).city(this.city).urgency(this.urgency).status(this.status).notifiedAt(this.notifiedAt);
    }

    public Long getId() {
        return this.id;
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

    public String getHospitalName() {
        return this.hospitalName;
    }

    public String getCity() {
        return this.city;
    }

    public UrgencyLevel getUrgency() {
        return this.urgency;
    }

    public ResponseStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getNotifiedAt() {
        return this.notifiedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof NotificationDto)) {
            return false;
        }
        NotificationDto other = (NotificationDto)o;
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$requestId = this.getRequestId();
        Long other$requestId = other.getRequestId();
        if (this$requestId == null ? other$requestId != null : !((Object)this$requestId).equals(other$requestId)) {
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
        String this$hospitalName = this.getHospitalName();
        String other$hospitalName = other.getHospitalName();
        if (this$hospitalName == null ? other$hospitalName != null : !this$hospitalName.equals(other$hospitalName)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        UrgencyLevel this$urgency = this.getUrgency();
        UrgencyLevel other$urgency = other.getUrgency();
        if (this$urgency == null ? other$urgency != null : !((Object)((Object)this$urgency)).equals((Object)other$urgency)) {
            return false;
        }
        ResponseStatus this$status = this.getStatus();
        ResponseStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
            return false;
        }
        LocalDateTime this$notifiedAt = this.getNotifiedAt();
        LocalDateTime other$notifiedAt = other.getNotifiedAt();
        return !(this$notifiedAt == null ? other$notifiedAt != null : !((Object)this$notifiedAt).equals(other$notifiedAt));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : ((Object)$requestId).hashCode());
        String $patientName = this.getPatientName();
        result = result * 59 + ($patientName == null ? 43 : $patientName.hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $hospitalName = this.getHospitalName();
        result = result * 59 + ($hospitalName == null ? 43 : $hospitalName.hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        UrgencyLevel $urgency = this.getUrgency();
        result = result * 59 + ($urgency == null ? 43 : ((Object)((Object)$urgency)).hashCode());
        ResponseStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
        LocalDateTime $notifiedAt = this.getNotifiedAt();
        result = result * 59 + ($notifiedAt == null ? 43 : ((Object)$notifiedAt).hashCode());
        return result;
    }

    public String toString() {
        return "NotificationDto(id=" + this.getId() + ", requestId=" + this.getRequestId() + ", patientName=" + this.getPatientName() + ", bloodGroup=" + this.getBloodGroup() + ", hospitalName=" + this.getHospitalName() + ", city=" + this.getCity() + ", urgency=" + this.getUrgency() + ", status=" + this.getStatus() + ", notifiedAt=" + this.getNotifiedAt() + ")";
    }

    public static class NotificationDtoBuilder {
        private Long id;
        private Long requestId;
        private String patientName;
        private BloodGroup bloodGroup;
        private String hospitalName;
        private String city;
        private UrgencyLevel urgency;
        private ResponseStatus status;
        private LocalDateTime notifiedAt;

        NotificationDtoBuilder() {
        }

        public NotificationDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationDtoBuilder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public NotificationDtoBuilder patientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public NotificationDtoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public NotificationDtoBuilder hospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
            return this;
        }

        public NotificationDtoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public NotificationDtoBuilder urgency(UrgencyLevel urgency) {
            this.urgency = urgency;
            return this;
        }

        public NotificationDtoBuilder status(ResponseStatus status) {
            this.status = status;
            return this;
        }

        public NotificationDtoBuilder notifiedAt(LocalDateTime notifiedAt) {
            this.notifiedAt = notifiedAt;
            return this;
        }

        public NotificationDto build() {
            return new NotificationDto(this.id, this.requestId, this.patientName, this.bloodGroup, this.hospitalName, this.city, this.urgency, this.status, this.notifiedAt);
        }

        public String toString() {
            return "NotificationDto.NotificationDtoBuilder(id=" + this.id + ", requestId=" + this.requestId + ", patientName=" + this.patientName + ", bloodGroup=" + this.bloodGroup + ", hospitalName=" + this.hospitalName + ", city=" + this.city + ", urgency=" + this.urgency + ", status=" + this.status + ", notifiedAt=" + this.notifiedAt + ")";
        }
    }
}

