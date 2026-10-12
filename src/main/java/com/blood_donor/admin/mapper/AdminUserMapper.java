package com.blood_donor.admin.mapper;

import com.blood_donor.admin.dto.DonorSummaryDto;
import com.blood_donor.admin.dto.UserSummaryDto;
import com.blood_donor.common.enums.Role;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class AdminUserMapper {
    public UserSummaryDto toUserSummary(User user) {
        UserSummaryDto.UserSummaryDtoBuilder builder = UserSummaryDto.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).phoneMasked(this.maskPhone(user.getPhone())).roles(user.getRoles()).isActive(user.isActive()).createdAt(user.getCreatedAt()).lastUpdatedAt(user.getUpdatedAt());
        if (user.getRoles() != null && user.getRoles().contains((Object)Role.DONOR) && user.getDonorProfile() != null) {
            DonorProfile donor = user.getDonorProfile();
            builder.bloodGroup(donor.getBloodGroup()).city(donor.getCity()).isDonorAvailable(donor.isAvailable()).totalDonations(donor.getTotalDonations()).reliabilityScore(donor.getReliabilityScore() != null ? donor.getReliabilityScore() : BigDecimal.ZERO);
        }
        return builder.build();
    }

    public DonorSummaryDto toDonorSummary(DonorProfile donor) {
        User user = donor.getUser();
        return DonorSummaryDto.builder().donorId(donor.getId()).userId(user.getId()).name(user.getName()).email(user.getEmail()).phoneMasked(this.maskPhone(user.getPhone())).bloodGroup(donor.getBloodGroup()).bloodGroupDisplay(donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayName() : null).city(donor.getCity()).isAvailable(donor.isAvailable()).lastDonationDate(donor.getLastDonationDate()).totalDonations(donor.getTotalDonations()).reliabilityScore(donor.getReliabilityScore() != null ? donor.getReliabilityScore() : BigDecimal.ZERO).responseRate(donor.getResponseRate() != null ? donor.getResponseRate() : BigDecimal.ZERO).hasLocation(donor.getLatitude() != null && donor.getLongitude() != null).registeredAt(donor.getCreatedAt()).lastUpdatedAt(donor.getUpdatedAt()).build();
    }

    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) {
            return "****";
        }
        return phone.substring(0, 2) + "*".repeat(phone.length() - 6) + phone.substring(phone.length() - 4);
    }
}

