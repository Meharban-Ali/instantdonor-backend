/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.blood_donor.ai_service.dto.response;

import com.blood_donor.ai_service.dto.response.ScoreBreakdown;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AiDonorResult {
    @JsonProperty(value="donor_id")
    private Long donorId;
    @JsonProperty(value="user_id")
    private Long userId;
    private Integer rank;
    private String name;
    @JsonProperty(value="phone_masked")
    private String phoneMasked;
    @JsonProperty(value="blood_group")
    private String bloodGroup;
    @JsonProperty(value="blood_group_display")
    private String bloodGroupDisplay;
    private String city;
    private Double latitude;
    private Double longitude;
    @JsonProperty(value="distance_km")
    private Double distanceKm;
    @JsonProperty(value="reliability_score")
    private Double reliabilityScore;
    @JsonProperty(value="response_rate")
    private Double responseRate;
    @JsonProperty(value="total_donations")
    private Integer totalDonations;
    @JsonProperty(value="is_available")
    private Boolean available;
    @JsonProperty(value="is_eligible")
    private Boolean eligible;
    @JsonProperty(value="last_donation_date")
    private LocalDate lastDonationDate;
    @JsonProperty(value="next_eligible_date")
    private LocalDate nextEligibleDate;
    @JsonProperty(value="ai_score")
    private Double aiScore;
    @JsonProperty(value="score_breakdown")
    private ScoreBreakdown scoreBreakdown;

    public double getAiScoreDisplay() {
        return this.aiScore != null ? (double)Math.round(this.aiScore * 100.0 * 10.0) / 10.0 : 0.0;
    }

    public boolean isTopMatch() {
        return this.rank != null && this.rank <= 3;
    }

    public Long getDonorId() {
        return this.donorId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Integer getRank() {
        return this.rank;
    }

    public String getName() {
        return this.name;
    }

    public String getPhoneMasked() {
        return this.phoneMasked;
    }

    public String getBloodGroup() {
        return this.bloodGroup;
    }

    public String getBloodGroupDisplay() {
        return this.bloodGroupDisplay;
    }

    public String getCity() {
        return this.city;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getDistanceKm() {
        return this.distanceKm;
    }

    public Double getReliabilityScore() {
        return this.reliabilityScore;
    }

    public Double getResponseRate() {
        return this.responseRate;
    }

    public Integer getTotalDonations() {
        return this.totalDonations;
    }

    public Boolean getAvailable() {
        return this.available;
    }

    public Boolean getEligible() {
        return this.eligible;
    }

    public LocalDate getLastDonationDate() {
        return this.lastDonationDate;
    }

    public LocalDate getNextEligibleDate() {
        return this.nextEligibleDate;
    }

    public Double getAiScore() {
        return this.aiScore;
    }

    public ScoreBreakdown getScoreBreakdown() {
        return this.scoreBreakdown;
    }

    @JsonProperty(value="donor_id")
    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    @JsonProperty(value="user_id")
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value="phone_masked")
    public void setPhoneMasked(String phoneMasked) {
        this.phoneMasked = phoneMasked;
    }

    @JsonProperty(value="blood_group")
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @JsonProperty(value="blood_group_display")
    public void setBloodGroupDisplay(String bloodGroupDisplay) {
        this.bloodGroupDisplay = bloodGroupDisplay;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty(value="distance_km")
    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    @JsonProperty(value="reliability_score")
    public void setReliabilityScore(Double reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    @JsonProperty(value="response_rate")
    public void setResponseRate(Double responseRate) {
        this.responseRate = responseRate;
    }

    @JsonProperty(value="total_donations")
    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    @JsonProperty(value="is_available")
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @JsonProperty(value="is_eligible")
    public void setEligible(Boolean eligible) {
        this.eligible = eligible;
    }

    @JsonProperty(value="last_donation_date")
    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    @JsonProperty(value="next_eligible_date")
    public void setNextEligibleDate(LocalDate nextEligibleDate) {
        this.nextEligibleDate = nextEligibleDate;
    }

    @JsonProperty(value="ai_score")
    public void setAiScore(Double aiScore) {
        this.aiScore = aiScore;
    }

    @JsonProperty(value="score_breakdown")
    public void setScoreBreakdown(ScoreBreakdown scoreBreakdown) {
        this.scoreBreakdown = scoreBreakdown;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AiDonorResult)) {
            return false;
        }
        AiDonorResult other = (AiDonorResult)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$donorId = this.getDonorId();
        Long other$donorId = other.getDonorId();
        if (this$donorId == null ? other$donorId != null : !((Object)this$donorId).equals(other$donorId)) {
            return false;
        }
        Long this$userId = this.getUserId();
        Long other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !((Object)this$userId).equals(other$userId)) {
            return false;
        }
        Integer this$rank = this.getRank();
        Integer other$rank = other.getRank();
        if (this$rank == null ? other$rank != null : !((Object)this$rank).equals(other$rank)) {
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
        Double this$distanceKm = this.getDistanceKm();
        Double other$distanceKm = other.getDistanceKm();
        if (this$distanceKm == null ? other$distanceKm != null : !((Object)this$distanceKm).equals(other$distanceKm)) {
            return false;
        }
        Double this$reliabilityScore = this.getReliabilityScore();
        Double other$reliabilityScore = other.getReliabilityScore();
        if (this$reliabilityScore == null ? other$reliabilityScore != null : !((Object)this$reliabilityScore).equals(other$reliabilityScore)) {
            return false;
        }
        Double this$responseRate = this.getResponseRate();
        Double other$responseRate = other.getResponseRate();
        if (this$responseRate == null ? other$responseRate != null : !((Object)this$responseRate).equals(other$responseRate)) {
            return false;
        }
        Integer this$totalDonations = this.getTotalDonations();
        Integer other$totalDonations = other.getTotalDonations();
        if (this$totalDonations == null ? other$totalDonations != null : !((Object)this$totalDonations).equals(other$totalDonations)) {
            return false;
        }
        Boolean this$available = this.getAvailable();
        Boolean other$available = other.getAvailable();
        if (this$available == null ? other$available != null : !((Object)this$available).equals(other$available)) {
            return false;
        }
        Boolean this$eligible = this.getEligible();
        Boolean other$eligible = other.getEligible();
        if (this$eligible == null ? other$eligible != null : !((Object)this$eligible).equals(other$eligible)) {
            return false;
        }
        Double this$aiScore = this.getAiScore();
        Double other$aiScore = other.getAiScore();
        if (this$aiScore == null ? other$aiScore != null : !((Object)this$aiScore).equals(other$aiScore)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$phoneMasked = this.getPhoneMasked();
        String other$phoneMasked = other.getPhoneMasked();
        if (this$phoneMasked == null ? other$phoneMasked != null : !this$phoneMasked.equals(other$phoneMasked)) {
            return false;
        }
        String this$bloodGroup = this.getBloodGroup();
        String other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !this$bloodGroup.equals(other$bloodGroup)) {
            return false;
        }
        String this$bloodGroupDisplay = this.getBloodGroupDisplay();
        String other$bloodGroupDisplay = other.getBloodGroupDisplay();
        if (this$bloodGroupDisplay == null ? other$bloodGroupDisplay != null : !this$bloodGroupDisplay.equals(other$bloodGroupDisplay)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        LocalDate this$lastDonationDate = this.getLastDonationDate();
        LocalDate other$lastDonationDate = other.getLastDonationDate();
        if (this$lastDonationDate == null ? other$lastDonationDate != null : !((Object)this$lastDonationDate).equals(other$lastDonationDate)) {
            return false;
        }
        LocalDate this$nextEligibleDate = this.getNextEligibleDate();
        LocalDate other$nextEligibleDate = other.getNextEligibleDate();
        if (this$nextEligibleDate == null ? other$nextEligibleDate != null : !((Object)this$nextEligibleDate).equals(other$nextEligibleDate)) {
            return false;
        }
        ScoreBreakdown this$scoreBreakdown = this.getScoreBreakdown();
        ScoreBreakdown other$scoreBreakdown = other.getScoreBreakdown();
        return !(this$scoreBreakdown == null ? other$scoreBreakdown != null : !((Object)this$scoreBreakdown).equals(other$scoreBreakdown));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AiDonorResult;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $donorId = this.getDonorId();
        result = result * 59 + ($donorId == null ? 43 : ((Object)$donorId).hashCode());
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        Integer $rank = this.getRank();
        result = result * 59 + ($rank == null ? 43 : ((Object)$rank).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $distanceKm = this.getDistanceKm();
        result = result * 59 + ($distanceKm == null ? 43 : ((Object)$distanceKm).hashCode());
        Double $reliabilityScore = this.getReliabilityScore();
        result = result * 59 + ($reliabilityScore == null ? 43 : ((Object)$reliabilityScore).hashCode());
        Double $responseRate = this.getResponseRate();
        result = result * 59 + ($responseRate == null ? 43 : ((Object)$responseRate).hashCode());
        Integer $totalDonations = this.getTotalDonations();
        result = result * 59 + ($totalDonations == null ? 43 : ((Object)$totalDonations).hashCode());
        Boolean $available = this.getAvailable();
        result = result * 59 + ($available == null ? 43 : ((Object)$available).hashCode());
        Boolean $eligible = this.getEligible();
        result = result * 59 + ($eligible == null ? 43 : ((Object)$eligible).hashCode());
        Double $aiScore = this.getAiScore();
        result = result * 59 + ($aiScore == null ? 43 : ((Object)$aiScore).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $phoneMasked = this.getPhoneMasked();
        result = result * 59 + ($phoneMasked == null ? 43 : $phoneMasked.hashCode());
        String $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : $bloodGroup.hashCode());
        String $bloodGroupDisplay = this.getBloodGroupDisplay();
        result = result * 59 + ($bloodGroupDisplay == null ? 43 : $bloodGroupDisplay.hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        LocalDate $lastDonationDate = this.getLastDonationDate();
        result = result * 59 + ($lastDonationDate == null ? 43 : ((Object)$lastDonationDate).hashCode());
        LocalDate $nextEligibleDate = this.getNextEligibleDate();
        result = result * 59 + ($nextEligibleDate == null ? 43 : ((Object)$nextEligibleDate).hashCode());
        ScoreBreakdown $scoreBreakdown = this.getScoreBreakdown();
        result = result * 59 + ($scoreBreakdown == null ? 43 : ((Object)$scoreBreakdown).hashCode());
        return result;
    }

    public String toString() {
        return "AiDonorResult(donorId=" + this.getDonorId() + ", userId=" + this.getUserId() + ", rank=" + this.getRank() + ", name=" + this.getName() + ", phoneMasked=" + this.getPhoneMasked() + ", bloodGroup=" + this.getBloodGroup() + ", bloodGroupDisplay=" + this.getBloodGroupDisplay() + ", city=" + this.getCity() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ", distanceKm=" + this.getDistanceKm() + ", reliabilityScore=" + this.getReliabilityScore() + ", responseRate=" + this.getResponseRate() + ", totalDonations=" + this.getTotalDonations() + ", available=" + this.getAvailable() + ", eligible=" + this.getEligible() + ", lastDonationDate=" + this.getLastDonationDate() + ", nextEligibleDate=" + this.getNextEligibleDate() + ", aiScore=" + this.getAiScore() + ", scoreBreakdown=" + this.getScoreBreakdown() + ")";
    }
}

