package com.blood_donor.controller.donor_controller;

import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.request.donor_request.DonorRegisterRequest;
import com.blood_donor.model.dto.request.donor_request.DonorSearchRequest;
import com.blood_donor.model.dto.request.donor_request.DonorUpdateRequest;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import com.blood_donor.model.dto.response.donor_response.DonationHistoryDto;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.dto.response.donor_response.EligibilityResponse;
import com.blood_donor.model.entity.DonationHistory;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.repository.donor_repository.DonationHistoryRepository;
import com.blood_donor.service.donor_service.IDonorProfileService;
import com.blood_donor.service.donor_service.IDonorRegistrationService;
import com.blood_donor.service.donor_service.IDonorSearchService;
import com.blood_donor.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@org.springframework.validation.annotation.Validated
@RestController
@RequestMapping(value={"/api/donor"})
@Tag(name="Donor", description="Donor registration, search and management APIs")
public class DonorController {
    private static final Logger log = LoggerFactory.getLogger(DonorController.class);
    private final IDonorRegistrationService registrationService;
    private final IDonorProfileService profileService;
    private final IDonorSearchService searchService;
    private final SecurityUtils securityUtils;
    private final DonationHistoryRepository historyRepository;
    private final DonorProfileRepository donorRepo;

    @PostMapping(value={"/register"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Register as donor", description="Create donor profile. Role updated to DONOR automatically.")
    public ResponseEntity<ApiResponse<DonorResponse>> registerDonor(@Valid @RequestBody DonorRegisterRequest request) {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorResponse response = this.registrationService.registerDonor(userId, request);
        log.info("Donor registered: userId={}", (Object)userId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.CREATED).body(ApiResponse.success("Donor registered successfully", response));
    }

    @GetMapping(value={"/me", "/profile"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Get my donor profile")
    public ResponseEntity<ApiResponse<DonorResponse>> getMyProfile() {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorResponse response = this.profileService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }

    @PatchMapping(value={"/profile"})
    @PreAuthorize(value="hasRole('DONOR') or hasRole('ADMIN')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Update donor profile", description="Partial update \u2014 send only fields to change. Location update requires all location fields.")
    public ResponseEntity<ApiResponse<DonorResponse>> updateProfile(@Valid @RequestBody DonorUpdateRequest request) {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorResponse response = this.profileService.updateProfile(userId, request);
        log.info("Profile updated: userId={}", (Object)userId);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PatchMapping(value={"/preferences"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Update notification preferences", description="Toggle SMS and email emergency alerts.")
    public ResponseEntity<ApiResponse<DonorResponse>> updatePreferences(@RequestBody com.blood_donor.model.dto.request.donor_request.DonorPreferencesRequest request) {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorResponse response = this.profileService.updatePreferences(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Notification preferences updated successfully", response));
    }

    @GetMapping(value={"/availability"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Check donation availability", description="Check current availability status of donor.")
    public ResponseEntity<ApiResponse<Boolean>> getAvailability() {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorResponse response = this.profileService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Availability status fetched", response.isAvailable()));
    }

    @PatchMapping(value={"/availability"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Toggle donation availability", description="Mark yourself available or unavailable. 90-day rule is checked before marking available.")
    public ResponseEntity<ApiResponse<Void>> toggleAvailability() {
        Long userId = this.securityUtils.getCurrentUserId();
        String message = this.profileService.toggleAvailability(userId);
        log.info("Availability toggled: userId={}", (Object)userId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping(value={"/eligibility"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Check donation eligibility", description="Check 90-day rule. Returns status and days remaining.")
    public ResponseEntity<ApiResponse<EligibilityResponse>> checkEligibility() {
        Long userId = this.securityUtils.getCurrentUserId();
        EligibilityResponse response = this.profileService.checkEligibility(userId);
        return ResponseEntity.ok(ApiResponse.success("Eligibility checked", response));
    }

    @GetMapping(value={"/search"})
    @Operation(summary="Search donors by city", description="Public endpoint. Compatible blood groups included. Redis cached for performance.")
    public ResponseEntity<ApiResponse<Page<DonorResponse>>> searchByCity(@Valid @ModelAttribute DonorSearchRequest request) {
        Page<DonorResponse> donors = this.searchService.searchByCity(request);
        log.info("City search: city={}, bloodGroup={}, results={}", new Object[]{request.getCity(), request.getBloodGroup(), donors.getTotalElements()});
        return ResponseEntity.ok(ApiResponse.success("Found " + donors.getTotalElements() + " donors", donors));
    }

    @GetMapping(value={"/search/nearby"})
    @Operation(summary="Search nearby donors", description="Public endpoint. Uses Haversine formula. Returns donors sorted by distance with distanceKm field.")
    public ResponseEntity<ApiResponse<List<DonorResponse>>> searchNearby(@Valid @ModelAttribute DonorSearchRequest request) {
        List<DonorResponse> donors = this.searchService.searchByLocation(request);
        log.info("Radius search: lat={}, lng={}, radius={}km, results={}", new Object[]{request.getRadiusSearch() != null ? request.getRadiusSearch().getLatitude() : "N/A", request.getRadiusSearch() != null ? request.getRadiusSearch().getLongitude() : "N/A", request.getRadiusSearch() != null ? request.getRadiusSearch().getRadiusKm() : "N/A", donors.size()});
        return ResponseEntity.ok(ApiResponse.success("Found " + donors.size() + " nearby donors", donors));
    }

    @GetMapping(value={"/history"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Get my donation history")
    public ResponseEntity<ApiResponse<Page<DonationHistoryDto>>> getMyHistory(@RequestParam(defaultValue="0") @jakarta.validation.constraints.Min(0L) int page, @RequestParam(defaultValue="10") @jakarta.validation.constraints.Min(1L) @jakarta.validation.constraints.Max(50L) int size) {
        Long userId = this.securityUtils.getCurrentUserId();
        DonorProfile donor = this.donorRepo.findByUserId(userId).orElseThrow(() -> ApiException.notFound("Donor profile not found"));
        PageRequest pageable = PageRequest.of((int)page, (int)size, (Sort)Sort.by((String[])new String[]{"donationDate"}).descending());
        Page<DonationHistory> history = this.historyRepository.findByDonorProfileIdOrderByDonationDateDesc(donor.getId(), (Pageable)pageable);
        Page response = history.map(h -> DonationHistoryDto.builder().id(h.getId()).hospitalName(h.getHospitalName()).donationDate(h.getDonationDate()).bloodGroup(h.getBloodGroup()).unitsDonated(h.getUnitsDonated()).verified(h.isVerified()).build());
        return ResponseEntity.ok(ApiResponse.success("History fetched", response));
    }

    public DonorController(IDonorRegistrationService registrationService, IDonorProfileService profileService, IDonorSearchService searchService, SecurityUtils securityUtils, DonationHistoryRepository historyRepository, DonorProfileRepository donorRepo) {
        this.registrationService = registrationService;
        this.profileService = profileService;
        this.searchService = searchService;
        this.securityUtils = securityUtils;
        this.historyRepository = historyRepository;
        this.donorRepo = donorRepo;
    }
}

