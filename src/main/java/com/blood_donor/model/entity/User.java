/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.CascadeType
 *  jakarta.persistence.CollectionTable
 *  jakarta.persistence.Column
 *  jakarta.persistence.ElementCollection
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
 *  jakarta.persistence.OneToOne
 *  jakarta.persistence.Table
 *  jakarta.validation.constraints.Email
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.Pattern
 *  jakarta.validation.constraints.Size
 *  org.springframework.data.annotation.CreatedDate
 *  org.springframework.data.annotation.LastModifiedDate
 *  org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
package com.blood_donor.model.entity;

import com.blood_donor.common.enums.Role;
import com.blood_donor.model.entity.DonorProfile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="users", indexes={@Index(name="idx_user_email", columnList="email")})
@EntityListeners(value={AuditingEntityListener.class})
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message="Name is required")
    @Size(max=100, message="Name cannot exceed 100 characters")
    @Column(nullable=false, length=100)
    private @NotBlank(message="Name is required") @Size(max=100, message="Name cannot exceed 100 characters") String name;
    @NotBlank(message="Email is required")
    @Email(message="Invalid email format")
    @Column(nullable=false, length=150, unique=true)
    private @NotBlank(message="Email is required") @Email(message="Invalid email format") String email;
    @NotBlank(message="Password is required")
    @Column(nullable=false, length=255)
    private @NotBlank(message="Password is required") String password;
    @NotBlank(message="Phone is required")
    @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid phone number")
    @Column(nullable=false, length=15)
    private @NotBlank(message="Phone is required") @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid phone number") String phone;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="user_roles", joinColumns={@JoinColumn(name="user_id")})
    @Column(name="role", nullable=false)
    @Enumerated(value=EnumType.STRING)
    private Set<Role> roles;
    @Column(nullable=false)
    private Boolean isActive;
    @CreatedDate
    @Column(updatable=false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    @OneToOne(mappedBy="user", cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
    private DonorProfile donorProfile;

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        if (this.roles.size() <= 1) {
            throw new IllegalStateException("User must have at least one role");
        }
        this.roles.remove((Object)role);
    }

    public boolean isDonor() {
        return this.roles.contains((Object)Role.DONOR);
    }

    public boolean isAdmin() {
        return this.roles.contains((Object)Role.ADMIN);
    }

    public boolean isHospital() {
        return this.roles.contains((Object)Role.HOSPITAL);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains((Object)role);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }

    private static Set<Role> $default$roles() {
        return new HashSet<Role>(Set.of(Role.USER));
    }

    private static Boolean $default$isActive() {
        return true;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPhone() {
        return this.phone;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return this.lastLoginAt;
    }

    public DonorProfile getDonorProfile() {
        return this.donorProfile;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setDonorProfile(DonorProfile donorProfile) {
        this.donorProfile = donorProfile;
    }

    public User(Long id, String name, String email, String password, String phone, Set<Role> roles, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt, DonorProfile donorProfile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.roles = roles;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
        this.donorProfile = donorProfile;
    }

    public User() {
        this.roles = User.$default$roles();
        this.isActive = User.$default$isActive();
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String phone;
        private boolean roles$set;
        private Set<Role> roles$value;
        private boolean isActive$set;
        private Boolean isActive$value;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLoginAt;
        private DonorProfile donorProfile;

        UserBuilder() {
        }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder roles(Set<Role> roles) {
            this.roles$value = roles;
            this.roles$set = true;
            return this;
        }

        public UserBuilder isActive(Boolean isActive) {
            this.isActive$value = isActive;
            this.isActive$set = true;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public UserBuilder lastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserBuilder donorProfile(DonorProfile donorProfile) {
            this.donorProfile = donorProfile;
            return this;
        }

        public User build() {
            Set<Role> roles$value = this.roles$value;
            if (!this.roles$set) {
                roles$value = User.$default$roles();
            }
            Boolean isActive$value = this.isActive$value;
            if (!this.isActive$set) {
                isActive$value = User.$default$isActive();
            }
            return new User(this.id, this.name, this.email, this.password, this.phone, roles$value, isActive$value, this.createdAt, this.updatedAt, this.lastLoginAt, this.donorProfile);
        }

        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", name=" + this.name + ", email=" + this.email + ", password=" + this.password + ", phone=" + this.phone + ", roles$value=" + this.roles$value + ", isActive$value=" + this.isActive$value + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", lastLoginAt=" + this.lastLoginAt + ", donorProfile=" + this.donorProfile + ")";
        }
    }
}

