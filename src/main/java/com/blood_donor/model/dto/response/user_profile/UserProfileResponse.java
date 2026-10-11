/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.model.dto.response.user_profile;

import com.blood_donor.common.enums.Role;
import java.time.LocalDateTime;
import java.util.Set;

public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Set<Role> roles;
    private Boolean isActive;
    private LocalDateTime createdAt;

    UserProfileResponse(Long id, String name, String email, String phone, Set<Role> roles, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roles = roles;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public static UserProfileResponseBuilder builder() {
        return new UserProfileResponseBuilder();
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserProfileResponse)) {
            return false;
        }
        UserProfileResponse other = (UserProfileResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Boolean this$isActive = this.getIsActive();
        Boolean other$isActive = other.getIsActive();
        if (this$isActive == null ? other$isActive != null : !((Object)this$isActive).equals(other$isActive)) {
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
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        if (this$phone == null ? other$phone != null : !this$phone.equals(other$phone)) {
            return false;
        }
        Set<Role> this$roles = this.getRoles();
        Set<Role> other$roles = other.getRoles();
        if (this$roles == null ? other$roles != null : !((Object)this$roles).equals(other$roles)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserProfileResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Boolean $isActive = this.getIsActive();
        result = result * 59 + ($isActive == null ? 43 : ((Object)$isActive).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        Set<Role> $roles = this.getRoles();
        result = result * 59 + ($roles == null ? 43 : ((Object)$roles).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "UserProfileResponse(id=" + this.getId() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", phone=" + this.getPhone() + ", roles=" + this.getRoles() + ", isActive=" + this.getIsActive() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    public static class UserProfileResponseBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private Set<Role> roles;
        private Boolean isActive;
        private LocalDateTime createdAt;

        UserProfileResponseBuilder() {
        }

        public UserProfileResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserProfileResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserProfileResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileResponseBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserProfileResponseBuilder roles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public UserProfileResponseBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserProfileResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserProfileResponse build() {
            return new UserProfileResponse(this.id, this.name, this.email, this.phone, this.roles, this.isActive, this.createdAt);
        }

        public String toString() {
            return "UserProfileResponse.UserProfileResponseBuilder(id=" + this.id + ", name=" + this.name + ", email=" + this.email + ", phone=" + this.phone + ", roles=" + this.roles + ", isActive=" + this.isActive + ", createdAt=" + this.createdAt + ")";
        }
    }
}

