package com.blood_donor.integration;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Real MySQL 8 Donor Eligibility & Compatibility Tests")
public class RealDatabaseDonorEligibilityTest extends BaseIntegrationTest {

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    private User createUser(String email, String phone) {
        return userRepository.save(User.builder()
                .name("Eligibility User")
                .email(email)
                .phone(phone)
                .password("$2a$12$e8J4V07V77v6d7p2q5k7te1V5zL8Zt2X0W2j1Y8m5k2p3q4r5s6t7u")
                .roles(Collections.singleton(Role.DONOR))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    private DonorProfile createDonor(User user, BloodGroup bg, LocalDate lastDonation) {
        return donorProfileRepository.save(DonorProfile.builder()
                .user(user)
                .bloodGroup(bg)
                .city("Delhi")
                .state("Delhi")
                .latitude(BigDecimal.valueOf(28.6139))
                .longitude(BigDecimal.valueOf(77.2090))
                .isAvailable(true)
                .lastDonationDate(lastDonation)
                .reliabilityScore(BigDecimal.valueOf(90.0))
                .responseRate(BigDecimal.valueOf(85.0))
                .totalDonations(lastDonation == null ? 0 : 2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("Eligibility Check: Donors with no prior donations or > 90 days interval are eligible")
    void testDonorEligibilityQueriesOnMySql() {
        LocalDate threshold = LocalDate.now().minusDays(90);

        // Donor 1: Never donated (null) -> eligible
        User u1 = createUser("elig1@test.com", "9300000001");
        DonorProfile dp1 = createDonor(u1, BloodGroup.O_POSITIVE, null);

        // Donor 2: Donated 100 days ago -> eligible
        User u2 = createUser("elig2@test.com", "9300000002");
        DonorProfile dp2 = createDonor(u2, BloodGroup.A_POSITIVE, LocalDate.now().minusDays(100));

        // Donor 3: Donated 30 days ago (< 90 days gap) -> NOT eligible
        User u3 = createUser("inelig@test.com", "9300000003");
        DonorProfile dp3 = createDonor(u3, BloodGroup.B_POSITIVE, LocalDate.now().minusDays(30));

        boolean isDp1Eligible = donorProfileRepository.isDonorEligible(dp1.getId(), threshold);
        boolean isDp2Eligible = donorProfileRepository.isDonorEligible(dp2.getId(), threshold);
        boolean isDp3Eligible = donorProfileRepository.isDonorEligible(dp3.getId(), threshold);

        assertThat(isDp1Eligible).isTrue();
        assertThat(isDp2Eligible).isTrue();
        assertThat(isDp3Eligible).isFalse();

        List<DonorProfile> eligibleList = donorProfileRepository.findEligibleDonors(threshold);
        assertThat(eligibleList).hasSize(2);
        List<Long> eligibleIds = eligibleList.stream().map(DonorProfile::getId).toList();
        assertThat(eligibleIds).containsExactlyInAnyOrder(dp1.getId(), dp2.getId());

        long countEligible = donorProfileRepository.countEligibleDonors(threshold);
        assertThat(countEligible).isEqualTo(2);
    }

    @Test
    @DisplayName("Blood Group Compatibility Matrix Verification")
    void testBloodGroupCompatibilityRules() {
        // Universal donor O- can donate to all groups
        List<BloodGroup> oNegCanDonateTo = BloodGroup.getCompatibleRecipients(BloodGroup.O_NEGATIVE);
        assertThat(oNegCanDonateTo).containsExactlyInAnyOrder(
                BloodGroup.O_NEGATIVE, BloodGroup.O_POSITIVE,
                BloodGroup.A_NEGATIVE, BloodGroup.A_POSITIVE,
                BloodGroup.B_NEGATIVE, BloodGroup.B_POSITIVE,
                BloodGroup.AB_NEGATIVE, BloodGroup.AB_POSITIVE
        );

        // Universal recipient AB+ can receive from all groups
        List<BloodGroup> abPosCanReceiveFrom = BloodGroup.getCompatibleDonors(BloodGroup.AB_POSITIVE);
        assertThat(abPosCanReceiveFrom).containsExactlyInAnyOrder(
                BloodGroup.O_NEGATIVE, BloodGroup.O_POSITIVE,
                BloodGroup.A_NEGATIVE, BloodGroup.A_POSITIVE,
                BloodGroup.B_NEGATIVE, BloodGroup.B_POSITIVE,
                BloodGroup.AB_NEGATIVE, BloodGroup.AB_POSITIVE
        );

        // Compatible donors for A+ patient
        List<BloodGroup> compatibleForAPos = BloodGroup.getCompatibleDonors(BloodGroup.A_POSITIVE);
        assertThat(compatibleForAPos).containsExactlyInAnyOrder(
                BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
                BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE
        );
    }

    @Test
    @DisplayName("updateAfterDonation updates last donation date, increments count, updates timestamp")
    void testUpdateAfterDonation() {
        User u = createUser("after_don@test.com", "9300000010");
        DonorProfile dp = createDonor(u, BloodGroup.AB_NEGATIVE, null);
        assertThat(dp.getTotalDonations()).isEqualTo(0);

        LocalDate donationDate = LocalDate.now();
        LocalDateTime updateTime = LocalDateTime.now();

        int rows = donorProfileRepository.updateAfterDonation(dp.getId(), donationDate, updateTime);
        assertThat(rows).isEqualTo(1);

        DonorProfile refreshed = donorProfileRepository.findById(dp.getId()).orElseThrow();
        assertThat(refreshed.getLastDonationDate()).isEqualTo(donationDate);
        assertThat(refreshed.getTotalDonations()).isEqualTo(1);
    }
}
