package com.blood_donor.model.dto.forgetpassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotBlank(message="Current password is required")
    private @NotBlank(message="Current password is required") String currentPassword;
    @NotBlank(message="New password is required")
    @Size(min=8, message="New password must be at least 8 characters")
    @jakarta.validation.constraints.Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$", message="Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String newPassword;

    public String getCurrentPassword() {
        return this.currentPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangePasswordRequest)) {
            return false;
        }
        ChangePasswordRequest other = (ChangePasswordRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$currentPassword = this.getCurrentPassword();
        String other$currentPassword = other.getCurrentPassword();
        if (this$currentPassword == null ? other$currentPassword != null : !this$currentPassword.equals(other$currentPassword)) {
            return false;
        }
        String this$newPassword = this.getNewPassword();
        String other$newPassword = other.getNewPassword();
        return !(this$newPassword == null ? other$newPassword != null : !this$newPassword.equals(other$newPassword));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ChangePasswordRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $currentPassword = this.getCurrentPassword();
        result = result * 59 + ($currentPassword == null ? 43 : $currentPassword.hashCode());
        String $newPassword = this.getNewPassword();
        result = result * 59 + ($newPassword == null ? 43 : $newPassword.hashCode());
        return result;
    }

    public String toString() {
        return "ChangePasswordRequest(currentPassword=" + this.getCurrentPassword() + ", newPassword=" + this.getNewPassword() + ")";
    }
}

