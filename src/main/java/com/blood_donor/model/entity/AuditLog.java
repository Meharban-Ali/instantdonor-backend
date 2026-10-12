package com.blood_donor.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="audit_logs", indexes={@Index(name="idx_audit_timestamp", columnList="timestamp")})
@EntityListeners(value={AuditingEntityListener.class})
public class AuditLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, length=100)
    private String action;
    @Column(columnDefinition="TEXT")
    private String details;
    @Column(length=150)
    private String performedBy;
    @Column(length=45)
    private String clientIp;
    @CreatedDate
    @Column(nullable=false, updatable=false)
    private LocalDateTime timestamp;

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getAction() {
        return this.action;
    }

    public String getDetails() {
        return this.details;
    }

    public String getPerformedBy() {
        return this.performedBy;
    }

    public String getClientIp() {
        return this.clientIp;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public AuditLog() {
    }

    public AuditLog(Long id, String action, String details, String performedBy, String clientIp, LocalDateTime timestamp) {
        this.id = id;
        this.action = action;
        this.details = details;
        this.performedBy = performedBy;
        this.clientIp = clientIp;
        this.timestamp = timestamp;
    }

    public static class AuditLogBuilder {
        private Long id;
        private String action;
        private String details;
        private String performedBy;
        private String clientIp;
        private LocalDateTime timestamp;

        AuditLogBuilder() {
        }

        public AuditLogBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuditLogBuilder action(String action) {
            this.action = action;
            return this;
        }

        public AuditLogBuilder details(String details) {
            this.details = details;
            return this;
        }

        public AuditLogBuilder performedBy(String performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        public AuditLogBuilder clientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public AuditLogBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AuditLog build() {
            return new AuditLog(this.id, this.action, this.details, this.performedBy, this.clientIp, this.timestamp);
        }

        public String toString() {
            return "AuditLog.AuditLogBuilder(id=" + this.id + ", action=" + this.action + ", details=" + this.details + ", performedBy=" + this.performedBy + ", clientIp=" + this.clientIp + ", timestamp=" + this.timestamp + ")";
        }
    }
}

