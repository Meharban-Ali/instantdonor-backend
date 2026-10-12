package com.blood_donor.integration;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.model.dto.request.donor_request.DonorSearchRequest;
import com.blood_donor.model.dto.request.donor_request.RadiusSearchRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.user_repository.UserRepository;
import com.blood_donor.service.donor_service_impl.DonorSearchService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Real MySQL 8 Haversine & Geo-Search Integration Tests")
public class RealDatabaseGeoSearchTest extends BaseIntegrationTest {

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonorSearchService donorSearchService;

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        cleanDatabase();
    }

    private User createUser(String email, String phone) {
        return userRepository.save(User.builder()
                .name("Test User " + email)
                .email(email)
                .phone(phone)
                .password("$2a$12$e8J4V07V77v6d7p2q5k7te1V5zL8Zt2X0W2j1Y8m5k2p3q4r5s6t7u")
                .roles(Collections.singleton(Role.DONOR))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    private DonorProfile createDonor(User user, BloodGroup bg, String city, double lat, double lng, boolean available) {
        return donorProfileRepository.save(DonorProfile.builder()
                .user(user)
                .bloodGroup(bg)
                .city(city)
                .state("State")
                .latitude(BigDecimal.valueOf(lat))
                .longitude(BigDecimal.valueOf(lng))
                .isAvailable(available)
                .reliabilityScore(BigDecimal.valueOf(95.0))
                .responseRate(BigDecimal.valueOf(90.0))
                .totalDonations(5)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
    }

    @Test
    @DisplayName("MySQL Native Haversine calculateDistanceKm accuracy test")
    void testHaversineDistanceAccuracyInMySql() {
        // Delhi: 28.6139, 77.2090
        // Noida: 28.5355, 77.3910
        // Expected distance: ~19.8 km
        Double distance = donorProfileRepository.calculateDistanceKm(28.6139, 77.2090, 28.5355, 77.3910);
        assertThat(distance).isNotNull();
        assertThat(distance).isCloseTo(19.8, within(1.5));
    }

    @Test
    @DisplayName("Radius Search: Returns nearby donors within radius, excludes far donors (> radius)")
    void testRadiusSearchIncludesNearbyAndExcludesFarDonors() {
        User user1 = createUser("delhi_donor@test.com", "9111111111");
        createDonor(user1, BloodGroup.O_POSITIVE, "Delhi", 28.6139, 77.2090, true);

        User user2 = createUser("noida_donor@test.com", "9222222222");
        createDonor(user2, BloodGroup.O_POSITIVE, "Noida", 28.5355, 77.3910, true);

        User user3 = createUser("jaipur_donor@test.com", "9333333333");
        // Jaipur: 26.9124, 75.7873 (approx 240km away from Delhi)
        createDonor(user3, BloodGroup.O_POSITIVE, "Jaipur", 26.9124, 75.7873, true);

        DonorSearchRequest request = new DonorSearchRequest();
        request.setBloodGroup(BloodGroup.O_POSITIVE);
        RadiusSearchRequest radiusReq = new RadiusSearchRequest();
        radiusReq.setLatitude(28.6139);
        radiusReq.setLongitude(77.2090);
        radiusReq.setRadiusKm(25.0); // 25km radius from central Delhi
        request.setRadiusSearch(radiusReq);

        List<DonorResponse> results = donorSearchService.searchByLocation(request);

        // Expect Delhi donor (~0km) and Noida donor (~20km) to be found, Jaipur excluded!
        assertThat(results).hasSize(2);
        List<String> foundCities = results.stream().map(r -> r.getLocation().getCity()).toList();
        assertThat(foundCities).containsExactlyInAnyOrder("Delhi", "Noida");
    }

    @Test
    @DisplayName("Unavailable donors are excluded from nearby search")
    void testUnavailableDonorsExcludedFromGeoSearch() {
        User user = createUser("unavail_donor@test.com", "9444444444");
        createDonor(user, BloodGroup.A_POSITIVE, "Delhi", 28.6139, 77.2090, false);

        DonorSearchRequest request = new DonorSearchRequest();
        request.setBloodGroup(BloodGroup.A_POSITIVE);
        RadiusSearchRequest radiusReq = new RadiusSearchRequest();
        radiusReq.setLatitude(28.6139);
        radiusReq.setLongitude(77.2090);
        radiusReq.setRadiusKm(50.0);
        request.setRadiusSearch(radiusReq);

        List<DonorResponse> results = donorSearchService.searchByLocation(request);
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Radius cap enforcement: Radius > 100km violates validation constraints")
    void testRadiusCapValidation() {
        RadiusSearchRequest radiusReq = new RadiusSearchRequest();
        radiusReq.setLatitude(28.6139);
        radiusReq.setLongitude(77.2090);
        radiusReq.setRadiusKm(150.0); // Exceeds 100.0 max

        Set<ConstraintViolation<RadiusSearchRequest>> violations = validator.validate(radiusReq);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getMessage().contains("Radius must be <= 100 km"));
    }

    @Test
    @DisplayName("Boundary validation: Latitude outside [-90, 90] and Longitude outside [-180, 180] rejected")
    void testCoordinateBoundaryValidation() {
        RadiusSearchRequest invalidLat = new RadiusSearchRequest();
        invalidLat.setLatitude(95.0);
        invalidLat.setLongitude(77.0);
        invalidLat.setRadiusKm(10.0);

        Set<ConstraintViolation<RadiusSearchRequest>> latViolations = validator.validate(invalidLat);
        assertThat(latViolations).anyMatch(v -> v.getMessage().contains("Latitude must be <= 90.0"));

        RadiusSearchRequest invalidLng = new RadiusSearchRequest();
        invalidLng.setLatitude(28.0);
        invalidLng.setLongitude(195.0);
        invalidLng.setRadiusKm(10.0);

        Set<ConstraintViolation<RadiusSearchRequest>> lngViolations = validator.validate(invalidLng);
        assertThat(lngViolations).anyMatch(v -> v.getMessage().contains("Longitude must be <= 180.0"));
    }
}
