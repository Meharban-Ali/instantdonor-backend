package com.blood_donor.ai_service.dto.response;

import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AiMatchResponse {
    @JsonProperty(value="request_id")
    private Long requestId;
    @JsonProperty(value="blood_group")
    private String bloodGroup;
    private String urgency;
    @JsonProperty(value="total_found")
    private Integer totalFound;
    private List<AiDonorResult> donors;
    @JsonProperty(value="compatible_groups_searched")
    private List<String> compatibleGroupsSearched;
    @JsonProperty(value="search_radius_km")
    private Double searchRadiusKm;
    @JsonProperty(value="processing_time_ms")
    private Double processingTimeMs;
    @JsonProperty(value="algorithm_version")
    private String algorithmVersion;
    private Boolean cached;

    public boolean hasResults() {
        return this.totalFound != null && this.totalFound > 0 && this.donors != null && !this.donors.isEmpty();
    }

    public AiDonorResult getTopDonor() {
        if (this.donors == null || this.donors.isEmpty()) {
            return null;
        }
        return this.donors.get(0);
    }

    public Long getRequestId() {
        return this.requestId;
    }

    public String getBloodGroup() {
        return this.bloodGroup;
    }

    public String getUrgency() {
        return this.urgency;
    }

    public Integer getTotalFound() {
        return this.totalFound;
    }

    public List<AiDonorResult> getDonors() {
        return this.donors;
    }

    public List<String> getCompatibleGroupsSearched() {
        return this.compatibleGroupsSearched;
    }

    public Double getSearchRadiusKm() {
        return this.searchRadiusKm;
    }

    public Double getProcessingTimeMs() {
        return this.processingTimeMs;
    }

    public String getAlgorithmVersion() {
        return this.algorithmVersion;
    }

    public Boolean getCached() {
        return this.cached;
    }

    @JsonProperty(value="request_id")
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @JsonProperty(value="blood_group")
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    @JsonProperty(value="total_found")
    public void setTotalFound(Integer totalFound) {
        this.totalFound = totalFound;
    }

    public void setDonors(List<AiDonorResult> donors) {
        this.donors = donors;
    }

    @JsonProperty(value="compatible_groups_searched")
    public void setCompatibleGroupsSearched(List<String> compatibleGroupsSearched) {
        this.compatibleGroupsSearched = compatibleGroupsSearched;
    }

    @JsonProperty(value="search_radius_km")
    public void setSearchRadiusKm(Double searchRadiusKm) {
        this.searchRadiusKm = searchRadiusKm;
    }

    @JsonProperty(value="processing_time_ms")
    public void setProcessingTimeMs(Double processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    @JsonProperty(value="algorithm_version")
    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
    }

    public void setCached(Boolean cached) {
        this.cached = cached;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AiMatchResponse)) {
            return false;
        }
        AiMatchResponse other = (AiMatchResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$requestId = this.getRequestId();
        Long other$requestId = other.getRequestId();
        if (this$requestId == null ? other$requestId != null : !((Object)this$requestId).equals(other$requestId)) {
            return false;
        }
        Integer this$totalFound = this.getTotalFound();
        Integer other$totalFound = other.getTotalFound();
        if (this$totalFound == null ? other$totalFound != null : !((Object)this$totalFound).equals(other$totalFound)) {
            return false;
        }
        Double this$searchRadiusKm = this.getSearchRadiusKm();
        Double other$searchRadiusKm = other.getSearchRadiusKm();
        if (this$searchRadiusKm == null ? other$searchRadiusKm != null : !((Object)this$searchRadiusKm).equals(other$searchRadiusKm)) {
            return false;
        }
        Double this$processingTimeMs = this.getProcessingTimeMs();
        Double other$processingTimeMs = other.getProcessingTimeMs();
        if (this$processingTimeMs == null ? other$processingTimeMs != null : !((Object)this$processingTimeMs).equals(other$processingTimeMs)) {
            return false;
        }
        Boolean this$cached = this.getCached();
        Boolean other$cached = other.getCached();
        if (this$cached == null ? other$cached != null : !((Object)this$cached).equals(other$cached)) {
            return false;
        }
        String this$bloodGroup = this.getBloodGroup();
        String other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !this$bloodGroup.equals(other$bloodGroup)) {
            return false;
        }
        String this$urgency = this.getUrgency();
        String other$urgency = other.getUrgency();
        if (this$urgency == null ? other$urgency != null : !this$urgency.equals(other$urgency)) {
            return false;
        }
        List<AiDonorResult> this$donors = this.getDonors();
        List<AiDonorResult> other$donors = other.getDonors();
        if (this$donors == null ? other$donors != null : !((Object)this$donors).equals(other$donors)) {
            return false;
        }
        List<String> this$compatibleGroupsSearched = this.getCompatibleGroupsSearched();
        List<String> other$compatibleGroupsSearched = other.getCompatibleGroupsSearched();
        if (this$compatibleGroupsSearched == null ? other$compatibleGroupsSearched != null : !((Object)this$compatibleGroupsSearched).equals(other$compatibleGroupsSearched)) {
            return false;
        }
        String this$algorithmVersion = this.getAlgorithmVersion();
        String other$algorithmVersion = other.getAlgorithmVersion();
        return !(this$algorithmVersion == null ? other$algorithmVersion != null : !this$algorithmVersion.equals(other$algorithmVersion));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AiMatchResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $requestId = this.getRequestId();
        result = result * 59 + ($requestId == null ? 43 : ((Object)$requestId).hashCode());
        Integer $totalFound = this.getTotalFound();
        result = result * 59 + ($totalFound == null ? 43 : ((Object)$totalFound).hashCode());
        Double $searchRadiusKm = this.getSearchRadiusKm();
        result = result * 59 + ($searchRadiusKm == null ? 43 : ((Object)$searchRadiusKm).hashCode());
        Double $processingTimeMs = this.getProcessingTimeMs();
        result = result * 59 + ($processingTimeMs == null ? 43 : ((Object)$processingTimeMs).hashCode());
        Boolean $cached = this.getCached();
        result = result * 59 + ($cached == null ? 43 : ((Object)$cached).hashCode());
        String $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : $bloodGroup.hashCode());
        String $urgency = this.getUrgency();
        result = result * 59 + ($urgency == null ? 43 : $urgency.hashCode());
        List<AiDonorResult> $donors = this.getDonors();
        result = result * 59 + ($donors == null ? 43 : ((Object)$donors).hashCode());
        List<String> $compatibleGroupsSearched = this.getCompatibleGroupsSearched();
        result = result * 59 + ($compatibleGroupsSearched == null ? 43 : ((Object)$compatibleGroupsSearched).hashCode());
        String $algorithmVersion = this.getAlgorithmVersion();
        result = result * 59 + ($algorithmVersion == null ? 43 : $algorithmVersion.hashCode());
        return result;
    }

    public String toString() {
        return "AiMatchResponse(requestId=" + this.getRequestId() + ", bloodGroup=" + this.getBloodGroup() + ", urgency=" + this.getUrgency() + ", totalFound=" + this.getTotalFound() + ", donors=" + this.getDonors() + ", compatibleGroupsSearched=" + this.getCompatibleGroupsSearched() + ", searchRadiusKm=" + this.getSearchRadiusKm() + ", processingTimeMs=" + this.getProcessingTimeMs() + ", algorithmVersion=" + this.getAlgorithmVersion() + ", cached=" + this.getCached() + ")";
    }
}

