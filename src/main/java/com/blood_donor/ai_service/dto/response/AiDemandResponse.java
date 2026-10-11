/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.blood_donor.ai_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AiDemandResponse {
    private String city;
    @JsonProperty(value="blood_group")
    private String bloodGroup;
    @JsonProperty(value="period_days")
    private Integer periodDays;
    @JsonProperty(value="predicted_requests")
    private Integer predictedRequests;
    private Double confidence;
    private String trend;
    @JsonProperty(value="peak_days")
    private List<String> peakDays;
    private String recommendation;
    @JsonProperty(value="historical_avg")
    private Double historicalAvg;
    @JsonProperty(value="predicted_peak")
    private Integer predictedPeak;

    public int getConfidencePercent() {
        if (this.confidence == null) {
            return 0;
        }
        return (int)Math.round(this.confidence * 100.0);
    }

    public boolean isHighConfidence() {
        return this.confidence != null && this.confidence >= 0.7;
    }

    public boolean isIncreasing() {
        return "increasing".equalsIgnoreCase(this.trend);
    }

    public boolean isDecreasing() {
        return "decreasing".equalsIgnoreCase(this.trend);
    }

    public boolean needsUrgentAction() {
        return this.isIncreasing() && this.isHighConfidence();
    }

    public String getTrendIcon() {
        if (this.trend == null) {
            return "\u2192";
        }
        return switch (this.trend.toLowerCase()) {
            case "increasing" -> "\u2191";
            case "decreasing" -> "\u2193";
            default -> "\u2192";
        };
    }

    public String getCity() {
        return this.city;
    }

    public String getBloodGroup() {
        return this.bloodGroup;
    }

    public Integer getPeriodDays() {
        return this.periodDays;
    }

    public Integer getPredictedRequests() {
        return this.predictedRequests;
    }

    public Double getConfidence() {
        return this.confidence;
    }

    public String getTrend() {
        return this.trend;
    }

    public List<String> getPeakDays() {
        return this.peakDays;
    }

    public String getRecommendation() {
        return this.recommendation;
    }

    public Double getHistoricalAvg() {
        return this.historicalAvg;
    }

    public Integer getPredictedPeak() {
        return this.predictedPeak;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty(value="blood_group")
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @JsonProperty(value="period_days")
    public void setPeriodDays(Integer periodDays) {
        this.periodDays = periodDays;
    }

    @JsonProperty(value="predicted_requests")
    public void setPredictedRequests(Integer predictedRequests) {
        this.predictedRequests = predictedRequests;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    @JsonProperty(value="peak_days")
    public void setPeakDays(List<String> peakDays) {
        this.peakDays = peakDays;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @JsonProperty(value="historical_avg")
    public void setHistoricalAvg(Double historicalAvg) {
        this.historicalAvg = historicalAvg;
    }

    @JsonProperty(value="predicted_peak")
    public void setPredictedPeak(Integer predictedPeak) {
        this.predictedPeak = predictedPeak;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AiDemandResponse)) {
            return false;
        }
        AiDemandResponse other = (AiDemandResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$periodDays = this.getPeriodDays();
        Integer other$periodDays = other.getPeriodDays();
        if (this$periodDays == null ? other$periodDays != null : !((Object)this$periodDays).equals(other$periodDays)) {
            return false;
        }
        Integer this$predictedRequests = this.getPredictedRequests();
        Integer other$predictedRequests = other.getPredictedRequests();
        if (this$predictedRequests == null ? other$predictedRequests != null : !((Object)this$predictedRequests).equals(other$predictedRequests)) {
            return false;
        }
        Double this$confidence = this.getConfidence();
        Double other$confidence = other.getConfidence();
        if (this$confidence == null ? other$confidence != null : !((Object)this$confidence).equals(other$confidence)) {
            return false;
        }
        Double this$historicalAvg = this.getHistoricalAvg();
        Double other$historicalAvg = other.getHistoricalAvg();
        if (this$historicalAvg == null ? other$historicalAvg != null : !((Object)this$historicalAvg).equals(other$historicalAvg)) {
            return false;
        }
        Integer this$predictedPeak = this.getPredictedPeak();
        Integer other$predictedPeak = other.getPredictedPeak();
        if (this$predictedPeak == null ? other$predictedPeak != null : !((Object)this$predictedPeak).equals(other$predictedPeak)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        String this$bloodGroup = this.getBloodGroup();
        String other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !this$bloodGroup.equals(other$bloodGroup)) {
            return false;
        }
        String this$trend = this.getTrend();
        String other$trend = other.getTrend();
        if (this$trend == null ? other$trend != null : !this$trend.equals(other$trend)) {
            return false;
        }
        List<String> this$peakDays = this.getPeakDays();
        List<String> other$peakDays = other.getPeakDays();
        if (this$peakDays == null ? other$peakDays != null : !((Object)this$peakDays).equals(other$peakDays)) {
            return false;
        }
        String this$recommendation = this.getRecommendation();
        String other$recommendation = other.getRecommendation();
        return !(this$recommendation == null ? other$recommendation != null : !this$recommendation.equals(other$recommendation));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AiDemandResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $periodDays = this.getPeriodDays();
        result = result * 59 + ($periodDays == null ? 43 : ((Object)$periodDays).hashCode());
        Integer $predictedRequests = this.getPredictedRequests();
        result = result * 59 + ($predictedRequests == null ? 43 : ((Object)$predictedRequests).hashCode());
        Double $confidence = this.getConfidence();
        result = result * 59 + ($confidence == null ? 43 : ((Object)$confidence).hashCode());
        Double $historicalAvg = this.getHistoricalAvg();
        result = result * 59 + ($historicalAvg == null ? 43 : ((Object)$historicalAvg).hashCode());
        Integer $predictedPeak = this.getPredictedPeak();
        result = result * 59 + ($predictedPeak == null ? 43 : ((Object)$predictedPeak).hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : $bloodGroup.hashCode());
        String $trend = this.getTrend();
        result = result * 59 + ($trend == null ? 43 : $trend.hashCode());
        List<String> $peakDays = this.getPeakDays();
        result = result * 59 + ($peakDays == null ? 43 : ((Object)$peakDays).hashCode());
        String $recommendation = this.getRecommendation();
        result = result * 59 + ($recommendation == null ? 43 : $recommendation.hashCode());
        return result;
    }

    public String toString() {
        return "AiDemandResponse(city=" + this.getCity() + ", bloodGroup=" + this.getBloodGroup() + ", periodDays=" + this.getPeriodDays() + ", predictedRequests=" + this.getPredictedRequests() + ", confidence=" + this.getConfidence() + ", trend=" + this.getTrend() + ", peakDays=" + this.getPeakDays() + ", recommendation=" + this.getRecommendation() + ", historicalAvg=" + this.getHistoricalAvg() + ", predictedPeak=" + this.getPredictedPeak() + ")";
    }
}

