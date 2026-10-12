package com.blood_donor.model.entity;

import com.blood_donor.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="password_reset_tokens", indexes={@Index(name="idx_prt_token", columnList="token", unique=true), @Index(name="idx_prt_user_id", columnList="user_id"), @Index(name="idx_prt_expiry", columnList="expires_at")})
@EntityListeners(value={AuditingEntityListener.class})
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=200)
    private String token;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Column(nullable=false)
    private LocalDateTime expiresAt;
    @Column(nullable=false)
    private Boolean used;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return this.used == false && !this.isExpired();
    }

    private static Boolean $default$used() {
        return false;
    }

    public static PasswordResetTokenBuilder builder() {
        return new PasswordResetTokenBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return this.user;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public Boolean getUsed() {
        return this.used;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PasswordResetToken() {
        this.used = PasswordResetToken.$default$used();
    }

    public PasswordResetToken(Long id, String token, User user, LocalDateTime expiresAt, Boolean used, LocalDateTime createdAt) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    public static class PasswordResetTokenBuilder {
        private Long id;
        private String token;
        private User user;
        private LocalDateTime expiresAt;
        private boolean used$set;
        private Boolean used$value;
        private LocalDateTime createdAt;

        PasswordResetTokenBuilder() {
        }

        public PasswordResetTokenBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PasswordResetTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        public PasswordResetTokenBuilder user(User user) {
            this.user = user;
            return this;
        }

        public PasswordResetTokenBuilder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public PasswordResetTokenBuilder used(Boolean used) {
            this.used$value = used;
            this.used$set = true;
            return this;
        }

        public PasswordResetTokenBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PasswordResetToken build() {
            Boolean used$value = this.used$value;
            if (!this.used$set) {
                used$value = PasswordResetToken.$default$used();
            }
            return new PasswordResetToken(this.id, this.token, this.user, this.expiresAt, used$value, this.createdAt);
        }

        public String toString() {
            return "PasswordResetToken.PasswordResetTokenBuilder(id=" + this.id + ", token=" + this.token + ", user=" + this.user + ", expiresAt=" + this.expiresAt + ", used$value=" + this.used$value + ", createdAt=" + this.createdAt + ")";
        }
    }
}

