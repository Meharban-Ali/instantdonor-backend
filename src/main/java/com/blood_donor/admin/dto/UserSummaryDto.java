package com.blood_donor.admin.dto;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=JsonInclude.Include.NON_NULL)
public class UserSummaryDto {
    private Long id;
    private String name;
    private String email;
    private String phoneMasked;
    private Set<Role> roles;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private LocalDateTime lastLoginAt;
    private BloodGroup bloodGroup;
    private String city;
    private Boolean isDonorAvailable;
    private Integer totalDonations;
    private BigDecimal reliabilityScore;
    private Integer requestsCreated;

    public String getPrimaryRole() {
        if (this.roles == null || this.roles.isEmpty()) {
            return "USER";
        }
        if (this.roles.contains((Object)Role.ADMIN)) {
            return "ADMIN";
        }
        if (this.roles.contains((Object)Role.HOSPITAL)) {
            return "HOSPITAL";
        }
        if (this.roles.contains((Object)Role.DONOR)) {
            return "DONOR";
        }
        return "USER";
    }

    public boolean isDonor() {
        return this.roles != null && this.roles.contains((Object)Role.DONOR);
    }

    public String getReliabilityLevel() {
        if (this.reliabilityScore == null) {
            return "UNKNOWN";
        }
        if (this.reliabilityScore.compareTo(BigDecimal.valueOf(80L)) >= 0) {
            return "HIGH";
        }
        if (this.reliabilityScore.compareTo(BigDecimal.valueOf(50L)) >= 0) {
            return "MEDIUM";
        }
        return "LOW";
    }

