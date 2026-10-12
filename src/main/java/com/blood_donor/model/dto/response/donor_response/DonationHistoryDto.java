package com.blood_donor.model.dto.response.donor_response;

import com.blood_donor.common.enums.BloodGroup;
import java.time.LocalDateTime;

public class DonationHistoryDto {
    private Long id;
    private String hospitalName;
    private LocalDateTime donationDate;
    private BloodGroup bloodGroup;
    private Integer unitsDonated;
    private boolean verified;

    public static DonationHistoryDtoBuilder builder() {
        return new DonationHistoryDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getHospitalName() {
        return this.hospitalName;
    }

    public LocalDateTime getDonationDate() {
        return this.donationDate;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public Integer getUnitsDonated() {
        return this.unitsDonated;
    }

    public boolean isVerified() {
        return this.verified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setDonationDate(LocalDateTime donationDate) {
        this.donationDate = donationDate;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setUnitsDonated(Integer unitsDonated) {
        this.unitsDonated = unitsDonated;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public DonationHistoryDto() {
    }

    public DonationHistoryDto(Long id, String hospitalName, LocalDateTime donationDate, BloodGroup bloodGroup, Integer unitsDonated, boolean verified) {
        this.id = id;
        this.hospitalName = hospitalName;
        this.donationDate = donationDate;
        this.bloodGroup = bloodGroup;
        this.unitsDonated = unitsDonated;
        this.verified = verified;
    }

    public static class DonationHistoryDtoBuilder {
        private Long id;
        private String hospitalName;
        private LocalDateTime donationDate;
        private BloodGroup bloodGroup;
        private Integer unitsDonated;
        private boolean verified;

        DonationHistoryDtoBuilder() {
        }

        public DonationHistoryDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DonationHistoryDtoBuilder hospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
            return this;
        }

        public DonationHistoryDtoBuilder donationDate(LocalDateTime donationDate) {
            this.donationDate = donationDate;
            return this;
        }

        public DonationHistoryDtoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public DonationHistoryDtoBuilder unitsDonated(Integer unitsDonated) {
            this.unitsDonated = unitsDonated;
            return this;
        }

        public DonationHistoryDtoBuilder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public DonationHistoryDto build() {
            return new DonationHistoryDto(this.id, this.hospitalName, this.donationDate, this.bloodGroup, this.unitsDonated, this.verified);
        }

        public String toString() {
            return "DonationHistoryDto.DonationHistoryDtoBuilder(id=" + this.id + ", hospitalName=" + this.hospitalName + ", donationDate=" + this.donationDate + ", bloodGroup=" + this.bloodGroup + ", unitsDonated=" + this.unitsDonated + ", verified=" + this.verified + ")";
        }
    }
}

