/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 */
package com.blood_donor.admin.dto;

import com.blood_donor.common.enums.BloodGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class DonorSummaryDto {
    private Long donorId;
    private Long userId;
    private String name;
    private String email;
    private String phoneMasked;
    private BloodGroup bloodGroup;
    private String bloodGroupDisplay;
    private String city;
    private String state;
    private Boolean isAvailable;
    private LocalDate lastDonationDate;
    private Integer totalDonations;
    private BigDecimal reliabilityScore;
    private BigDecimal responseRate;
    private Integer totalNotified;
    private Integer totalAccepted;
    private Integer totalRejected;
    private Boolean hasLocation;
    private LocalDateTime registeredAt;
    private LocalDateTime lastUpdatedAt;

    public Boolean getIsEligible() {
        if (!Boolean.TRUE.equals(this.isAvailable)) {
            return false;
        }
        if (this.lastDonationDate == null) {
            return true;
        }
        return ChronoUnit.DAYS.between(this.lastDonationDate, LocalDate.now()) >= 90L;
    }

    public LocalDate getNextEligibleDate() {
        if (this.lastDonationDate == null) {
            return null;
        }
        LocalDate nextDate = this.lastDonationDate.plusDays(90L);
        return nextDate.isAfter(LocalDate.now()) ? nextDate : null;
    }

    public Long getDaysUntilEligible() {
        LocalDate next = this.getNextEligibleDate();
        if (next == null) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), next);
    }

    public String getReliabilityLevel() {
        if (this.reliabilityScore == null) {
            return "UNKNOWN";
        }
        if (this.reliabilityScore.compareTo(BigDecimal.valueOf(80L)) >= 0) {
            return "HIGH";
        }
        if (this.reliabilityScore.compareTo(BigDecimal.valueOf(50L)) >= 0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    public String getResponseLevel() {
        if (this.responseRate == null) {
            return "UNKNOWN";
        }
        if (this.responseRate.compareTo(BigDecimal.valueOf(70L)) >= 0) {
            return "GOOD";
        }
        if (this.responseRate.compareTo(BigDecimal.valueOf(40L)) >= 0) {
            return "AVERAGE";
        }
        return "POOR";
    }

    public String getStatusSummary() {
        if (!Boolean.TRUE.equals(this.isAvailable)) {
            return "Not Available";
        }
        if (Boolean.TRUE.equals(this.getIsEligible())) {
            return "Available + Eligible";
        }
        Long days = this.getDaysUntilEligible();
        return "Available — Eligible in " + days + " day" + (days != null && days == 1L ? "" : "s");
    }

    public static DonorSummaryDtoBuilder builder() {
        return new DonorSummaryDtoBuilder();
    }

    public Long getDonorId() {
        return this.donorId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneMasked() {
        return this.phoneMasked;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getBloodGroupDisplay() {
        return this.bloodGroupDisplay;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public Boolean getIsAvailable() {
        return this.isAvailable;
    }

    public LocalDate getLastDonationDate() {
        return this.lastDonationDate;
    }

    public Integer getTotalDonations() {
        return this.totalDonations;
    }

    public BigDecimal getReliabilityScore() {
        return this.reliabilityScore;
    }

    public BigDecimal getResponseRate() {
        return this.responseRate;
    }

    public Integer getTotalNotified() {
        return this.totalNotified;
    }

    public Integer getTotalAccepted() {
        return this.totalAccepted;
    }

    public Integer getTotalRejected() {
        return this.totalRejected;
    }

    public Boolean getHasLocation() {
        return this.hasLocation;
    }

    public LocalDateTime getRegisteredAt() {
        return this.registeredAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneMasked(String phoneMasked) {
        this.phoneMasked = phoneMasked;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setBloodGroupDisplay(String bloodGroupDisplay) {
        this.bloodGroupDisplay = bloodGroupDisplay;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public void setResponseRate(BigDecimal responseRate) {
        this.responseRate = responseRate;
    }

    public void setTotalNotified(Integer totalNotified) {
        this.totalNotified = totalNotified;
    }

    public void setTotalAccepted(Integer totalAccepted) {
        this.totalAccepted = totalAccepted;
    }

    public void setTotalRejected(Integer totalRejected) {
        this.totalRejected = totalRejected;
    }

    public void setHasLocation(Boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DonorSummaryDto)) {
            return false;
        }
        DonorSummaryDto other = (DonorSummaryDto)o;
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
        Boolean this$isAvailable = this.getIsAvailable();
        Boolean other$isAvailable = other.getIsAvailable();
        if (this$isAvailable == null ? other$isAvailable != null : !((Object)this$isAvailable).equals(other$isAvailable)) {
            return false;
        }
        Integer this$totalDonations = this.getTotalDonations();
        Integer other$totalDonations = other.getTotalDonations();
        if (this$totalDonations == null ? other$totalDonations != null : !((Object)this$totalDonations).equals(other$totalDonations)) {
            return false;
        }
        Integer this$totalNotified = this.getTotalNotified();
        Integer other$totalNotified = other.getTotalNotified();
        if (this$totalNotified == null ? other$totalNotified != null : !((Object)this$totalNotified).equals(other$totalNotified)) {
            return false;
        }
        Integer this$totalAccepted = this.getTotalAccepted();
        Integer other$totalAccepted = other.getTotalAccepted();
        if (this$totalAccepted == null ? other$totalAccepted != null : !((Object)this$totalAccepted).equals(other$totalAccepted)) {
            return false;
        }
        Integer this$totalRejected = this.getTotalRejected();
        Integer other$totalRejected = other.getTotalRejected();
        if (this$totalRejected == null ? other$totalRejected != null : !((Object)this$totalRejected).equals(other$totalRejected)) {
            return false;
        }
        Boolean this$hasLocation = this.getHasLocation();
        Boolean other$hasLocation = other.getHasLocation();
        if (this$hasLocation == null ? other$hasLocation != null : !((Object)this$hasLocation).equals(other$hasLocation)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$phoneMasked = this.getPhoneMasked();
        String other$phoneMasked = other.getPhoneMasked();
        if (this$phoneMasked == null ? other$phoneMasked != null : !this$phoneMasked.equals(other$phoneMasked)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
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
        String this$state = this.getState();
        String other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        LocalDate this$lastDonationDate = this.getLastDonationDate();
        LocalDate other$lastDonationDate = other.getLastDonationDate();
        if (this$lastDonationDate == null ? other$lastDonationDate != null : !((Object)this$lastDonationDate).equals(other$lastDonationDate)) {
            return false;
        }
        BigDecimal this$reliabilityScore = this.getReliabilityScore();
        BigDecimal other$reliabilityScore = other.getReliabilityScore();
        if (this$reliabilityScore == null ? other$reliabilityScore != null : !((Object)this$reliabilityScore).equals(other$reliabilityScore)) {
            return false;
        }
        BigDecimal this$responseRate = this.getResponseRate();
        BigDecimal other$responseRate = other.getResponseRate();
        if (this$responseRate == null ? other$responseRate != null : !((Object)this$responseRate).equals(other$responseRate)) {
            return false;
        }
        LocalDateTime this$registeredAt = this.getRegisteredAt();
        LocalDateTime other$registeredAt = other.getRegisteredAt();
        if (this$registeredAt == null ? other$registeredAt != null : !((Object)this$registeredAt).equals(other$registeredAt)) {
            return false;
        }
        LocalDateTime this$lastUpdatedAt = this.getLastUpdatedAt();
        LocalDateTime other$lastUpdatedAt = other.getLastUpdatedAt();
        return !(this$lastUpdatedAt == null ? other$lastUpdatedAt != null : !((Object)this$lastUpdatedAt).equals(other$lastUpdatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DonorSummaryDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $donorId = this.getDonorId();
        result = result * 59 + ($donorId == null ? 43 : ((Object)$donorId).hashCode());
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        Boolean $isAvailable = this.getIsAvailable();
        result = result * 59 + ($isAvailable == null ? 43 : ((Object)$isAvailable).hashCode());
        Integer $totalDonations = this.getTotalDonations();
        result = result * 59 + ($totalDonations == null ? 43 : ((Object)$totalDonations).hashCode());
        Integer $totalNotified = this.getTotalNotified();
        result = result * 59 + ($totalNotified == null ? 43 : ((Object)$totalNotified).hashCode());
        Integer $totalAccepted = this.getTotalAccepted();
        result = result * 59 + ($totalAccepted == null ? 43 : ((Object)$totalAccepted).hashCode());
        Integer $totalRejected = this.getTotalRejected();
        result = result * 59 + ($totalRejected == null ? 43 : ((Object)$totalRejected).hashCode());
        Boolean $hasLocation = this.getHasLocation();
        result = result * 59 + ($hasLocation == null ? 43 : ((Object)$hasLocation).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $phoneMasked = this.getPhoneMasked();
        result = result * 59 + ($phoneMasked == null ? 43 : $phoneMasked.hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $bloodGroupDisplay = this.getBloodGroupDisplay();
        result = result * 59 + ($bloodGroupDisplay == null ? 43 : $bloodGroupDisplay.hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        String $state = this.getState();
        result = result * 59 + ($state == null ? 43 : $state.hashCode());
        LocalDate $lastDonationDate = this.getLastDonationDate();
        result = result * 59 + ($lastDonationDate == null ? 43 : ((Object)$lastDonationDate).hashCode());
        BigDecimal $reliabilityScore = this.getReliabilityScore();
        result = result * 59 + ($reliabilityScore == null ? 43 : ((Object)$reliabilityScore).hashCode());
        BigDecimal $responseRate = this.getResponseRate();
        result = result * 59 + ($responseRate == null ? 43 : ((Object)$responseRate).hashCode());
        LocalDateTime $registeredAt = this.getRegisteredAt();
        result = result * 59 + ($registeredAt == null ? 43 : ((Object)$registeredAt).hashCode());
        LocalDateTime $lastUpdatedAt = this.getLastUpdatedAt();
        result = result * 59 + ($lastUpdatedAt == null ? 43 : ((Object)$lastUpdatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "DonorSummaryDto(donorId=" + this.getDonorId() + ", userId=" + this.getUserId() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", phoneMasked=" + this.getPhoneMasked() + ", bloodGroup=" + this.getBloodGroup() + ", bloodGroupDisplay=" + this.getBloodGroupDisplay() + ", city=" + this.getCity() + ", state=" + this.getState() + ", isAvailable=" + this.getIsAvailable() + ", lastDonationDate=" + this.getLastDonationDate() + ", totalDonations=" + this.getTotalDonations() + ", reliabilityScore=" + this.getReliabilityScore() + ", responseRate=" + this.getResponseRate() + ", totalNotified=" + this.getTotalNotified() + ", totalAccepted=" + this.getTotalAccepted() + ", totalRejected=" + this.getTotalRejected() + ", hasLocation=" + this.getHasLocation() + ", registeredAt=" + this.getRegisteredAt() + ", lastUpdatedAt=" + this.getLastUpdatedAt() + ")";
    }

    public DonorSummaryDto() {
    }

    public DonorSummaryDto(Long donorId, Long userId, String name, String email, String phoneMasked, BloodGroup bloodGroup, String bloodGroupDisplay, String city, String state, Boolean isAvailable, LocalDate lastDonationDate, Integer totalDonations, BigDecimal reliabilityScore, BigDecimal responseRate, Integer totalNotified, Integer totalAccepted, Integer totalRejected, Boolean hasLocation, LocalDateTime registeredAt, LocalDateTime lastUpdatedAt) {
        this.donorId = donorId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneMasked = phoneMasked;
        this.bloodGroup = bloodGroup;
        this.bloodGroupDisplay = bloodGroupDisplay;
        this.city = city;
        this.state = state;
        this.isAvailable = isAvailable;
        this.lastDonationDate = lastDonationDate;
        this.totalDonations = totalDonations;
        this.reliabilityScore = reliabilityScore;
        this.responseRate = responseRate;
        this.totalNotified = totalNotified;
        this.totalAccepted = totalAccepted;
        this.totalRejected = totalRejected;
        this.hasLocation = hasLocation;
        this.registeredAt = registeredAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public static class DonorSummaryDtoBuilder {
        private Long donorId;
        private Long userId;
        private String name;
        private String email;
        private String phoneMasked;
        private BloodGroup bloodGroup;
        private String bloodGroupDisplay;
        private String city;
        private String state;
        private Boolean isAvailable;
        private LocalDate lastDonationDate;
        private Integer totalDonations;
        private BigDecimal reliabilityScore;
        private BigDecimal responseRate;
        private Integer totalNotified;
        private Integer totalAccepted;
        private Integer totalRejected;
        private Boolean hasLocation;
        private LocalDateTime registeredAt;
        private LocalDateTime lastUpdatedAt;

        DonorSummaryDtoBuilder() {
        }

        public DonorSummaryDtoBuilder donorId(Long donorId) {
            this.donorId = donorId;
            return this;
        }

        public DonorSummaryDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public DonorSummaryDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DonorSummaryDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public DonorSummaryDtoBuilder phoneMasked(String phoneMasked) {
            this.phoneMasked = phoneMasked;
            return this;
        }

        public DonorSummaryDtoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public DonorSummaryDtoBuilder bloodGroupDisplay(String bloodGroupDisplay) {
            this.bloodGroupDisplay = bloodGroupDisplay;
            return this;
        }

        public DonorSummaryDtoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public DonorSummaryDtoBuilder state(String state) {
            this.state = state;
            return this;
        }

        public DonorSummaryDtoBuilder isAvailable(Boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public DonorSummaryDtoBuilder lastDonationDate(LocalDate lastDonationDate) {
            this.lastDonationDate = lastDonationDate;
            return this;
        }

        public DonorSummaryDtoBuilder totalDonations(Integer totalDonations) {
            this.totalDonations = totalDonations;
            return this;
        }

        public DonorSummaryDtoBuilder reliabilityScore(BigDecimal reliabilityScore) {
            this.reliabilityScore = reliabilityScore;
            return this;
        }

        public DonorSummaryDtoBuilder responseRate(BigDecimal responseRate) {
            this.responseRate = responseRate;
            return this;
        }

        public DonorSummaryDtoBuilder totalNotified(Integer totalNotified) {
            this.totalNotified = totalNotified;
            return this;
        }

        public DonorSummaryDtoBuilder totalAccepted(Integer totalAccepted) {
            this.totalAccepted = totalAccepted;
            return this;
        }

        public DonorSummaryDtoBuilder totalRejected(Integer totalRejected) {
            this.totalRejected = totalRejected;
            return this;
        }

        public DonorSummaryDtoBuilder hasLocation(Boolean hasLocation) {
            this.hasLocation = hasLocation;
            return this;
        }

        public DonorSummaryDtoBuilder registeredAt(LocalDateTime registeredAt) {
            this.registeredAt = registeredAt;
            return this;
        }

        public DonorSummaryDtoBuilder lastUpdatedAt(LocalDateTime lastUpdatedAt) {
            this.lastUpdatedAt = lastUpdatedAt;
            return this;
        }

        public DonorSummaryDto build() {
            return new DonorSummaryDto(this.donorId, this.userId, this.name, this.email, this.phoneMasked, this.bloodGroup, this.bloodGroupDisplay, this.city, this.state, this.isAvailable, this.lastDonationDate, this.totalDonations, this.reliabilityScore, this.responseRate, this.totalNotified, this.totalAccepted, this.totalRejected, this.hasLocation, this.registeredAt, this.lastUpdatedAt);
        }

        public String toString() {
            return "DonorSummaryDto.DonorSummaryDtoBuilder(donorId=" + this.donorId + ", userId=" + this.userId + ", name=" + this.name + ", email=" + this.email + ", phoneMasked=" + this.phoneMasked + ", bloodGroup=" + this.bloodGroup + ", bloodGroupDisplay=" + this.bloodGroupDisplay + ", city=" + this.city + ", state=" + this.state + ", isAvailable=" + this.isAvailable + ", lastDonationDate=" + this.lastDonationDate + ", totalDonations=" + this.totalDonations + ", reliabilityScore=" + this.reliabilityScore + ", responseRate=" + this.responseRate + ", totalNotified=" + this.totalNotified + ", totalAccepted=" + this.totalAccepted + ", totalRejected=" + this.totalRejected + ", hasLocation=" + this.hasLocation + ", registeredAt=" + this.registeredAt + ", lastUpdatedAt=" + this.lastUpdatedAt + ")";
        }
    }
}