    public static UserSummaryDtoBuilder builder() {
        return new UserSummaryDtoBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneMasked() {
        return this.phoneMasked;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return this.lastLoginAt;
    }

    public BloodGroup getBloodGroup() {
        return this.bloodGroup;
    }

    public String getCity() {
        return this.city;
    }

    public Boolean getIsDonorAvailable() {
        return this.isDonorAvailable;
    }

    public Integer getTotalDonations() {
        return this.totalDonations;
    }

    public BigDecimal getReliabilityScore() {
        return this.reliabilityScore;
    }

    public Integer getRequestsCreated() {
        return this.requestsCreated;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneMasked(String phoneMasked) {
        this.phoneMasked = phoneMasked;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setIsDonorAvailable(Boolean isDonorAvailable) {
        this.isDonorAvailable = isDonorAvailable;
    }

    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }

    public void setRequestsCreated(Integer requestsCreated) {
        this.requestsCreated = requestsCreated;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserSummaryDto)) {
            return false;
        }
        UserSummaryDto other = (UserSummaryDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Boolean this$isActive = this.getIsActive();
        Boolean other$isActive = other.getIsActive();
        if (this$isActive == null ? other$isActive != null : !((Object)this$isActive).equals(other$isActive)) {
            return false;
        }
        Boolean this$isDonorAvailable = this.getIsDonorAvailable();
        Boolean other$isDonorAvailable = other.getIsDonorAvailable();
        if (this$isDonorAvailable == null ? other$isDonorAvailable != null : !((Object)this$isDonorAvailable).equals(other$isDonorAvailable)) {
            return false;
        }
        Integer this$totalDonations = this.getTotalDonations();
        Integer other$totalDonations = other.getTotalDonations();
        if (this$totalDonations == null ? other$totalDonations != null : !((Object)this$totalDonations).equals(other$totalDonations)) {
            return false;
        }
        Integer this$requestsCreated = this.getRequestsCreated();
        Integer other$requestsCreated = other.getRequestsCreated();
        if (this$requestsCreated == null ? other$requestsCreated != null : !((Object)this$requestsCreated).equals(other$requestsCreated)) {
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
        String this$phoneMasked = this.getPhoneMasked();
        String other$phoneMasked = other.getPhoneMasked();
        if (this$phoneMasked == null ? other$phoneMasked != null : !this$phoneMasked.equals(other$phoneMasked)) {
            return false;
        }
        Set<Role> this$roles = this.getRoles();
        Set<Role> other$roles = other.getRoles();
        if (this$roles == null ? other$roles != null : !((Object)this$roles).equals(other$roles)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$lastUpdatedAt = this.getLastUpdatedAt();
        LocalDateTime other$lastUpdatedAt = other.getLastUpdatedAt();
        if (this$lastUpdatedAt == null ? other$lastUpdatedAt != null : !((Object)this$lastUpdatedAt).equals(other$lastUpdatedAt)) {
            return false;
        }
        LocalDateTime this$lastLoginAt = this.getLastLoginAt();
        LocalDateTime other$lastLoginAt = other.getLastLoginAt();
        if (this$lastLoginAt == null ? other$lastLoginAt != null : !((Object)this$lastLoginAt).equals(other$lastLoginAt)) {
            return false;
        }
        BloodGroup this$bloodGroup = this.getBloodGroup();
        BloodGroup other$bloodGroup = other.getBloodGroup();
        if (this$bloodGroup == null ? other$bloodGroup != null : !((Object)((Object)this$bloodGroup)).equals((Object)other$bloodGroup)) {
            return false;
        }
        String this$city = this.getCity();
        String other$city = other.getCity();
        if (this$city == null ? other$city != null : !this$city.equals(other$city)) {
            return false;
        }
        BigDecimal this$reliabilityScore = this.getReliabilityScore();
        BigDecimal other$reliabilityScore = other.getReliabilityScore();
        return !(this$reliabilityScore == null ? other$reliabilityScore != null : !((Object)this$reliabilityScore).equals(other$reliabilityScore));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserSummaryDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Boolean $isActive = this.getIsActive();
        result = result * 59 + ($isActive == null ? 43 : ((Object)$isActive).hashCode());
        Boolean $isDonorAvailable = this.getIsDonorAvailable();
        result = result * 59 + ($isDonorAvailable == null ? 43 : ((Object)$isDonorAvailable).hashCode());
        Integer $totalDonations = this.getTotalDonations();
        result = result * 59 + ($totalDonations == null ? 43 : ((Object)$totalDonations).hashCode());
        Integer $requestsCreated = this.getRequestsCreated();
        result = result * 59 + ($requestsCreated == null ? 43 : ((Object)$requestsCreated).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $phoneMasked = this.getPhoneMasked();
        result = result * 59 + ($phoneMasked == null ? 43 : $phoneMasked.hashCode());
        Set<Role> $roles = this.getRoles();
        result = result * 59 + ($roles == null ? 43 : ((Object)$roles).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $lastUpdatedAt = this.getLastUpdatedAt();
        result = result * 59 + ($lastUpdatedAt == null ? 43 : ((Object)$lastUpdatedAt).hashCode());
        LocalDateTime $lastLoginAt = this.getLastLoginAt();
        result = result * 59 + ($lastLoginAt == null ? 43 : ((Object)$lastLoginAt).hashCode());
        BloodGroup $bloodGroup = this.getBloodGroup();
        result = result * 59 + ($bloodGroup == null ? 43 : ((Object)((Object)$bloodGroup)).hashCode());
        String $city = this.getCity();
        result = result * 59 + ($city == null ? 43 : $city.hashCode());
        BigDecimal $reliabilityScore = this.getReliabilityScore();
        result = result * 59 + ($reliabilityScore == null ? 43 : ((Object)$reliabilityScore).hashCode());
        return result;
    }

    public String toString() {
        return "UserSummaryDto(id=" + this.getId() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", phoneMasked=" + this.getPhoneMasked() + ", roles=" + this.getRoles() + ", isActive=" + this.getIsActive() + ", createdAt=" + this.getCreatedAt() + ", lastUpdatedAt=" + this.getLastUpdatedAt() + ", lastLoginAt=" + this.getLastLoginAt() + ", bloodGroup=" + this.getBloodGroup() + ", city=" + this.getCity() + ", isDonorAvailable=" + this.getIsDonorAvailable() + ", totalDonations=" + this.getTotalDonations() + ", reliabilityScore=" + this.getReliabilityScore() + ", requestsCreated=" + this.getRequestsCreated() + ")";
    }

    public UserSummaryDto() {
    }

    public UserSummaryDto(Long id, String name, String email, String phoneMasked, Set<Role> roles, Boolean isActive, LocalDateTime createdAt, LocalDateTime lastUpdatedAt, LocalDateTime lastLoginAt, BloodGroup bloodGroup, String city, Boolean isDonorAvailable, Integer totalDonations, BigDecimal reliabilityScore, Integer requestsCreated) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneMasked = phoneMasked;
        this.roles = roles;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.lastLoginAt = lastLoginAt;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.isDonorAvailable = isDonorAvailable;
        this.totalDonations = totalDonations;
        this.reliabilityScore = reliabilityScore;
        this.requestsCreated = requestsCreated;
    }

    public static class UserSummaryDtoBuilder {
        private Long id;
        private String name;
        private String email;
        private String phoneMasked;
        private Set<Role> roles;
        private Boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime lastUpdatedAt;
        private LocalDateTime lastLoginAt;
        private BloodGroup bloodGroup;
        private String city;
        private Boolean isDonorAvailable;
        private Integer totalDonations;
        private BigDecimal reliabilityScore;
        private Integer requestsCreated;

        UserSummaryDtoBuilder() {
        }

        public UserSummaryDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSummaryDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserSummaryDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserSummaryDtoBuilder phoneMasked(String phoneMasked) {
            this.phoneMasked = phoneMasked;
            return this;
        }

        public UserSummaryDtoBuilder roles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public UserSummaryDtoBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserSummaryDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserSummaryDtoBuilder lastUpdatedAt(LocalDateTime lastUpdatedAt) {
            this.lastUpdatedAt = lastUpdatedAt;
            return this;
        }

        public UserSummaryDtoBuilder lastLoginAt(LocalDateTime lastLoginAt) {
            this.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserSummaryDtoBuilder bloodGroup(BloodGroup bloodGroup) {
            this.bloodGroup = bloodGroup;
            return this;
        }

        public UserSummaryDtoBuilder city(String city) {
            this.city = city;
            return this;
        }

        public UserSummaryDtoBuilder isDonorAvailable(Boolean isDonorAvailable) {
            this.isDonorAvailable = isDonorAvailable;
            return this;
        }

        public UserSummaryDtoBuilder totalDonations(Integer totalDonations) {
            this.totalDonations = totalDonations;
            return this;
        }

        public UserSummaryDtoBuilder reliabilityScore(BigDecimal reliabilityScore) {
            this.reliabilityScore = reliabilityScore;
            return this;
        }

        public UserSummaryDtoBuilder requestsCreated(Integer requestsCreated) {
            this.requestsCreated = requestsCreated;
            return this;
        }

        public UserSummaryDto build() {
            return new UserSummaryDto(this.id, this.name, this.email, this.phoneMasked, this.roles, this.isActive, this.createdAt, this.lastUpdatedAt, this.lastLoginAt, this.bloodGroup, this.city, this.isDonorAvailable, this.totalDonations, this.reliabilityScore, this.requestsCreated);
        }

        public String toString() {
            return "UserSummaryDto.UserSummaryDtoBuilder(id=" + this.id + ", name=" + this.name + ", email=" + this.email + ", phoneMasked=" + this.phoneMasked + ", roles=" + this.roles + ", isActive=" + this.isActive + ", createdAt=" + this.createdAt + ", lastUpdatedAt=" + this.lastUpdatedAt + ", lastLoginAt=" + this.lastLoginAt + ", bloodGroup=" + this.bloodGroup + ", city=" + this.city + ", isDonorAvailable=" + this.isDonorAvailable + ", totalDonations=" + this.totalDonations + ", reliabilityScore=" + this.reliabilityScore + ", requestsCreated=" + this.requestsCreated + ")";
        }
    }
}

