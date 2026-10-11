/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.validation.constraints.Pattern
 *  jakarta.validation.constraints.Size
 */
package com.blood_donor.model.dto.request.update_profile;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(min=2, max=100, message="Name must be 2-100 characters")
    private @Size(min=2, max=100, message="Name must be 2-100 characters") String name;
    @Pattern(regexp="^[0-9]{10}$", message="Phone must be exactly 10 digits")
    private @Pattern(regexp="^[0-9]{10}$", message="Phone must be exactly 10 digits") String phone;

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UpdateProfileRequest)) {
            return false;
        }
        UpdateProfileRequest other = (UpdateProfileRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        return !(this$phone == null ? other$phone != null : !this$phone.equals(other$phone));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UpdateProfileRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        return result;
    }

    public String toString() {
        return "UpdateProfileRequest(name=" + this.getName() + ", phone=" + this.getPhone() + ")";
    }
}

