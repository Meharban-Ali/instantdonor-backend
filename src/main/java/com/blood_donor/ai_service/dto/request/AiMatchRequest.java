package com.blood_donor.ai_service.dto.request;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class AiMatchRequest {
    @NotNull(message="Blood group is required")
    @JsonProperty(value="blood_group")
    private @NotNull(message="Blood group is required") BloodGroup bloodGroup;
    @NotNull(message="Latitude is required")
    @DecimalMin(value="-90.0", message="Latitude >= -90")
    @DecimalMax(value="90.0", message="Latitude <= 90")
    private @NotNull(message="Latitude is required") @DecimalMin(value="-90.0", message="Latitude >= -90") @DecimalMax(value="90.0", message="Latitude <= 90") Double latitude;
    @NotNull(message="Longitude is required")
    @DecimalMin(value="-180.0", message="Longitude >= -180")
    @DecimalMax(value="180.0", message="Longitude <= 180")
    private @NotNull(message="Longitude is required") @DecimalMin(value="-180.0", message="Longitude >= -180") @DecimalMax(value="180.0", message="Longitude <= 180") Double longitude;
    @Min(value=1L, message="Radius >= 1 km")
    @Max(value=200L, message="Radius <= 200 km")
    @JsonProperty(value="radius_km")
    private @Min(value=1L, message="Radius >= 1 km") @Max(value=200L, message="Radius <= 200 km") Double radiusKm;
    private UrgencyLevel urgency;
    @Min(value=1L)
    @Max(value=10L)
    @JsonProperty(value="units_required")
    private @Min(value=1L) @Max(value=10L) Integer unitsRequired;
    @Min(value=1L)
    @Max(value=50L)
    private @Min(value=1L) @Max(value=50L) Integer limit;
    @JsonProperty(value="request_id")
    private Long requestId;

    public static AiMatchRequest from(BloodGroup bloodGroup, Double latitude, Double longitude, Double radiusKm, UrgencyLevel urgency, Integer unitsRequired, Integer limit, Long requestId) {
        return AiMatchRequest.builder().bloodGroup(bloodGroup).latitude(latitude).longitude(longitude).radiusKm(radiusKm != null ? radiusKm : 25.0).urgency(urgency != null ? urgency : UrgencyLevel.NORMAL).unitsRequired(unitsRequired != null ? unitsRequired : 1).limit(limit != null ? limit : 10).requestId(requestId).build();
    }

    private static Double $default$radiusKm() {
        return 25.0;
    }

    private static UrgencyLevel $default$urgency() {
        return UrgencyLevel.NORMAL;
    }

    private static Integer $default$unitsRequired() {
        return 1;
    }

    private static Integer $default$limit() {
        return 10;
    }

    public static AiMatchRequestBuilder builder() {
        return new AiMatchRequestBuilder();
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getRadiusKm() {
        return this.radiusKm;
    }

    public UrgencyLevel getUrgency() {
        return this.urgency;
    }

    public Integer getUnitsRequired() {
        return this.unitsRequired;
    }

    public Integer getLimit() {
        return this.limit;
    }

    public Long getRequestId() {
        return this.requestId;
    }

    @JsonProperty(value="blood_group")
    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty(value="radius_km")
    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    @JsonProperty(value="units_required")
    public void setUnitsRequired(Integer unitsRequired) {
        this.unitsRequired = unitsRequired;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @JsonProperty(value="request_id")
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AiMatchRequest)) {
            return false;
        }
        AiMatchRequest other = (AiMatchRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$latitude = this.getLatitude();
        Double other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        Double this$longitude = this.getLongitude();
        Double other$longitude = other.getLongitude();
        if (this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude)) {
            return false;
        }
        Double this$radiusKm = this.getRadiusKm();
        Double other$radiusKm = other.getRadiusKm();
        if (this$radiusKm == null ? other$radiusKm != null : !((Object)this$radiusKm).equals(other$radiusKm)) {
            return false;
        }
        Integer this$unitsRequired = this.getUnitsRequired();
        Integer other$unitsRequired = other.getUnitsRequired();
        if (this$unitsRequired == null ? other$unitsRequired != null : !((Object)this$unitsRequired).equals(other$unitsRequired)) {
            return false;
        }
        Integer this$limit = this.getLimit();
        Integer other$limit = other.getLimit();
        if (this$limit == null ? other$limit != null : !((Object)this$limit).equals(other$limit)) {
            return false;
        }
        Long this$requestId = this.getRequestId();
        Long other$requestId = other.getRequestId();
        if (this$requestId == null ? other$requestId != null : !((Object)this$requestId).equals(other$requestId)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        UrgencyLevel this$urgency = this.getUrgency();
        UrgencyLevel other$urgency = other.getUrgency();
        return !(this$urgency == null ? other$urgency != null : !((Object)((Object)this$urgency)).equals((Object)other$urgency));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AiMatchRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $radiusKm = this.getRadiusKm();
        result = result * 59 + ($radiusKm == null ? 43 : ((Object)$radiusKm).hashCode());
        Integer $unitsRequired = this.getUnitsRequired();
        result = result * 59 + ($unitsRequired == null ? 43 : ((Object)$unitsRequired).hashCode());
        Integer $limit = this.getLimit();
        result = result * 59 + ($limit == null ? 43 : ((Object)$limit).hashCode());
        Long $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : ((Object)$requestId).hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        UrgencyLevel $urgency = this.getUrgency();
        result = result * 59 + ($urgency == null ? 43 : ((Object)((Object)$urgency)).hashCode());
        return result;
    }

    public String toString() {
        return "AiMatchRequest(bloodGroup=" + this.getBloodGroup() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ", radiusKm=" + this.getRadiusKm() + ", urgency=" + this.getUrgency() + ", unitsRequired=" + this.getUnitsRequired() + ", limit=" + this.getLimit() + ", requestId=" + this.getRequestId() + ")";
    }

    public AiMatchRequest() {
        this.radiusKm = AiMatchRequest.$default$radiusKm();
        this.urgency = AiMatchRequest.$default$urgency();
        this.unitsRequired = AiMatchRequest.$default$unitsRequired();
        this.limit = AiMatchRequest.$default$limit();
    }

    public AiMatchRequest(BloodGroup bloodGroup, Double latitude, Double longitude, Double radiusKm, UrgencyLevel urgency, Integer unitsRequired, Integer limit, Long requestId) {
        this.bloodGroup = bloodGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusKm = radiusKm;
        this.urgency = urgency;
        this.unitsRequired = unitsRequired;
        this.limit = limit;
        this.requestId = requestId;
    }

    public static class AiMatchRequestBuilder {
        private BloodGroup bloodGroup;
        private Double latitude;
        private Double longitude;
        private boolean radiusKm$set;
        private Double radiusKm$value;
        private boolean urgency$set;
        private UrgencyLevel urgency$value;
        private boolean unitsRequired$set;
        private Integer unitsRequired$value;
        private boolean limit$set;
        private Integer limit$value;
        private Long requestId;

        AiMatchRequestBuilder() {
        }

        @JsonProperty(value="blood_group")
        public AiMatchRequestBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public AiMatchRequestBuilder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public AiMatchRequestBuilder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        @JsonProperty(value="radius_km")
        public AiMatchRequestBuilder radiusKm(Double radiusKm) {
            this.radiusKm$value = radiusKm;
            this.radiusKm$set = true;
            return this;
        }

        public AiMatchRequestBuilder urgency(UrgencyLevel urgency) {
            this.urgency$value = urgency;
            this.urgency$set = true;
            return this;
        }

        @JsonProperty(value="units_required")
        public AiMatchRequestBuilder unitsRequired(Integer unitsRequired) {
            this.unitsRequired$value = unitsRequired;
            this.unitsRequired$set = true;
            return this;
        }

        public AiMatchRequestBuilder limit(Integer limit) {
            this.limit$value = limit;
            this.limit$set = true;
            return this;
        }

        @JsonProperty(value="request_id")
        public AiMatchRequestBuilder requestId(Long requestId) {
            this.requestId = requestId;
            return this;
        }

        public AiMatchRequest build() {
            Double radiusKm$value = this.radiusKm$value;
            if (!this.radiusKm$set) {
                radiusKm$value = AiMatchRequest.$default$radiusKm();
            }
            UrgencyLevel urgency$value = this.urgency$value;
            if (!this.urgency$set) {
                urgency$value = AiMatchRequest.$default$urgency();
            }
            Integer unitsRequired$value = this.unitsRequired$value;
            if (!this.unitsRequired$set) {
                unitsRequired$value = AiMatchRequest.$default$unitsRequired();
            }
            Integer limit$value = this.limit$value;
            if (!this.limit$set) {
                limit$value = AiMatchRequest.$default$limit();
            }
            return new AiMatchRequest(this.bloodGroup, this.latitude, this.longitude, radiusKm$value, urgency$value, unitsRequired$value, limit$value, this.requestId);
        }

        public String toString() {
            return "AiMatchRequest.AiMatchRequestBuilder(bloodGroup=" + this.bloodGroup + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", radiusKm$value=" + this.radiusKm$value + ", urgency$value=" + this.urgency$value + ", unitsRequired$value=" + this.unitsRequired$value + ", limit$value=" + this.limit$value + ", requestId=" + this.requestId + ")";
        }
    }
}

