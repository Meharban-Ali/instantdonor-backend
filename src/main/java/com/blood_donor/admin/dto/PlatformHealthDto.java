package com.blood_donor.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PlatformHealthDto {
    private Double fulfillmentRate;
    private Double avgResponseTimeHours;
    private boolean aiServiceOnline;
    private Double avgAiMatchScore;
    private long aiMatchesThisWeek;
    private Double emailDeliveryRate;
    private Double smsDeliveryRate;
    private boolean dbHealthy;
    private boolean redisHealthy;

    public boolean isFullyOperational() {
        return this.dbHealthy && this.aiServiceOnline;
    }

    public int getHealthScore() {
        int score = 0;
        if (this.dbHealthy) {
            score += 40;
        }
        if (this.aiServiceOnline) {
            score += 30;
        }
        if (this.redisHealthy) {
            score += 20;
        }
        if (this.fulfillmentRate != null && this.fulfillmentRate >= 70.0) {
            score += 10;
        }
        return score;
    }

    public static PlatformHealthDtoBuilder builder() {
        return new PlatformHealthDtoBuilder();
    }

    public Double getFulfillmentRate() {
        return this.fulfillmentRate;
    }

    public Double getAvgResponseTimeHours() {
        return this.avgResponseTimeHours;
    }

    public boolean isAiServiceOnline() {
        return this.aiServiceOnline;
    }

    public Double getAvgAiMatchScore() {
        return this.avgAiMatchScore;
    }

    public long getAiMatchesThisWeek() {
        return this.aiMatchesThisWeek;
    }

    public Double getEmailDeliveryRate() {
        return this.emailDeliveryRate;
    }

    public Double getSmsDeliveryRate() {
        return this.smsDeliveryRate;
    }

    public boolean isDbHealthy() {
        return this.dbHealthy;
    }

    public boolean isRedisHealthy() {
        return this.redisHealthy;
    }

    public void setFulfillmentRate(Double fulfillmentRate) {
        this.fulfillmentRate = fulfillmentRate;
    }

    public void setAvgResponseTimeHours(Double avgResponseTimeHours) {
        this.avgResponseTimeHours = avgResponseTimeHours;
    }

    public void setAiServiceOnline(boolean aiServiceOnline) {
        this.aiServiceOnline = aiServiceOnline;
    }

    public void setAvgAiMatchScore(Double avgAiMatchScore) {
        this.avgAiMatchScore = avgAiMatchScore;
    }

    public void setAiMatchesThisWeek(long aiMatchesThisWeek) {
        this.aiMatchesThisWeek = aiMatchesThisWeek;
    }

    public void setEmailDeliveryRate(Double emailDeliveryRate) {
        this.emailDeliveryRate = emailDeliveryRate;
    }

    public void setSmsDeliveryRate(Double smsDeliveryRate) {
        this.smsDeliveryRate = smsDeliveryRate;
    }

    public void setDbHealthy(boolean dbHealthy) {
        this.dbHealthy = dbHealthy;
    }

    public void setRedisHealthy(boolean redisHealthy) {
        this.redisHealthy = redisHealthy;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlatformHealthDto)) {
            return false;
        }
        PlatformHealthDto other = (PlatformHealthDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isAiServiceOnline() != other.isAiServiceOnline()) {
            return false;
        }
        if (this.getAiMatchesThisWeek() != other.getAiMatchesThisWeek()) {
            return false;
        }
        if (this.isDbHealthy() != other.isDbHealthy()) {
            return false;
        }
        if (this.isRedisHealthy() != other.isRedisHealthy()) {
            return false;
        }
        Double this$fulfillmentRate = this.getFulfillmentRate();
        Double other$fulfillmentRate = other.getFulfillmentRate();
        if (this$fulfillmentRate == null ? other$fulfillmentRate != null : !((Object)this$fulfillmentRate).equals(other$fulfillmentRate)) {
            return false;
        }
        Double this$avgResponseTimeHours = this.getAvgResponseTimeHours();
        Double other$avgResponseTimeHours = other.getAvgResponseTimeHours();
        if (this$avgResponseTimeHours == null ? other$avgResponseTimeHours != null : !((Object)this$avgResponseTimeHours).equals(other$avgResponseTimeHours)) {
            return false;
        }
        Double this$avgAiMatchScore = this.getAvgAiMatchScore();
        Double other$avgAiMatchScore = other.getAvgAiMatchScore();
        if (this$avgAiMatchScore == null ? other$avgAiMatchScore != null : !((Object)this$avgAiMatchScore).equals(other$avgAiMatchScore)) {
            return false;
        }
        Double this$emailDeliveryRate = this.getEmailDeliveryRate();
        Double other$emailDeliveryRate = other.getEmailDeliveryRate();
        if (this$emailDeliveryRate == null ? other$emailDeliveryRate != null : !((Object)this$emailDeliveryRate).equals(other$emailDeliveryRate)) {
            return false;
        }
        Double this$smsDeliveryRate = this.getSmsDeliveryRate();
        Double other$smsDeliveryRate = other.getSmsDeliveryRate();
        return !(this$smsDeliveryRate == null ? other$smsDeliveryRate != null : !((Object)this$smsDeliveryRate).equals(other$smsDeliveryRate));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlatformHealthDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isAiServiceOnline() ? 79 : 97);
        long $aiMatchesThisWeek = this.getAiMatchesThisWeek();
        result = result * 59 + (int)($aiMatchesThisWeek >>> 32 ^ $aiMatchesThisWeek);
        result = result * 59 + (this.isDbHealthy() ? 79 : 97);
        result = result * 59 + (this.isRedisHealthy() ? 79 : 97);
        Double $fulfillmentRate = this.getFulfillmentRate();
        result = result * 59 + ($fulfillmentRate == null ? 43 : ((Object)$fulfillmentRate).hashCode());
        Double $avgResponseTimeHours = this.getAvgResponseTimeHours();
        result = result * 59 + ($avgResponseTimeHours == null ? 43 : ((Object)$avgResponseTimeHours).hashCode());
        Double $avgAiMatchScore = this.getAvgAiMatchScore();
        result = result * 59 + ($avgAiMatchScore == null ? 43 : ((Object)$avgAiMatchScore).hashCode());
        Double $emailDeliveryRate = this.getEmailDeliveryRate();
        result = result * 59 + ($emailDeliveryRate == null ? 43 : ((Object)$emailDeliveryRate).hashCode());
        Double $smsDeliveryRate = this.getSmsDeliveryRate();
        result = result * 59 + ($smsDeliveryRate == null ? 43 : ((Object)$smsDeliveryRate).hashCode());
        return result;
    }

    public String toString() {
        return "PlatformHealthDto(fulfillmentRate=" + this.getFulfillmentRate() + ", avgResponseTimeHours=" + this.getAvgResponseTimeHours() + ", aiServiceOnline=" + this.isAiServiceOnline() + ", avgAiMatchScore=" + this.getAvgAiMatchScore() + ", aiMatchesThisWeek=" + this.getAiMatchesThisWeek() + ", emailDeliveryRate=" + this.getEmailDeliveryRate() + ", smsDeliveryRate=" + this.getSmsDeliveryRate() + ", dbHealthy=" + this.isDbHealthy() + ", redisHealthy=" + this.isRedisHealthy() + ")";
    }

    public PlatformHealthDto() {
    }

    public PlatformHealthDto(Double fulfillmentRate, Double avgResponseTimeHours, boolean aiServiceOnline, Double avgAiMatchScore, long aiMatchesThisWeek, Double emailDeliveryRate, Double smsDeliveryRate, boolean dbHealthy, boolean redisHealthy) {
        this.fulfillmentRate = fulfillmentRate;
        this.avgResponseTimeHours = avgResponseTimeHours;
        this.aiServiceOnline = aiServiceOnline;
        this.avgAiMatchScore = avgAiMatchScore;
        this.aiMatchesThisWeek = aiMatchesThisWeek;
        this.emailDeliveryRate = emailDeliveryRate;
        this.smsDeliveryRate = smsDeliveryRate;
        this.dbHealthy = dbHealthy;
        this.redisHealthy = redisHealthy;
    }

    public static class PlatformHealthDtoBuilder {
        private Double fulfillmentRate;
        private Double avgResponseTimeHours;
        private boolean aiServiceOnline;
        private Double avgAiMatchScore;
        private long aiMatchesThisWeek;
        private Double emailDeliveryRate;
        private Double smsDeliveryRate;
        private boolean dbHealthy;
        private boolean redisHealthy;

        PlatformHealthDtoBuilder() {
        }

        public PlatformHealthDtoBuilder fulfillmentRate(Double fulfillmentRate) {
            this.fulfillmentRate = fulfillmentRate;
            return this;
        }

        public PlatformHealthDtoBuilder avgResponseTimeHours(Double avgResponseTimeHours) {
            this.avgResponseTimeHours = avgResponseTimeHours;
            return this;
        }

        public PlatformHealthDtoBuilder aiServiceOnline(boolean aiServiceOnline) {
            this.aiServiceOnline = aiServiceOnline;
            return this;
        }

        public PlatformHealthDtoBuilder avgAiMatchScore(Double avgAiMatchScore) {
            this.avgAiMatchScore = avgAiMatchScore;
            return this;
        }

        public PlatformHealthDtoBuilder aiMatchesThisWeek(long aiMatchesThisWeek) {
            this.aiMatchesThisWeek = aiMatchesThisWeek;
            return this;
        }

        public PlatformHealthDtoBuilder emailDeliveryRate(Double emailDeliveryRate) {
            this.emailDeliveryRate = emailDeliveryRate;
            return this;
        }

        public PlatformHealthDtoBuilder smsDeliveryRate(Double smsDeliveryRate) {
            this.smsDeliveryRate = smsDeliveryRate;
            return this;
        }

        public PlatformHealthDtoBuilder dbHealthy(boolean dbHealthy) {
            this.dbHealthy = dbHealthy;
            return this;
        }

        public PlatformHealthDtoBuilder redisHealthy(boolean redisHealthy) {
            this.redisHealthy = redisHealthy;
            return this;
        }

        public PlatformHealthDto build() {
            return new PlatformHealthDto(this.fulfillmentRate, this.avgResponseTimeHours, this.aiServiceOnline, this.avgAiMatchScore, this.aiMatchesThisWeek, this.emailDeliveryRate, this.smsDeliveryRate, this.dbHealthy, this.redisHealthy);
        }

        public String toString() {
            return "PlatformHealthDto.PlatformHealthDtoBuilder(fulfillmentRate=" + this.fulfillmentRate + ", avgResponseTimeHours=" + this.avgResponseTimeHours + ", aiServiceOnline=" + this.aiServiceOnline + ", avgAiMatchScore=" + this.avgAiMatchScore + ", aiMatchesThisWeek=" + this.aiMatchesThisWeek + ", emailDeliveryRate=" + this.emailDeliveryRate + ", smsDeliveryRate=" + this.smsDeliveryRate + ", dbHealthy=" + this.dbHealthy + ", redisHealthy=" + this.redisHealthy + ")";
        }
    }
}

