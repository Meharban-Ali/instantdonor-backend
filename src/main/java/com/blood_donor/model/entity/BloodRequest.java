/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.EntityListeners
 *  jakarta.persistence.EnumType
 *  jakarta.persistence.Enumerated
 *  jakarta.persistence.FetchType
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.Index
 *  jakarta.persistence.JoinColumn
 *  jakarta.persistence.ManyToOne
 *  jakarta.persistence.Table
 *  org.springframework.data.annotation.CreatedDate
 *  org.springframework.data.annotation.LastModifiedDate
 *  org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
package com.blood_donor.model.entity;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.blood_request.BloodRequestStatus;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="blood_requests", indexes={@Index(name="idx_br_status", columnList="status"), @Index(name="idx_br_blood_group", columnList="bloodGroup"), @Index(name="idx_br_city", columnList="city"), @Index(name="idx_br_urgency", columnList="urgency"), @Index(name="idx_br_expired_at", columnList="expiredAt"), @Index(name="idx_br_created_at", columnList="createdAt"), @Index(name="idx_br_created_by", columnList="created_by")})
@EntityListeners(value={AuditingEntityListener.class})
public class BloodRequest {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=100)
    private String patientName;
    @Column(nullable=false, length=15)
    private String contactPhone;
    @Column(nullable=false, length=200)
    private String hospitalName;
    @Column(nullable=false, length=100)
    private String city;
    @Column(precision=10, scale=8)
    private BigDecimal latitude;
    @Column(precision=11, scale=8)
    private BigDecimal longitude;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false)
    private BloodGroup bloodGroup;
    @Column(nullable=false)
    private Integer unitsRequired;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false, length=10)
    private UrgencyLevel urgency;
    @Column(columnDefinition="TEXT")
    private String additionalInfo;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false, length=20)
    private BloodRequestStatus status;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="created_by", nullable=false)
    private User createdBy;
    @Column(nullable=false)
    private LocalDateTime expiredAt;
    private LocalDateTime fulfilledAt;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean isExpiredByTime() {
        return this.expiredAt != null && LocalDateTime.now().isAfter(this.expiredAt);
    }

    public boolean isActive() {
        return this.status == BloodRequestStatus.PENDING || this.status == BloodRequestStatus.ACTIVE;
    }

    public boolean isTerminal() {
        return this.status == BloodRequestStatus.FULFILLED || this.status == BloodRequestStatus.CANCELLED || this.status == BloodRequestStatus.EXPIRED;
    }

    public boolean isFulfilled() {
        return this.status == BloodRequestStatus.FULFILLED;
    }

    public long getMinutesRemaining() {
        if (this.expiredAt == null || this.isTerminal()) {
            return 0L;
        }
        return Math.max(0L, ChronoUnit.MINUTES.between(LocalDateTime.now(), this.expiredAt));
    }

    public boolean hasLocation() {
        return this.latitude != null && this.longitude != null;
    }

    public void updateStatus(BloodRequestStatus newStatus) {
        if (this.status != null && this.status.isTerminal()) {
            throw new IllegalStateException("Cannot change status from terminal state: " + this.status);
        }
        this.status = newStatus;
        if (newStatus == BloodRequestStatus.FULFILLED) {
            this.fulfilledAt = LocalDateTime.now();
        }
    }

    private static Integer $default$unitsRequired() {
        return 1;
    }

    private static UrgencyLevel $default$urgency() {
        return UrgencyLevel.NORMAL;
    }

    private static BloodRequestStatus $default$status() {
        return BloodRequestStatus.PENDING;
    }

    public static BloodRequestBuilder builder() {
        return new BloodRequestBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getPatientName() {
        return this.patientName;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public String getHospitalName() {
        return this.hospitalName;
    }

    public String getCity() {
        return this.city;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public Integer getUnitsRequired() {
        return this.unitsRequired;
    }

    public UrgencyLevel getUrgency() {
        return this.urgency;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public BloodRequestStatus getStatus() {
        return this.status;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public LocalDateTime getExpiredAt() {
        return this.expiredAt;
    }

    public LocalDateTime getFulfilledAt() {
        return this.fulfilledAt;
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

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setUnitsRequired(Integer unitsRequired) {
        this.unitsRequired = unitsRequired;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setStatus(BloodRequestStatus status) {
        this.status = status;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void setFulfilledAt(LocalDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BloodRequest() {
        this.unitsRequired = BloodRequest.$default$unitsRequired();
        this.urgency = BloodRequest.$default$urgency();
        this.status = BloodRequest.$default$status();
    }

    public BloodRequest(Long id, String patientName, String contactPhone, String hospitalName, String city, BigDecimal latitude, BigDecimal longitude, BloodGroup bloodGroup, Integer unitsRequired, UrgencyLevel urgency, String additionalInfo, BloodRequestStatus status, User createdBy, LocalDateTime expiredAt, LocalDateTime fulfilledAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.patientName = patientName;
        this.contactPhone = contactPhone;
        this.hospitalName = hospitalName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bloodGroup = bloodGroup;
        this.unitsRequired = unitsRequired;
        this.urgency = urgency;
        this.additionalInfo = additionalInfo;
        this.status = status;
        this.createdBy = createdBy;
        this.expiredAt = expiredAt;
        this.fulfilledAt = fulfilledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static class BloodRequestBuilder {
        private Long id;
        private String patientName;
        private String contactPhone;
        private String hospitalName;
        private String city;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private BloodGroup bloodGroup;
        private boolean unitsRequired$set;
        private Integer unitsRequired$value;
        private boolean urgency$set;
        private UrgencyLevel urgency$value;
        private String additionalInfo;
        private boolean status$set;
        private BloodRequestStatus status$value;
        private User createdBy;
        private LocalDateTime expiredAt;
        private LocalDateTime fulfilledAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        BloodRequestBuilder() {
        }

        public BloodRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BloodRequestBuilder patientName(String patientName) {
            this.patientName = patientName;
            return this;
        }

        public BloodRequestBuilder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public BloodRequestBuilder hospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
            return this;
        }

        public BloodRequestBuilder city(String city) {
            this.city = city;
            return this;
        }

        public BloodRequestBuilder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public BloodRequestBuilder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public BloodRequestBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public BloodRequestBuilder unitsRequired(Integer unitsRequired) {
            this.unitsRequired$value = unitsRequired;
            this.unitsRequired$set = true;
            return this;
        }

        public BloodRequestBuilder urgency(UrgencyLevel urgency) {
            this.urgency$value = urgency;
            this.urgency$set = true;
            return this;
        }

        public BloodRequestBuilder additionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }

        public BloodRequestBuilder status(BloodRequestStatus status) {
            this.status$value = status;
            this.status$set = true;
            return this;
        }

        public BloodRequestBuilder createdBy(User createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public BloodRequestBuilder expiredAt(LocalDateTime expiredAt) {
            this.expiredAt = expiredAt;
            return this;
        }

        public BloodRequestBuilder fulfilledAt(LocalDateTime fulfilledAt) {
            this.fulfilledAt = fulfilledAt;
            return this;
        }

        public BloodRequestBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BloodRequestBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BloodRequest build() {
            Integer unitsRequired$value = this.unitsRequired$value;
            if (!this.unitsRequired$set) {
                unitsRequired$value = BloodRequest.$default$unitsRequired();
            }
            UrgencyLevel urgency$value = this.urgency$value;
            if (!this.urgency$set) {
                urgency$value = BloodRequest.$default$urgency();
            }
            BloodRequestStatus status$value = this.status$value;
            if (!this.status$set) {
                status$value = BloodRequest.$default$status();
            }
            return new BloodRequest(this.id, this.patientName, this.contactPhone, this.hospitalName, this.city, this.latitude, this.longitude, this.bloodGroup, unitsRequired$value, urgency$value, this.additionalInfo, status$value, this.createdBy, this.expiredAt, this.fulfilledAt, this.createdAt, this.updatedAt);
        }

        public String toString() {
            return "BloodRequest.BloodRequestBuilder(id=" + this.id + ", patientName=" + this.patientName + ", contactPhone=" + this.contactPhone + ", hospitalName=" + this.hospitalName + ", city=" + this.city + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", bloodGroup=" + this.bloodGroup + ", unitsRequired$value=" + this.unitsRequired$value + ", urgency$value=" + this.urgency$value + ", additionalInfo=" + this.additionalInfo + ", status$value=" + this.status$value + ", createdBy=" + this.createdBy + ", expiredAt=" + this.expiredAt + ", fulfilledAt=" + this.fulfilledAt + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ")";
        }
    }
}

