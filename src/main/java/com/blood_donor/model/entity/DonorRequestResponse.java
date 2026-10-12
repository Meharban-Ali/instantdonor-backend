package com.blood_donor.model.entity;

import com.blood_donor.model.dto.response.donor_response.status.ResponseStatus;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="donor_request_responses", indexes={@Index(name="idx_drr_donor", columnList="donor_id"), @Index(name="idx_drr_request", columnList="request_id"), @Index(name="idx_drr_status", columnList="status"), @Index(name="idx_drr_notified_at", columnList="notifiedAt")}, uniqueConstraints={@UniqueConstraint(columnNames={"donor_id", "request_id"}, name="unique_donor_request")})
@EntityListeners(value={AuditingEntityListener.class})
public class DonorRequestResponse {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="donor_id", nullable=false)
    private DonorProfile donor;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="request_id", nullable=false)
    private BloodRequest request;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false, length=20)
    private ResponseStatus status;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime notifiedAt;
    private LocalDateTime respondedAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    @Column(length=500)
    private String notes;

    public void markAsAccepted() {
        this.validateNotTerminal();
        this.status = ResponseStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }

    public void markAsRejected(String reason) {
        this.validateNotTerminal();
        this.status = ResponseStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
        this.notes = reason;
    }

    public void markAsNoResponse() {
        this.validateNotTerminal();
        this.status = ResponseStatus.NO_RESPONSE;
        this.respondedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.validateNotTerminal();
        this.status = ResponseStatus.CANCELLED;
        this.respondedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status.isPending();
    }

    public boolean isAccepted() {
        return this.status.isPositive();
    }

    public boolean isTerminal() {
        return this.status.isTerminal();
    }

    private void validateNotTerminal() {
        if (this.status != null && this.status.isTerminal()) {
            throw new IllegalStateException("Cannot change status from terminal state: " + this.status);
        }
    }

    private static ResponseStatus $default$status() {
        return ResponseStatus.NOTIFIED;
    }

    public static DonorRequestResponseBuilder builder() {
        return new DonorRequestResponseBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public DonorProfile getDonor() {
        return this.donor;
    }

    public BloodRequest getRequest() {
        return this.request;
    }

    public ResponseStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getNotifiedAt() {
        return this.notifiedAt;
    }

    public LocalDateTime getRespondedAt() {
        return this.respondedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDonor(DonorProfile donor) {
        this.donor = donor;
    }

    public void setRequest(BloodRequest request) {
        this.request = request;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public void setNotifiedAt(LocalDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public DonorRequestResponse() {
        this.status = DonorRequestResponse.$default$status();
    }

    public DonorRequestResponse(Long id, DonorProfile donor, BloodRequest request, ResponseStatus status, LocalDateTime notifiedAt, LocalDateTime respondedAt, LocalDateTime updatedAt, String notes) {
        this.id = id;
        this.donor = donor;
        this.request = request;
        this.status = status;
        this.notifiedAt = notifiedAt;
        this.respondedAt = respondedAt;
        this.updatedAt = updatedAt;
        this.notes = notes;
    }

    public static class DonorRequestResponseBuilder {
        private Long id;
        private DonorProfile donor;
        private BloodRequest request;
        private boolean status$set;
        private ResponseStatus status$value;
        private LocalDateTime notifiedAt;
        private LocalDateTime respondedAt;
        private LocalDateTime updatedAt;
        private String notes;

        DonorRequestResponseBuilder() {
        }

        public DonorRequestResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DonorRequestResponseBuilder donor(DonorProfile donor) {
            this.donor = donor;
            return this;
        }

        public DonorRequestResponseBuilder request(BloodRequest request) {
            this.request = request;
            return this;
        }

        public DonorRequestResponseBuilder status(ResponseStatus status) {
            this.status$value = status;
            this.status$set = true;
            return this;
        }

        public DonorRequestResponseBuilder notifiedAt(LocalDateTime notifiedAt) {
            this.notifiedAt = notifiedAt;
            return this;
        }

        public DonorRequestResponseBuilder respondedAt(LocalDateTime respondedAt) {
            this.respondedAt = respondedAt;
            return this;
        }

        public DonorRequestResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public DonorRequestResponseBuilder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public DonorRequestResponse build() {
            ResponseStatus status$value = this.status$value;
            if (!this.status$set) {
                status$value = DonorRequestResponse.$default$status();
            }
            return new DonorRequestResponse(this.id, this.donor, this.request, status$value, this.notifiedAt, this.respondedAt, this.updatedAt, this.notes);
        }

        public String toString() {
            return "DonorRequestResponse.DonorRequestResponseBuilder(id=" + this.id + ", donor=" + this.donor + ", request=" + this.request + ", status$value=" + this.status$value + ", notifiedAt=" + this.notifiedAt + ", respondedAt=" + this.respondedAt + ", updatedAt=" + this.updatedAt + ", notes=" + this.notes + ")";
        }
    }
}

