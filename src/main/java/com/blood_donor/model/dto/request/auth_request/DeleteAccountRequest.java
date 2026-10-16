package com.blood_donor.model.dto.request.auth_request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class DeleteAccountRequest {
    @NotBlank(message = "Password is required for account deletion")
    @JsonAlias({"currentPassword"})
    private String password;

    public DeleteAccountRequest() {}

    public DeleteAccountRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurrentPassword(String currentPassword) {
        this.password = currentPassword;
    }
}
