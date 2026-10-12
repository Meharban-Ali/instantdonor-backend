package com.blood_donor.ai_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ScoreBreakdown {
    @JsonProperty(value="distance_score")
    private Double distanceScore;
    @JsonProperty(value="reliability_score")
    private Double reliabilityScore;
    @JsonProperty(value="eligibility_score")
    private Double eligibilityScore;
    @JsonProperty(value="response_rate_score")
    private Double responseRateScore;
    @JsonProperty(value="final_score")
    private Double finalScore;
    @JsonProperty(value="weights_used")
    private Map<String, Double> weightsUsed;

    public Double getDistanceScore() {
        return this.distanceScore;
    }

    public Double getReliabilityScore() {
        return this.reliabilityScore;
    }

    public Double getEligibilityScore() {
        return this.eligibilityScore;
    }

    public Double getResponseRateScore() {
        return this.responseRateScore;
    }

    public Double getFinalScore() {
        return this.finalScore;
    }

    public Map<String, Double> getWeightsUsed() {
        return this.weightsUsed;
    }

    @JsonProperty(value="distance_score")
    public void setDistanceScore(Double distanceScore) {
        this.distanceScore = distanceScore;
    }

    @JsonProperty(value="reliability_score")
    public void setReliabilityScore(Double reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    @JsonProperty(value="eligibility_score")
    public void setEligibilityScore(Double eligibilityScore) {
        this.eligibilityScore = eligibilityScore;
    }

    @JsonProperty(value="response_rate_score")
    public void setResponseRateScore(Double responseRateScore) {
        this.responseRateScore = responseRateScore;
    }

    @JsonProperty(value="final_score")
    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    @JsonProperty(value="weights_used")
    public void setWeightsUsed(Map<String, Double> weightsUsed) {
        this.weightsUsed = weightsUsed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ScoreBreakdown)) {
            return false;
        }
        ScoreBreakdown other = (ScoreBreakdown)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$distanceScore = this.getDistanceScore();
        Double other$distanceScore = other.getDistanceScore();
        if (this$distanceScore == null ? other$distanceScore != null : !((Object)this$distanceScore).equals(other$distanceScore)) {
            return false;
        }
        Double this$reliabilityScore = this.getReliabilityScore();
        Double other$reliabilityScore = other.getReliabilityScore();
        if (this$reliabilityScore == null ? other$reliabilityScore != null : !((Object)this$reliabilityScore).equals(other$reliabilityScore)) {
            return false;
        }
        Double this$eligibilityScore = this.getEligibilityScore();
        Double other$eligibilityScore = other.getEligibilityScore();
        if (this$eligibilityScore == null ? other$eligibilityScore != null : !((Object)this$eligibilityScore).equals(other$eligibilityScore)) {
            return false;
        }
        Double this$responseRateScore = this.getResponseRateScore();
        Double other$responseRateScore = other.getResponseRateScore();
        if (this$responseRateScore == null ? other$responseRateScore != null : !((Object)this$responseRateScore).equals(other$responseRateScore)) {
            return false;
        }
        Double this$finalScore = this.getFinalScore();
        Double other$finalScore = other.getFinalScore();
        if (this$finalScore == null ? other$finalScore != null : !((Object)this$finalScore).equals(other$finalScore)) {
            return false;
        }
        Map<String, Double> this$weightsUsed = this.getWeightsUsed();
        Map<String, Double> other$weightsUsed = other.getWeightsUsed();
        return !(this$weightsUsed == null ? other$weightsUsed != null : !((Object)this$weightsUsed).equals(other$weightsUsed));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ScoreBreakdown;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $distanceScore = this.getDistanceScore();
        result = result * 59 + ($distanceScore == null ? 43 : ((Object)$distanceScore).hashCode());
        Double $reliabilityScore = this.getReliabilityScore();
        result = result * 59 + ($reliabilityScore == null ? 43 : ((Object)$reliabilityScore).hashCode());
        Double $eligibilityScore = this.getEligibilityScore();
        result = result * 59 + ($eligibilityScore == null ? 43 : ((Object)$eligibilityScore).hashCode());
        Double $responseRateScore = this.getResponseRateScore();
        result = result * 59 + ($responseRateScore == null ? 43 : ((Object)$responseRateScore).hashCode());
        Double $finalScore = this.getFinalScore();
        result = result * 59 + ($finalScore == null ? 43 : ((Object)$finalScore).hashCode());
        Map<String, Double> $weightsUsed = this.getWeightsUsed();
        result = result * 59 + ($weightsUsed == null ? 43 : ((Object)$weightsUsed).hashCode());
        return result;
    }

    public String toString() {
        return "ScoreBreakdown(distanceScore=" + this.getDistanceScore() + ", reliabilityScore=" + this.getReliabilityScore() + ", eligibilityScore=" + this.getEligibilityScore() + ", responseRateScore=" + this.getResponseRateScore() + ", finalScore=" + this.getFinalScore() + ", weightsUsed=" + this.getWeightsUsed() + ")";
    }
}

