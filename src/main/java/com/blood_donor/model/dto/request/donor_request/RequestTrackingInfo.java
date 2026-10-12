package com.blood_donor.model.dto.request.donor_request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class RequestTrackingInfo {
    private Long notifiedDonors;
    private Long acceptedDonors;
    private Double fulfillmentPercentage;
    private boolean isFulfilled;

    public static RequestTrackingInfoBuilder builder() {
        return new RequestTrackingInfoBuilder();
    }

    public Long getNotifiedDonors() {
        return this.notifiedDonors;
    }

    public Long getAcceptedDonors() {
        return this.acceptedDonors;
    }

    public Double getFulfillmentPercentage() {
        return this.fulfillmentPercentage;
    }

    public boolean isFulfilled() {
        return this.isFulfilled;
    }

    public void setNotifiedDonors(Long notifiedDonors) {
        this.notifiedDonors = notifiedDonors;
    }

    public void setAcceptedDonors(Long acceptedDonors) {
        this.acceptedDonors = acceptedDonors;
    }

    public void setFulfillmentPercentage(Double fulfillmentPercentage) {
        this.fulfillmentPercentage = fulfillmentPercentage;
    }

    public void setFulfilled(boolean isFulfilled) {
        this.isFulfilled = isFulfilled;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RequestTrackingInfo)) {
            return false;
        }
        RequestTrackingInfo other = (RequestTrackingInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isFulfilled() != other.isFulfilled()) {
            return false;
        }
        Long this$notifiedDonors = this.getNotifiedDonors();
        Long other$notifiedDonors = other.getNotifiedDonors();
        if (this$notifiedDonors == null ? other$notifiedDonors != null : !((Object)this$notifiedDonors).equals(other$notifiedDonors)) {
            return false;
        }
        Long this$acceptedDonors = this.getAcceptedDonors();
        Long other$acceptedDonors = other.getAcceptedDonors();
        if (this$acceptedDonors == null ? other$acceptedDonors != null : !((Object)this$acceptedDonors).equals(other$acceptedDonors)) {
            return false;
        }
        Double this$fulfillmentPercentage = this.getFulfillmentPercentage();
        Double other$fulfillmentPercentage = other.getFulfillmentPercentage();
        return !(this$fulfillmentPercentage == null ? other$fulfillmentPercentage != null : !((Object)this$fulfillmentPercentage).equals(other$fulfillmentPercentage));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RequestTrackingInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isFulfilled() ? 79 : 97);
        Long $notifiedDonors = this.getNotifiedDonors();
        result = result * 59 + ($notifiedDonors == null ? 43 : ((Object)$notifiedDonors).hashCode());
        Long $acceptedDonors = this.getAcceptedDonors();
        result = result * 59 + ($acceptedDonors == null ? 43 : ((Object)$acceptedDonors).hashCode());
        Double $fulfillmentPercentage = this.getFulfillmentPercentage();
        result = result * 59 + ($fulfillmentPercentage == null ? 43 : ((Object)$fulfillmentPercentage).hashCode());
        return result;
    }

    public String toString() {
        return "RequestTrackingInfo(notifiedDonors=" + this.getNotifiedDonors() + ", acceptedDonors=" + this.getAcceptedDonors() + ", fulfillmentPercentage=" + this.getFulfillmentPercentage() + ", isFulfilled=" + this.isFulfilled() + ")";
    }

    public RequestTrackingInfo() {
    }

    public RequestTrackingInfo(Long notifiedDonors, Long acceptedDonors, Double fulfillmentPercentage, boolean isFulfilled) {
        this.notifiedDonors = notifiedDonors;
        this.acceptedDonors = acceptedDonors;
        this.fulfillmentPercentage = fulfillmentPercentage;
        this.isFulfilled = isFulfilled;
    }

    public static class RequestTrackingInfoBuilder {
        private Long notifiedDonors;
        private Long acceptedDonors;
        private Double fulfillmentPercentage;
        private boolean isFulfilled;

        RequestTrackingInfoBuilder() {
        }

        public RequestTrackingInfoBuilder notifiedDonors(Long notifiedDonors) {
            this.notifiedDonors = notifiedDonors;
            return this;
        }

        public RequestTrackingInfoBuilder acceptedDonors(Long acceptedDonors) {
            this.acceptedDonors = acceptedDonors;
            return this;
        }

        public RequestTrackingInfoBuilder fulfillmentPercentage(Double fulfillmentPercentage) {
            this.fulfillmentPercentage = fulfillmentPercentage;
            return this;
        }

        public RequestTrackingInfoBuilder isFulfilled(boolean isFulfilled) {
            this.isFulfilled = isFulfilled;
            return this;
        }

        public RequestTrackingInfo build() {
            return new RequestTrackingInfo(this.notifiedDonors, this.acceptedDonors, this.fulfillmentPercentage, this.isFulfilled);
        }

        public String toString() {
            return "RequestTrackingInfo.RequestTrackingInfoBuilder(notifiedDonors=" + this.notifiedDonors + ", acceptedDonors=" + this.acceptedDonors + ", fulfillmentPercentage=" + this.fulfillmentPercentage + ", isFulfilled=" + this.isFulfilled + ")";
        }
    }
}

