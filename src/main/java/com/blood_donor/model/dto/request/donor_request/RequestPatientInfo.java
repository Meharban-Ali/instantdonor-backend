package com.blood_donor.model.dto.request.donor_request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestPatientInfo {
    private String patientName;
    private String contactPhone;
    private Long createdById;
    private String createdByName;

    public static RequestPatientInfoBuilder builder() {
        return new RequestPatientInfoBuilder();
    }

    public String getPatientName() {
        return this.patientName;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public Long getCreatedById() {
        return this.createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RequestPatientInfo)) {
            return false;
        }
        RequestPatientInfo other = (RequestPatientInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$createdById = this.getCreatedById();
        Long other$createdById = other.getCreatedById();
        if (this$createdById == null ? other$createdById != null : !((Object)this$createdById).equals(other$createdById)) {
            return false;
        }
        String this$patientName = this.getPatientName();
        String other$patientName = other.getPatientName();
        if (this$patientName == null ? other$patientName != null : !this$patientName.equals(other$patientName)) {
            return false;
        }
        String this$contactPhone = this.getContactPhone();
        String other$contactPhone = other.getContactPhone();
        if (this$contactPhone == null ? other$contactPhone != null : !this$contactPhone.equals(other$contactPhone)) {
            return false;
        }
        String this$createdByName = this.getCreatedByName();
        String other$createdByName = other.getCreatedByName();
        return !(this$createdByName == null ? other$createdByName != null : !this$createdByName.equals(other$createdByName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RequestPatientInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $createdById = this.getCreatedById();
        result = result * 59 + ($createdById == null ? 43 : ((Object)$createdById).hashCode());
        String $patientName = this.getPatientName();
        result = result * 59 + ($patientName == null ? 43 : $patientName.hashCode());
        String $contactPhone = this.getContactPhone();
        result = result * 59 + ($contactPhone == null ? 43 : $contactPhone.hashCode());
        String $createdByName = this.getCreatedByName();
        result = result * 59 + ($createdByName == null ? 43 : $createdByName.hashCode());
        return result;
    }

    public String toString() {
        return "RequestPatientInfo(patientName=" + this.getPatientName() + ", contactPhone=" + this.getContactPhone() + ", createdById=" + this.getCreatedById() + ", createdByName=" + this.getCreatedByName() + ")";
    }

    public RequestPatientInfo() {
    }

    public RequestPatientInfo(String patientName, String contactPhone, Long createdById, String createdByName) {
        this.patientName = patientName;
        this.contactPhone = contactPhone;
        this.createdById = createdById;
        this.createdByName = createdByName;
    }

    public static class RequestPatientInfoBuilder {
        private String patientName;
        private String contactPhone;
        private Long createdById;
        private String createdByName;

        RequestPatientInfoBuilder() {
        }

        public RequestPatientInfoBuilder patientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public RequestPatientInfoBuilder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public RequestPatientInfoBuilder createdById(Long createdById) {
            this.createdById = createdById;
            return this;
        }

        public RequestPatientInfoBuilder createdByName(String createdByName) {
            this.createdByName = createdByName;
            return this;
        }

        public RequestPatientInfo build() {
            return new RequestPatientInfo(this.patientName, this.contactPhone, this.createdById, this.createdByName);
        }

        public String toString() {
            return "RequestPatientInfo.RequestPatientInfoBuilder(patientName=" + this.patientName + ", contactPhone=" + this.contactPhone + ", createdById=" + this.createdById + ", createdByName=" + this.createdByName + ")";
        }
    }
}

