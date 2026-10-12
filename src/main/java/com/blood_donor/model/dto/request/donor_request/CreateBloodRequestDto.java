package com.blood_donor.model.dto.request.donor_request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.donor_request.HospitalInfo;
import com.blood_donor.model.dto.request.donor_request.PatientInfo;
import com.blood_donor.model.dto.request.donor_request.RequestConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateBloodRequestDto {
    @Valid
    @NotNull(message="Patient information is required")
    private PatientInfo patient;

    @Valid
    @NotNull(message="Hospital information is required")
    private HospitalInfo hospital;

    @Valid
    @NotNull(message="Request configuration is required")
    private RequestConfig config;

    public String getPatientName() {
        return this.patient != null ? this.patient.getPatientName() : null;
    }

    public String getContactPhone() {
        return this.patient != null ? this.patient.getContactPhone() : null;
    }

    public String getHospitalName() {
        return this.hospital != null ? this.hospital.getHospitalName() : null;
    }

    public String getCity() {
        return this.hospital != null ? this.hospital.getCity() : null;
    }

    public BigDecimal getLatitude() {
        return this.hospital != null ? this.hospital.getLatitude() : null;
    }

    public BigDecimal getLongitude() {
        return this.hospital != null ? this.hospital.getLongitude() : null;
    }

    public BloodGroup getBloodGroup() {
        return this.config != null ? this.config.getBloodGroup() : null;
    }

    public Integer getUnitsRequired() {
        return this.config != null ? this.config.getUnitsRequired() : null;
    }

    public UrgencyLevel getUrgency() {
        return this.config != null ? this.config.getUrgency() : null;
    }

    public String getAdditionalInfo() {
        return this.config != null ? this.config.getAdditionalInfo() : null;
    }

    public boolean hasLocation() {
        return this.hospital != null && this.hospital.hasCoordinates();
    }

    public PatientInfo getPatient() {
        return this.patient;
    }

    public HospitalInfo getHospital() {
        return this.hospital;
    }

    public RequestConfig getConfig() {
        return this.config;
    }

    public void setPatient(PatientInfo patient) {
        this.patient = patient;
    }

    public void setHospital(HospitalInfo hospital) {
        this.hospital = hospital;
    }

    public void setConfig(RequestConfig config) {
        this.config = config;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CreateBloodRequestDto)) {
            return false;
        }
        CreateBloodRequestDto other = (CreateBloodRequestDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        PatientInfo this$patient = this.getPatient();
        PatientInfo other$patient = other.getPatient();
        if (this$patient == null ? other$patient != null : !((Object)this$patient).equals(other$patient)) {
            return false;
        }
        HospitalInfo this$hospital = this.getHospital();
        HospitalInfo other$hospital = other.getHospital();
        if (this$hospital == null ? other$hospital != null : !((Object)this$hospital).equals(other$hospital)) {
            return false;
        }
        RequestConfig this$config = this.getConfig();
        RequestConfig other$config = other.getConfig();
        return !(this$config == null ? other$config != null : !((Object)this$config).equals(other$config));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CreateBloodRequestDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        PatientInfo $patient = this.getPatient();
        result = result * 59 + ($patient == null ? 43 : ((Object)$patient).hashCode());
        HospitalInfo $hospital = this.getHospital();
        result = result * 59 + ($hospital == null ? 43 : ((Object)$hospital).hashCode());
        RequestConfig $config = this.getConfig();
        result = result * 59 + ($config == null ? 43 : ((Object)$config).hashCode());
        return result;
    }

    public String toString() {
        return "CreateBloodRequestDto(patient=" + this.getPatient() + ", hospital=" + this.getHospital() + ", config=" + this.getConfig() + ")";
    }
}

