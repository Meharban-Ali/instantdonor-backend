package com.blood_donor.admin.dto;

import com.blood_donor.common.enums.Role;
import jakarta.validation.constraints.NotNull;

public class RoleUpdateRequest {
    @NotNull(message="Role is required")
    private @NotNull(message="Role is required") Role role;
    private String reason;

    public Role getRole() {
        return this.role;
    }

    public String getReason() {
        return this.reason;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoleUpdateRequest)) {
            return false;
        }
        RoleUpdateRequest other = (RoleUpdateRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Role this$role = this.getRole();
        Role other$role = other.getRole();
        if (this$role == null ? other$role != null : !((Object)((Object)this$role)).equals((Object)other$role)) {
            return false;
        }
        String this$reason = this.getReason();
        String other$reason = other.getReason();
        return !(this$reason == null ? other$reason != null : !this$reason.equals(other$reason));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoleUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Role $role = this.getRole();
        result = result * 59 + ($role == null ? 43 : ((Object)((Object)$role)).hashCode());
        String $reason = this.getReason();
        result = result * 59 + ($reason == null ? 43 : $reason.hashCode());
        return result;
    }

    public String toString() {
        return "RoleUpdateRequest(role=" + this.getRole() + ", reason=" + this.getReason() + ")";
    }

    public RoleUpdateRequest() {
    }

    public RoleUpdateRequest(Role role, String reason) {
        this.role = role;
        this.reason = reason;
    }
}

