/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.NotBlank
 */
package com.blood_donor.model.dto.request.auth_request;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
    @NotBlank(message="Refresh token is required")
    private @NotBlank(message="Refresh token is required") String refreshToken;

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RefreshTokenRequest)) {
            return false;
        }
        RefreshTokenRequest other = (RefreshTokenRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$refreshToken = this.getRefreshToken();
        String other$refreshToken = other.getRefreshToken();
        return !(this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RefreshTokenRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $refreshToken = this.getRefreshToken();
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        return result;
    }

    public String toString() {
        return "RefreshTokenRequest(refreshToken=" + this.getRefreshToken() + ")";
    }
}

