package com.blood_donor.model.entity;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.entity.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="donor_profiles", indexes={@Index(name="idx_donor_user", columnList="user_id"), @Index(name="idx_donor_blood_group", columnList="bloodGroup"), @Index(name="idx_donor_city", columnList="city"), @Index(name="idx_donor_available", columnList="isAvailable")})
@EntityListeners(value={AuditingEntityListener.class})
public class DonorProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, updatable=false)
    private User user;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false)
    private BloodGroup bloodGroup;
    @NotBlank(message="City is required")
    @Column(nullable=false, length=50)
    private @NotBlank(message="City is required") String city;
    @NotBlank(message="State is required")
    @Column(nullable=false, length=50)
    private @NotBlank(message="State is required") String state;
    @Column(nullable=false, precision=10, scale=8)
    private BigDecimal latitude;
    @Column(nullable=false, precision=11, scale=8)
    private BigDecimal longitude;
    @Column(nullable=false)
    private boolean isAvailable;
    private LocalDate lastDonationDate;
    private LocalDate nextEligibleDate;
    @Column(precision=5, scale=2, nullable=false)
    private BigDecimal reliabilityScore;
    @Column(nullable=false)
    private Integer totalDonations;
    @Column(nullable=false, precision=5, scale=2)
    private BigDecimal responseRate;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean isEligibleToDonate() {
        if (this.lastDonationDate == null) {
            return true;
        }
        return LocalDate.now().isAfter(this.lastDonationDate.plusDays(90L));
    }

    public void recordDonation() {
        this.lastDonationDate = LocalDate.now();
        this.nextEligibleDate = this.lastDonationDate.plusDays(90L);
        Integer n = this.totalDonations;
        this.totalDonations = this.totalDonations + 1;
        this.isAvailable = false;
    }

    private static boolean $default$isAvailable() {
        return true;
    }

    private static BigDecimal $default$reliabilityScore() {
        return BigDecimal.valueOf(50.0);
    }

    private static Integer $default$totalDonations() {
        return 0;
    }

    private static BigDecimal $default$responseRate() {
        return BigDecimal.ZERO;
    }

    public static DonorProfileBuilder builder() {
        return new DonorProfileBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    public LocalDate getLastDonationDate() {
        return this.lastDonationDate;
    }

    public LocalDate getNextEligibleDate() {
        return this.nextEligibleDate;
    }

    public BigDecimal getReliabilityScore() {
        return this.reliabilityScore;
    }

    public Integer getTotalDonations() {
        return this.totalDonations;
    }

    public BigDecimal getResponseRate() {
        return this.responseRate;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public void setNextEligibleDate(LocalDate nextEligibleDate) {
        this.nextEligibleDate = nextEligibleDate;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    public void setResponseRate(BigDecimal responseRate) {
        this.responseRate = responseRate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DonorProfile(Long id, User user, BloodGroup bloodGroup, String city, String state, BigDecimal latitude, BigDecimal longitude, boolean isAvailable, LocalDate lastDonationDate, LocalDate nextEligibleDate, BigDecimal reliabilityScore, Integer totalDonations, BigDecimal responseRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isAvailable = isAvailable;
        this.lastDonationDate = lastDonationDate;
        this.nextEligibleDate = nextEligibleDate;
        this.reliabilityScore = reliabilityScore;
        this.totalDonations = totalDonations;
        this.responseRate = responseRate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public DonorProfile() {
        this.isAvailable = DonorProfile.$default$isAvailable();
        this.reliabilityScore = DonorProfile.$default$reliabilityScore();
        this.totalDonations = DonorProfile.$default$totalDonations();
        this.responseRate = DonorProfile.$default$responseRate();
    }

    public static class DonorProfileBuilder {
        private Long id;
        private User user;
        private BloodGroup bloodGroup;
        private String city;
        private String state;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private boolean isAvailable$set;
        private boolean isAvailable$value;
        private LocalDate lastDonationDate;
        private LocalDate nextEligibleDate;
        private boolean reliabilityScore$set;
        private BigDecimal reliabilityScore$value;
        private boolean totalDonations$set;
        private Integer totalDonations$value;
        private boolean responseRate$set;
        private BigDecimal responseRate$value;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        DonorProfileBuilder() {
        }

        public DonorProfileBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DonorProfileBuilder user(User user) {
            this.user = user;
            return this;
        }

        public DonorProfileBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public DonorProfileBuilder city(String city) {
            this.city = city;
            return this;
        }

        public DonorProfileBuilder state(String state) {
            this.state = state;
            return this;
        }

        public DonorProfileBuilder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public DonorProfileBuilder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public DonorProfileBuilder isAvailable(boolean isAvailable) {
            this.isAvailable$value = isAvailable;
            this.isAvailable$set = true;
            return this;
        }

        public DonorProfileBuilder lastDonationDate(LocalDate lastDonationDate) {
            this.lastDonationDate = lastDonationDate;
            return this;
        }

        public DonorProfileBuilder nextEligibleDate(LocalDate nextEligibleDate) {
            this.nextEligibleDate = nextEligibleDate;
            return this;
        }

        public DonorProfileBuilder reliabilityScore(BigDecimal reliabilityScore) {
            this.reliabilityScore$value = reliabilityScore;
            this.reliabilityScore$set = true;
            return this;
        }

        public DonorProfileBuilder totalDonations(Integer totalDonations) {
            this.totalDonations$value = totalDonations;
            this.totalDonations$set = true;
            return this;
        }

        public DonorProfileBuilder responseRate(BigDecimal responseRate) {
            this.responseRate$value = responseRate;
            this.responseRate$set = true;
            return this;
        }

        public DonorProfileBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DonorProfileBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DonorProfile build() {
            boolean isAvailable$value = this.isAvailable$value;
            if (!this.isAvailable$set) {
                isAvailable$value = DonorProfile.$default$isAvailable();
            }
            BigDecimal reliabilityScore$value = this.reliabilityScore$value;
            if (!this.reliabilityScore$set) {
                reliabilityScore$value = DonorProfile.$default$reliabilityScore();
            }
            Integer totalDonations$value = this.totalDonations$value;
            if (!this.totalDonations$set) {
                totalDonations$value = DonorProfile.$default$totalDonations();
            }
            BigDecimal responseRate$value = this.responseRate$value;
            if (!this.responseRate$set) {
                responseRate$value = DonorProfile.$default$responseRate();
            }
            return new DonorProfile(this.id, this.user, this.bloodGroup, this.city, this.state, this.latitude, this.longitude, isAvailable$value, this.lastDonationDate, this.nextEligibleDate, reliabilityScore$value, totalDonations$value, responseRate$value, this.createdAt, this.updatedAt);
        }

        public String toString() {
            return "DonorProfile.DonorProfileBuilder(id=" + this.id + ", user=" + this.user + ", bloodGroup=" + this.bloodGroup + ", city=" + this.city + ", state=" + this.state + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", isAvailable$value=" + this.isAvailable$value + ", lastDonationDate=" + this.lastDonationDate + ", nextEligibleDate=" + this.nextEligibleDate + ", reliabilityScore$value=" + this.reliabilityScore$value + ", totalDonations$value=" + this.totalDonations$value + ", responseRate$value=" + this.responseRate$value + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
        }
    }
}

