package com.blood_donor.model.dto.response.donor_response;

public class UserSummary {
    private Long userId;
    private String name;
    private String phone;

    public static UserSummaryBuilder builder() {
        return new UserSummaryBuilder();
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        if (!(o instanceof UserSummary)) {
            return false;
        }
        UserSummary other = (UserSummary)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$userId = this.getUserId();
        Long other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !((Object)this$userId).equals(other$userId)) {
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
        return other instanceof UserSummary;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : ((Object)$userId).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        return result;
    }

    public String toString() {
        return "UserSummary(userId=" + this.getUserId() + ", name=" + this.getName() + ", phone=" + this.getPhone() + ")";
    }

    public UserSummary() {
    }

    public UserSummary(Long userId, String name, String phone) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
    }

    public static class UserSummaryBuilder {
        private Long userId;
        private String name;
        private String phone;

        UserSummaryBuilder() {
        }

        public UserSummaryBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserSummaryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserSummaryBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserSummary build() {
            return new UserSummary(this.userId, this.name, this.phone);
        }

        public String toString() {
            return "UserSummary.UserSummaryBuilder(userId=" + this.userId + ", name=" + this.name + ", phone=" + this.phone + ")";
        }
    }
}

