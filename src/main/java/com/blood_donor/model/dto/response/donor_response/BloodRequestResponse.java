/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 */
package com.blood_donor.model.dto.response.donor_response;

import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
import com.blood_donor.model.dto.request.donor_request.RequestBloodInfo;
import com.blood_donor.model.dto.request.donor_request.RequestHospitalInfo;
import com.blood_donor.model.dto.request.donor_request.RequestPatientInfo;
import com.blood_donor.model.dto.request.donor_request.RequestTimeInfo;
import com.blood_donor.model.dto.request.donor_request.RequestTrackingInfo;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class BloodRequestResponse {
    private Long id;
    private BloodRequestStatus status;
    private RequestPatientInfo patient;
    private RequestHospitalInfo hospital;
    private RequestBloodInfo blood;
    private RequestTrackingInfo tracking;
    private RequestTimeInfo time;

    public boolean isActive() {
        return this.status != null && this.status.isActive();
    }

    public boolean isExpired() {
        return this.time != null && this.time.getMinutesRemaining() != null && this.time.getMinutesRemaining() <= 0L;
    }

    public static BloodRequestResponseBuilder builder() {
        return new BloodRequestResponseBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public BloodRequestStatus getStatus() {
        return this.status;
    }

    public RequestPatientInfo getPatient() {
        return this.patient;
    }

    public RequestHospitalInfo getHospital() {
        return this.hospital;
    }

    public RequestBloodInfo getBlood() {
        return this.blood;
    }

    public RequestTrackingInfo getTracking() {
        return this.tracking;
    }

    public RequestTimeInfo getTime() {
        return this.time;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(BloodRequestStatus status) {
        this.status = status;
    }

    public void setPatient(RequestPatientInfo patient) {
        this.patient = patient;
    }

    public void setHospital(RequestHospitalInfo hospital) {
        this.hospital = hospital;
    }

    public void setBlood(RequestBloodInfo blood) {
        this.blood = blood;
    }

    public void setTracking(RequestTrackingInfo tracking) {
        this.tracking = tracking;
    }

    public void setTime(RequestTimeInfo time) {
        this.time = time;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BloodRequestResponse)) {
            return false;
        }
        BloodRequestResponse other = (BloodRequestResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        BloodRequestStatus this$status = this.getStatus();
        BloodRequestStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
            return false;
        }
        RequestPatientInfo this$patient = this.getPatient();
        RequestPatientInfo other$patient = other.getPatient();
        if (this$patient == null ? other$patient != null : !((Object)this$patient).equals(other$patient)) {
            return false;
        }
        RequestHospitalInfo this$hospital = this.getHospital();
        RequestHospitalInfo other$hospital = other.getHospital();
        if (this$hospital == null ? other$hospital != null : !((Object)this$hospital).equals(other$hospital)) {
            return false;
        }
        RequestBloodInfo this$blood = this.getBlood();
        RequestBloodInfo other$blood = other.getBlood();
        if (this$blood == null ? other$blood != null : !((Object)this$blood).equals(other$blood)) {
            return false;
        }
        RequestTrackingInfo this$tracking = this.getTracking();
        RequestTrackingInfo other$tracking = other.getTracking();
        if (this$tracking == null ? other$tracking != null : !((Object)this$tracking).equals(other$tracking)) {
            return false;
        }
        RequestTimeInfo this$time = this.getTime();
        RequestTimeInfo other$time = other.getTime();
        return !(this$time == null ? other$time != null : !((Object)this$time).equals(other$time));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BloodRequestResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        BloodRequestStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
        RequestPatientInfo $patient = this.getPatient();
        result = result * 59 + ($patient == null ? 43 : ((Object)$patient).hashCode());
        RequestHospitalInfo $hospital = this.getHospital();
        result = result * 59 + ($hospital == null ? 43 : ((Object)$hospital).hashCode());
        RequestBloodInfo $blood = this.getBlood();
        result = result * 59 + ($blood == null ? 43 : ((Object)$blood).hashCode());
        RequestTrackingInfo $tracking = this.getTracking();
        result = result * 59 + ($tracking == null ? 43 : ((Object)$tracking).hashCode());
        RequestTimeInfo $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : ((Object)$time).hashCode());
        return result;
    }

    public String toString() {
        return "BloodRequestResponse(id=" + this.getId() + ", status=" + this.getStatus() + ", patient=" + this.getPatient() + ", hospital=" + this.getHospital() + ", blood=" + this.getBlood() + ", tracking=" + this.getTracking() + ", time=" + this.getTime() + ")";
    }

    public BloodRequestResponse() {
    }

    public BloodRequestResponse(Long id, BloodRequestStatus status, RequestPatientInfo patient, RequestHospitalInfo hospital, RequestBloodInfo blood, RequestTrackingInfo tracking, RequestTimeInfo time) {
        this.id = id;
        this.status = status;
        this.patient = patient;
        this.hospital = hospital;
        this.blood = blood;
        this.tracking = tracking;
        this.time = time;
    }

    public static class BloodRequestResponseBuilder {
        private Long id;
        private BloodRequestStatus status;
        private RequestPatientInfo patient;
        private RequestHospitalInfo hospital;
        private RequestBloodInfo blood;
        private RequestTrackingInfo tracking;
        private RequestTimeInfo time;

        BloodRequestResponseBuilder() {
        }

        public BloodRequestResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BloodRequestResponseBuilder status(BloodRequestStatus status) {
            this.status = status;
            return this;
        }

        public BloodRequestResponseBuilder patient(RequestPatientInfo patient) {
            this.patient = patient;
            return this;
        }

        public BloodRequestResponseBuilder hospital(RequestHospitalInfo hospital) {
            this.hospital = hospital;
            return this;
        }

        public BloodRequestResponseBuilder blood(RequestBloodInfo blood) {
            this.blood = blood;
            return this;
        }

        public BloodRequestResponseBuilder tracking(RequestTrackingInfo tracking) {
            this.tracking = tracking;
            return this;
        }

        public BloodRequestResponseBuilder time(RequestTimeInfo time) {
            this.time = time;
            return this;
        }

        public BloodRequestResponse build() {
            return new BloodRequestResponse(this.id, this.status, this.patient, this.hospital, this.blood, this.tracking, this.time);
        }

        public String toString() {
            return "BloodRequestResponse.BloodRequestResponseBuilder(id=" + this.id + ", status=" + this.status + ", patient=" + this.patient + ", hospital=" + this.hospital + ", blood=" + this.blood + ", tracking=" + this.tracking + ", time=" + this.time + ")";
        }
    }
}

