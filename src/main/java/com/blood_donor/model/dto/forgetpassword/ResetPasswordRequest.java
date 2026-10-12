package com.blood_donor.model.dto.forgetpassword;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    @NotBlank(message="Reset token is required")
    private @NotBlank(message="Reset token is required") String token;
    @NotBlank(message="New password is required")
    @Size(min=8, message="Password must be at least 8 characters")
    @jakarta.validation.constraints.Pattern(regexp="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,}$", message="Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String newPassword;

    public String getToken() {
        return this.token;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ResetPasswordRequest)) {
            return false;
        }
        ResetPasswordRequest other = (ResetPasswordRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$token = this.getToken();
        String other$token = other.getToken();
        if (this$token == null ? other$token != null : !this$token.equals(other$token)) {
            return false;
        }
        String this$newPassword = this.getNewPassword();
        String other$newPassword = other.getNewPassword();
        return !(this$newPassword == null ? other$newPassword != null : !this$newPassword.equals(other$newPassword));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ResetPasswordRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $token = this.getToken();
        result = result * 59 + ($token == null ? 43 : $token.hashCode());
        String $newPassword = this.getNewPassword();
        result = result * 59 + ($newPassword == null ? 43 : $newPassword.hashCode());
        return result;
    }

    public String toString() {
        return "ResetPasswordRequest(token=" + this.getToken() + ", newPassword=" + this.getNewPassword() + ")";
    }
}

