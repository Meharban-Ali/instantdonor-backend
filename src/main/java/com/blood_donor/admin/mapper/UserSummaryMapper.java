/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.admin.mapper;

import com.blood_donor.admin.dto.DonorSummaryDto;
import com.blood_donor.admin.dto.UserSummaryDto;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserSummaryMapper {
    public UserSummaryDto toUserSummary(User user) {
        UserSummaryDto.UserSummaryDtoBuilder builder = UserSummaryDto.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).phoneMasked(this.maskPhone(user.getPhone())).roles(user.getRoles()).isActive(user.isActive()).createdAt(user.getCreatedAt()).lastUpdatedAt(user.getUpdatedAt());
        DonorProfile donor = user.getDonorProfile();
        if (donor != null) {
            builder.bloodGroup(donor.getBloodGroup()).city(donor.getCity()).isDonorAvailable(donor.isAvailable()).totalDonations(donor.getTotalDonations()).reliabilityScore(donor.getReliabilityScore());
        }
        return builder.build();
    }

    public DonorSummaryDto toDonorSummary(DonorProfile donor) {
        User user = donor.getUser();
        return DonorSummaryDto.builder().donorId(donor.getId()).userId(user.getId()).name(user.getName()).email(user.getEmail()).phoneMasked(this.maskPhone(user.getPhone())).bloodGroup(donor.getBloodGroup()).bloodGroupDisplay(donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayName() : null).city(donor.getCity()).isAvailable(donor.isAvailable()).lastDonationDate(donor.getLastDonationDate()).totalDonations(donor.getTotalDonations()).reliabilityScore(donor.getReliabilityScore()).responseRate(donor.getResponseRate()).hasLocation(donor.getLatitude() != null && donor.getLongitude() != null).registeredAt(donor.getCreatedAt()).lastUpdatedAt(donor.getUpdatedAt()).build();
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 6) {
            return "****";
        }
        return phone.substring(0, 2) + "*".repeat(phone.length() - 6) + phone.substring(phone.length() - 4);
    }
}

