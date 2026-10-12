package com.blood_donor.model.dto.request.auth_request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message="Name is required")
    @Size(min=3, max=100, message="Name must be 3-100 characters")
    private @NotBlank(message="Name is required") @Size(min=3, max=100, message="Name must be 3-100 characters") String name;
    @NotBlank(message="Email is required")
    @Email(message="Invalid email format")
    private @NotBlank(message="Email is required") @Email(message="Invalid email format") String email;
    @NotBlank(message="Password is required")
    @Size(min=8, message="Password must be at least 8 characters")
    private @NotBlank(message="Password is required") @Size(min=8, message="Password must be at least 8 characters") String password;
    @NotBlank(message="Mobile number is required")
    @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid mobile number")
    private @NotBlank(message="Mobile number is required") @Pattern(regexp="^[+]?[0-9]{10,15}$", message="Invalid mobile number") String phone;

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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RegisterRequest)) {
            return false;
        }
        RegisterRequest other = (RegisterRequest)o;
        if (!other.canEqual(this)) {
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
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$phone = this.getPhone();
        String other$phone = other.getPhone();
        return !(this$phone == null ? other$phone != null : !this$phone.equals(other$phone));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RegisterRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        return result;
    }

    public String toString() {
        return "RegisterRequest(name=" + this.getName() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", phone=" + this.getPhone() + ")";
    }
}

