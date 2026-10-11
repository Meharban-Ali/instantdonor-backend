/*
 * Decompiled with CFR 0.152.
 */
package com.blood_donor.model.dto.response.auth_response;

import com.blood_donor.common.enums.Role;
import java.util.Set;

public class AuthResponse {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String name;
    private String email;
    private Set<Role> roles;
    private Long expiresIn;

    private static String $default$tokenType() {
        return "Bearer";
    }

    AuthResponse(Long userId, String accessToken, String refreshToken, String tokenType, String name, String email, Set<Role> roles, Long expiresIn) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.expiresIn = expiresIn;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public Long getExpiresIn() {
        return this.expiresIn;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuthResponse)) {
            return false;
        }
        AuthResponse other = (AuthResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$userId = this.getUserId();
        Long other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !((Object)this$userId).equals(other$userId)) {
            return false;
        }
        Long this$expiresIn = this.getExpiresIn();
        Long other$expiresIn = other.getExpiresIn();
        if (this$expiresIn == null ? other$expiresIn != null : !((Object)this$expiresIn).equals(other$expiresIn)) {
            return false;
        }
        String this$accessToken = this.getAccessToken();
        String other$accessToken = other.getAccessToken();
        if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) {
            return false;
        }
        String this$refreshToken = this.getRefreshToken();
        String other$refreshToken = other.getRefreshToken();
        if (this$refreshToken == null ? other$refreshToken != null : !this$refreshToken.equals(other$refreshToken)) {
            return false;
        }
        String this$tokenType = this.getTokenType();
        String other$tokenType = other.getTokenType();
        if (this$tokenType == null ? other$tokenType != null : !this$tokenType.equals(other$tokenType)) {
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
        Set<Role> this$roles = this.getRoles();
        Set<Role> other$roles = other.getRoles();
        return !(this$roles == null ? other$roles != null : !((Object)this$roles).equals(other$roles));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuthResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        Long $expiresIn = this.getExpiresIn();
        result = result * 59 + ($expiresIn == null ? 43 : ((Object)$expiresIn).hashCode());
        String $accessToken = this.getAccessToken();
        result = result * 59 + ($accessToken == null ? 43 : $accessToken.hashCode());
        String $refreshToken = this.getRefreshToken();
        result = result * 59 + ($refreshToken == null ? 43 : $refreshToken.hashCode());
        String $tokenType = this.getTokenType();
        result = result * 59 + ($tokenType == null ? 43 : $tokenType.hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Set<Role> $roles = this.getRoles();
        result = result * 59 + ($roles == null ? 43 : ((Object)$roles).hashCode());
        return result;
    }

    public String toString() {
        return "AuthResponse(userId=" + this.getUserId() + ", accessToken=" + this.getAccessToken() + ", refreshToken=" + this.getRefreshToken() + ", tokenType=" + this.getTokenType() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", roles=" + this.getRoles() + ", expiresIn=" + this.getExpiresIn() + ")";
    }

    public static class AuthResponseBuilder {
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private boolean tokenType$set;
        private String tokenType$value;
        private String name;
        private String email;
        private Set<Role> roles;
        private Long expiresIn;

        AuthResponseBuilder() {
        }

        public AuthResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public AuthResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public AuthResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public AuthResponseBuilder tokenType(String tokenType) {
            this.tokenType$value = tokenType;
            this.tokenType$set = true;
            return this;
        }

        public AuthResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AuthResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AuthResponseBuilder roles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public AuthResponseBuilder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public AuthResponse build() {
            String tokenType$value = this.tokenType$value;
            if (!this.tokenType$set) {
                tokenType$value = AuthResponse.$default$tokenType();
            }
            return new AuthResponse(this.userId, this.accessToken, this.refreshToken, tokenType$value, this.name, this.email, this.roles, this.expiresIn);
        }

        public String toString() {
            return "AuthResponse.AuthResponseBuilder(userId=" + this.userId + ", accessToken=" + this.accessToken + ", refreshToken=" + this.refreshToken + ", tokenType$value=" + this.tokenType$value + ", name=" + this.name + ", email=" + this.email + ", roles=" + this.roles + ", expiresIn=" + this.expiresIn + ")";
        }
    }
}

