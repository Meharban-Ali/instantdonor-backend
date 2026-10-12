package com.blood_donor.model.dto.response.donor_response;

import com.blood_donor.model.dto.response.donor_response.BloodInfo;
import com.blood_donor.model.dto.response.donor_response.DonationStats;
import com.blood_donor.model.dto.response.donor_response.LocationInfo;
import com.blood_donor.model.dto.response.donor_response.UserSummary;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class DonorResponse {
    private Long donorId;
    private UserSummary user;
    private BloodInfo blood;
    private LocationInfo location;
    private DonationStats donationStats;
    private boolean isAvailable;
    @JsonInclude(value=JsonInclude.Include.NON_NULL)
    private Double distanceKm;
    private LocalDateTime createdAt;

    public static DonorResponseBuilder builder() {
        return new DonorResponseBuilder();
    }

    public Long getDonorId() {
        return this.donorId;
    }

    public UserSummary getUser() {
        return this.user;
    }

    public BloodInfo getBlood() {
        return this.blood;
    }

    public LocationInfo getLocation() {
        return this.location;
    }

    public DonationStats getDonationStats() {
        return this.donationStats;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public Double getDistanceKm() {
        return this.distanceKm;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public void setUser(UserSummary user) {
        this.user = user;
    }

    public void setBlood(BloodInfo blood) {
        this.blood = blood;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }

    public void setDonationStats(DonationStats donationStats) {
        this.donationStats = donationStats;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DonorResponse)) {
            return false;
        }
        DonorResponse other = (DonorResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isAvailable() != other.isAvailable()) {
            return false;
        }
        Long this$donorId = this.getDonorId();
        Long other$donorId = other.getDonorId();
        if (this$donorId == null ? other$donorId != null : !((Object)this$donorId).equals(other$donorId)) {
            return false;
        }
        Double this$distanceKm = this.getDistanceKm();
        Double other$distanceKm = other.getDistanceKm();
        if (this$distanceKm == null ? other$distanceKm != null : !((Object)this$distanceKm).equals(other$distanceKm)) {
            return false;
        }
        UserSummary this$user = this.getUser();
        UserSummary other$user = other.getUser();
        if (this$user == null ? other$user != null : !((Object)this$user).equals(other$user)) {
            return false;
        }
        BloodInfo this$blood = this.getBlood();
        BloodInfo other$blood = other.getBlood();
        if (this$blood == null ? other$blood != null : !((Object)this$blood).equals(other$blood)) {
            return false;
        }
        LocationInfo this$location = this.getLocation();
        LocationInfo other$location = other.getLocation();
        if (this$location == null ? other$location != null : !((Object)this$location).equals(other$location)) {
            return false;
        }
        DonationStats this$donationStats = this.getDonationStats();
        DonationStats other$donationStats = other.getDonationStats();
        if (this$donationStats == null ? other$donationStats != null : !((Object)this$donationStats).equals(other$donationStats)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DonorResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isAvailable() ? 79 : 97);
        Long $donorId = this.getDonorId();
        result = result * 59 + ($donorId == null ? 43 : ((Object)$donorId).hashCode());
        Double $distanceKm = this.getDistanceKm();
        result = result * 59 + ($distanceKm == null ? 43 : ((Object)$distanceKm).hashCode());
        UserSummary $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : ((Object)$user).hashCode());
        BloodInfo $blood = this.getBlood();
        result = result * 59 + ($blood == null ? 43 : ((Object)$blood).hashCode());
        LocationInfo $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : ((Object)$location).hashCode());
        DonationStats $donationStats = this.getDonationStats();
        result = result * 59 + ($donationStats == null ? 43 : ((Object)$donationStats).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "DonorResponse(donorId=" + this.getDonorId() + ", user=" + this.getUser() + ", blood=" + this.getBlood() + ", location=" + this.getLocation() + ", donationStats=" + this.getDonationStats() + ", isAvailable=" + this.isAvailable() + ", distanceKm=" + this.getDistanceKm() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    public DonorResponse() {
    }

    public DonorResponse(Long donorId, UserSummary user, BloodInfo blood, LocationInfo location, DonationStats donationStats, boolean isAvailable, Double distanceKm, LocalDateTime createdAt) {
        this.donorId = donorId;
        this.user = user;
        this.blood = blood;
        this.location = location;
        this.donationStats = donationStats;
        this.isAvailable = isAvailable;
        this.distanceKm = distanceKm;
        this.createdAt = createdAt;
    }

    public static class DonorResponseBuilder {
        private Long donorId;
        private UserSummary user;
        private BloodInfo blood;
        private LocationInfo location;
        private DonationStats donationStats;
        private boolean isAvailable;
        private Double distanceKm;
        private LocalDateTime createdAt;

        DonorResponseBuilder() {
        }

        public DonorResponseBuilder donorId(Long donorId) {
            this.donorId = donorId;
            return this;
        }

        public DonorResponseBuilder user(UserSummary user) {
            this.user = user;
            return this;
        }

        public DonorResponseBuilder blood(BloodInfo blood) {
            this.blood = blood;
            return this;
        }

        public DonorResponseBuilder location(LocationInfo location) {
            this.location = location;
            return this;
        }

        public DonorResponseBuilder donationStats(DonationStats donationStats) {
            this.donationStats = donationStats;
            return this;
        }

        public DonorResponseBuilder isAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public DonorResponseBuilder distanceKm(Double distanceKm) {
            this.distanceKm = distanceKm;
            return this;
        }

        public DonorResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DonorResponse build() {
            return new DonorResponse(this.donorId, this.user, this.blood, this.location, this.donationStats, this.isAvailable, this.distanceKm, this.createdAt);
        }

        public String toString() {
            return "DonorResponse.DonorResponseBuilder(donorId=" + this.donorId + ", user=" + this.user + ", blood=" + this.blood + ", location=" + this.location + ", donationStats=" + this.donationStats + ", isAvailable=" + this.isAvailable + ", distanceKm=" + this.distanceKm + ", createdAt=" + this.createdAt + ")";
        }
    }
}

