package com.blood_donor.model.entity;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(value={AuditingEntityListener.class})
@Table(name="donation_history", indexes={@Index(name="idx_donation_donor", columnList="donor_id"), @Index(name="idx_donation_request", columnList="request_id"), @Index(name="idx_donation_date", columnList="donationDate"), @Index(name="idx_donation_blood_group", columnList="bloodGroup")})
public class DonationHistory {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="donor_id", nullable=false)
    private DonorProfile donorProfile;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="request_id", nullable=false)
    private BloodRequest bloodRequest;
    @Column(nullable=false, length=100)
    @NotBlank(message="Hospital name is required")
    private @NotBlank(message="Hospital name is required") String hospitalName;
    @Column(nullable=false)
    private LocalDateTime donationDate;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false)
    private BloodGroup bloodGroup;
    @Column(nullable=false)
    private Integer unitsDonated;
    @Column(nullable=false)
    private boolean isVerified;
    private String certificateUrl;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private static Integer $default$unitsDonated() {
        return 1;
    }

    private static boolean $default$isVerified() {
        return false;
    }

    public static DonationHistoryBuilder builder() {
        return new DonationHistoryBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public DonorProfile getDonorProfile() {
        return this.donorProfile;
    }

    public BloodRequest getBloodRequest() {
        return this.bloodRequest;
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
        return this.isVerified;
    }

    public String getCertificateUrl() {
        return this.certificateUrl;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDonorProfile(DonorProfile donorProfile) {
        this.donorProfile = donorProfile;
    }

    public void setBloodRequest(BloodRequest bloodRequest) {
        this.bloodRequest = bloodRequest;
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

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DonationHistory(Long id, DonorProfile donorProfile, BloodRequest bloodRequest, String hospitalName, LocalDateTime donationDate, BloodGroup bloodGroup, Integer unitsDonated, boolean isVerified, String certificateUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.donorProfile = donorProfile;
        this.bloodRequest = bloodRequest;
        this.hospitalName = hospitalName;
        this.donationDate = donationDate;
        this.bloodGroup = bloodGroup;
        this.unitsDonated = unitsDonated;
        this.isVerified = isVerified;
        this.certificateUrl = certificateUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public DonationHistory() {
        this.unitsDonated = DonationHistory.$default$unitsDonated();
        this.isVerified = DonationHistory.$default$isVerified();
    }

    public static class DonationHistoryBuilder {
        private Long id;
        private DonorProfile donorProfile;
        private BloodRequest bloodRequest;
        private String hospitalName;
        private LocalDateTime donationDate;
        private BloodGroup bloodGroup;
        private boolean unitsDonated$set;
        private Integer unitsDonated$value;
        private boolean isVerified$set;
        private boolean isVerified$value;
        private String certificateUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        DonationHistoryBuilder() {
        }

        public DonationHistoryBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DonationHistoryBuilder donorProfile(DonorProfile donorProfile) {
            this.donorProfile = donorProfile;
            return this;
        }

        public DonationHistoryBuilder bloodRequest(BloodRequest bloodRequest) {
            this.bloodRequest = bloodRequest;
            return this;
        }

        public DonationHistoryBuilder hospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
            return this;
        }

        public DonationHistoryBuilder donationDate(LocalDateTime donationDate) {
            this.donationDate = donationDate;
            return this;
        }

        public DonationHistoryBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public DonationHistoryBuilder unitsDonated(Integer unitsDonated) {
            this.unitsDonated$value = unitsDonated;
            this.unitsDonated$set = true;
            return this;
        }

        public DonationHistoryBuilder isVerified(boolean isVerified) {
            this.isVerified$value = isVerified;
            this.isVerified$set = true;
            return this;
        }

        public DonationHistoryBuilder certificateUrl(String certificateUrl) {
            this.certificateUrl = certificateUrl;
            return this;
        }

        public DonationHistoryBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DonationHistoryBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DonationHistory build() {
            Integer unitsDonated$value = this.unitsDonated$value;
            if (!this.unitsDonated$set) {
                unitsDonated$value = DonationHistory.$default$unitsDonated();
            }
            boolean isVerified$value = this.isVerified$value;
            if (!this.isVerified$set) {
                isVerified$value = DonationHistory.$default$isVerified();
            }
            return new DonationHistory(this.id, this.donorProfile, this.bloodRequest, this.hospitalName, this.donationDate, this.bloodGroup, unitsDonated$value, isVerified$value, this.certificateUrl, this.createdAt, this.updatedAt);
        }

        public String toString() {
            return "DonationHistory.DonationHistoryBuilder(id=" + this.id + ", donorProfile=" + this.donorProfile + ", bloodRequest=" + this.bloodRequest + ", hospitalName=" + this.hospitalName + ", donationDate=" + this.donationDate + ", bloodGroup=" + this.bloodGroup + ", unitsDonated$value=" + this.unitsDonated$value + ", isVerified$value=" + this.isVerified$value + ", certificateUrl=" + this.certificateUrl + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
        }
    }
}

