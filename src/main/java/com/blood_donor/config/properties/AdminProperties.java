/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Email
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.Pattern
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 *  org.springframework.validation.annotation.Validated
 */
package com.blood_donor.config.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@ConfigurationProperties(prefix="app.admin")
public class AdminProperties {
    @NotBlank(message="Admin name is required")
    private @NotBlank(message="Admin name is required") String name = "Super Admin";
    @NotBlank(message="Admin email is required")
    @Email(message="Admin email must be valid")
    private @NotBlank(message="Admin email is required") @Email(message="Admin email must be valid") String email = "admin@blooddonor.com";
    @NotBlank(message="Admin phone is required")
    @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid admin phone number")
    private @NotBlank(message="Admin phone is required") @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid admin phone number") String phone = "9999999999";
    private String password = "";

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getPassword() {
        return this.password;
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

    public void setPassword(String password) {
        this.password = password;
    }
}

