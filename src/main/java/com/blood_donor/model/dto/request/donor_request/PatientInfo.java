package com.blood_donor.model.dto.request.donor_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientInfo {
    @NotBlank(message="Patient name is required")
    @Size(max=100, message="Patient name max 100 characters")
    private @NotBlank(message="Patient name is required") @Size(max=100, message="Patient name max 100 characters") String patientName;
    @NotBlank(message="Contact phone is required")
    @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid phone number format")
    private @NotBlank(message="Contact phone is required") @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid phone number format") String contactPhone;

    public String getPatientName() {
        return this.patientName;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PatientInfo)) {
            return false;
        }
        PatientInfo other = (PatientInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$patientName = this.getPatientName();
        String other$patientName = other.getPatientName();
        if (this$patientName == null ? other$patientName != null : !this$patientName.equals(other$patientName)) {
            return false;
        }
        String this$contactPhone = this.getContactPhone();
        String other$contactPhone = other.getContactPhone();
        return !(this$contactPhone == null ? other$contactPhone != null : !this$contactPhone.equals(other$contactPhone));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PatientInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $patientName = this.getPatientName();
        result = result * 59 + ($patientName == null ? 43 : $patientName.hashCode());
        String $contactPhone = this.getContactPhone();
        result = result * 59 + ($contactPhone == null ? 43 : $contactPhone.hashCode());
        return result;
    }

    public String toString() {
        return "PatientInfo(patientName=" + this.getPatientName() + ", contactPhone=" + this.getContactPhone() + ")";
    }
}

