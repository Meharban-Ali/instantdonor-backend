package com.blood_donor.ai_service.dto.request;

import com.blood_donor.common.enums.BloodGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class AiDemandRequest {
    @NotBlank(message="City is required")
    @Size(min=2, max=100, message="City 2-100 chars")
    private @NotBlank(message="City is required") @Size(min=2, max=100, message="City 2-100 chars") String city;
    @JsonProperty(value="blood_group")
    private BloodGroup bloodGroup;
    @Min(value=1L, message="Days >= 1")
    @Max(value=90L, message="Days <= 90")
    @JsonProperty(value="days_ahead")
    private @Min(value=1L, message="Days >= 1") @Max(value=90L, message="Days <= 90") Integer daysAhead;

    private static Integer $default$daysAhead() {
        return 30;
    }

    public static AiDemandRequestBuilder builder() {
        return new AiDemandRequestBuilder();
    }

    public String getCity() {
        return this.city;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public Integer getDaysAhead() {
        return this.daysAhead;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty(value="blood_group")
    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @JsonProperty(value="days_ahead")
    public void setDaysAhead(Integer daysAhead) {
        this.daysAhead = daysAhead;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AiDemandRequest)) {
            return false;
        }
        AiDemandRequest other = (AiDemandRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$daysAhead = this.getDaysAhead();
        Integer other$daysAhead = other.getDaysAhead();
        if (this$daysAhead == null ? other$daysAhead != null : !((Object)this$daysAhead).equals(other$daysAhead)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        return !(this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AiDemandRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $daysAhead = this.getDaysAhead();
        result = result * 59 + ($daysAhead == null ? 43 : ((Object)$daysAhead).hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        return result;
    }

    public String toString() {
        return "AiDemandRequest(city=" + this.getCity() + ", bloodGroup=" + this.getBloodGroup() + ", daysAhead=" + this.getDaysAhead() + ")";
    }

    public AiDemandRequest() {
        this.daysAhead = AiDemandRequest.$default$daysAhead();
    }

    public AiDemandRequest(String city, BloodGroup bloodGroup, Integer daysAhead) {
        this.city = city;
        this.bloodGroup = bloodGroup;
        this.daysAhead = daysAhead;
    }

    public static class AiDemandRequestBuilder {
        private String city;
        private BloodGroup bloodGroup;
        private boolean daysAhead$set;
        private Integer daysAhead$value;

        AiDemandRequestBuilder() {
        }

        public AiDemandRequestBuilder city(String city) {
            this.city = city;
            return this;
        }

        @JsonProperty(value="blood_group")
        public AiDemandRequestBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        @JsonProperty(value="days_ahead")
        public AiDemandRequestBuilder daysAhead(Integer daysAhead) {
            this.daysAhead$value = daysAhead;
            this.daysAhead$set = true;
            return this;
        }

        public AiDemandRequest build() {
            Integer daysAhead$value = this.daysAhead$value;
            if (!this.daysAhead$set) {
                daysAhead$value = AiDemandRequest.$default$daysAhead();
            }
            return new AiDemandRequest(this.city, this.bloodGroup, daysAhead$value);
        }

        public String toString() {
            return "AiDemandRequest.AiDemandRequestBuilder(city=" + this.city + ", bloodGroup=" + this.bloodGroup + ", daysAhead$value=" + this.daysAhead$value + ")";
        }
    }
}

