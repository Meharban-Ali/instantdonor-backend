package com.blood_donor.service.mapper;

import com.blood_donor.model.dto.response.donor_response.BloodInfo;
import com.blood_donor.model.dto.response.donor_response.DonationStats;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.dto.response.donor_response.LocationInfo;
import com.blood_donor.model.dto.response.donor_response.UserSummary;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DonorMapper {
    public DonorResponse toResponse(DonorProfile profile, Double distanceKm) {
        return this.toResponse(profile, distanceKm, false);
    }

    public DonorResponse toResponse(DonorProfile profile, Double distanceKm, boolean maskPhone) {
        User user = profile.getUser();
        String phone = user.getPhone();
        if (maskPhone && phone != null && phone.length() >= 6) {
            phone = phone.substring(0, 2) + "*".repeat(phone.length() - 6) + phone.substring(phone.length() - 4);
        }
        return DonorResponse.builder()
                .donorId(profile.getId())
                .user(UserSummary.builder().userId(user.getId()).name(user.getName()).phone(phone).build())
                .blood(BloodInfo.builder().bloodGroup(profile.getBloodGroup()).bloodGroupDisplay(profile.getBloodGroup().getDisplayName()).build())
                .location(LocationInfo.builder().city(profile.getCity()).state(profile.getState()).latitude(profile.getLatitude()).longitude(profile.getLongitude()).build())
                .donationStats(DonationStats.builder().totalDonations(profile.getTotalDonations()).reliabilityScore(profile.getReliabilityScore()).responseRate(profile.getResponseRate()).lastDonationDate(profile.getLastDonationDate()).nextEligibleDate(profile.getNextEligibleDate()).isEligible(profile.isEligibleToDonate()).build())
                .isAvailable(profile.isAvailable())
                .distanceKm(distanceKm != null ? Double.valueOf((double)Math.round(distanceKm * 10.0) / 10.0) : null)
                .smsAlertsEnabled(profile.isSmsAlertsEnabled())
                .emailAlertsEnabled(profile.isEmailAlertsEnabled())
                .consentUpdatedAt(profile.getConsentUpdatedAt())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}

