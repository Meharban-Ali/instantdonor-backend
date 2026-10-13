package com.blood_donor.admin.controller;

import com.blood_donor.admin.dto.BloodGroupStatsDto;
import com.blood_donor.admin.dto.CityDemandDto;
import com.blood_donor.admin.dto.DashboardStatsDto;
import com.blood_donor.admin.dto.DonorSummaryDto;
import com.blood_donor.admin.dto.RoleUpdateRequest;
import com.blood_donor.admin.dto.UserSummaryDto;
import com.blood_donor.admin.service.IAdminAnalyticsService;
import com.blood_donor.admin.service.IAdminDonorService;
import com.blood_donor.admin.service.IAdminUserService;
import com.blood_donor.admin.service.IDashboardService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import com.blood_donor.service.auth_service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value={"/api/admin"})
@PreAuthorize(value="hasRole('ADMIN')")
@SecurityRequirement(name="bearerAuth")
@Tag(name="Admin", description="Admin dashboard, user management, donor management, analytics")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final IDashboardService dashboardService;
    private final IAdminUserService userService;
    private final IAdminDonorService donorService;
    private final IAdminAnalyticsService analyticsService;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;

    private String getClientIp() {
        return this.httpServletRequest.getRemoteAddr();
    }

    @GetMapping(value={"/dashboard"})
    @Operation(summary="Complete dashboard statistics", description="Returns all stats in one call.\n\nCached for 5 minutes in Redis.\nUse GET /dashboard/refresh to force fresh data.\n\nIncludes:\n  User stats, Blood request stats,\n  Donation stats, Blood group distribution,\n  Top cities by demand, Recent activity,\n  Platform health (AI, DB, SMS status)")
    @ApiResponses(value={@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200", description="Dashboard data fetched successfully")})
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboard(Authentication authentication) {
        log.info("Dashboard requested by: {}", (Object)(authentication != null ? authentication.getName() : "unknown"));
        DashboardStatsDto stats = this.dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched", stats));
    }

    @PostMapping(value={"/dashboard/refresh"})
    @Operation(summary="Force refresh dashboard cache", description="Evicts Redis cache and fetches fresh data.\nUse when cached data seems outdated.")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> refreshDashboard(Authentication authentication) {
        log.info("Dashboard cache refresh by: {}", (Object)(authentication != null ? authentication.getName() : "unknown"));
        DashboardStatsDto stats = this.dashboardService.refreshDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard refreshed successfully", stats));
    }

    @GetMapping(value={"/users"})
    @Operation(summary="Get all users \u2014 paginated + filtered", description="Filter by role or search by name/email.\nSorted by createdAt DESC.")
    public ResponseEntity<ApiResponse<Page<UserSummaryDto>>> getUsers(@Parameter(description="Filter by role") @RequestParam(required=false) Role role, @Parameter(description="Search by name or email") @RequestParam(required=false) String search, @Parameter(description="Page number (0-based)") @RequestParam(defaultValue="0") @Min(value=0L, message="Page must be >= 0") int page, @Parameter(description="Page size (max 50)") @RequestParam(defaultValue="20") @Min(value=1L, message="Size must be >= 1") @Max(value=50L, message="Size must be <= 50") int size) {
        log.info("Get users: role={}, search={}, page={}, size={}", role, search, page, size);
        Page<UserSummaryDto> users = this.userService.getAllUsers(role, search, page, size);
        return ResponseEntity.ok(ApiResponse.success("Users fetched: " + users.getTotalElements(), users));
    }

    @GetMapping(value={"/users/{userId}"})
    @Operation(summary="Get single user detail", description="Full user info including donor profile if exists.")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getUserById(@PathVariable Long userId) {
        log.info("Get user by id: {}", userId);
        UserSummaryDto user = this.userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success("User fetched", user));
    }

    @PutMapping(value={"/users/{userId}/toggle-status"})
    @Operation(summary="Toggle user active status", description="Activate or deactivate a user.\n\nCannot deactivate ADMIN users.\nDeactivated users cannot login.")
    public ResponseEntity<ApiResponse<UserSummaryDto>> toggleUserStatus(@PathVariable Long userId, Authentication authentication) {
        log.info("Toggle user status: userId={}, by={}", userId, authentication != null ? authentication.getName() : "unknown");
        UserSummaryDto user = this.userService.toggleUserStatus(userId);
        String msg = Boolean.TRUE.equals(user.getIsActive()) ? "User activated successfully" : "User deactivated successfully";
        String adminEmail = authentication != null ? authentication.getName() : "unknown";
        String actionType = Boolean.TRUE.equals(user.getIsActive()) ? "USER_ACTIVATE" : "USER_DEACTIVATE";
        this.auditLogService.log(actionType, "Admin toggled status for user: " + user.getEmail(), adminEmail, this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success(msg, user));
    }

    @PutMapping(value={"/users/{userId}/role"})
    @Operation(summary="Update user role", description="Add a role to user's roles set.\n\nExisting roles are preserved \u2014 role is added.\nBody: {\"role\": \"DONOR\", \"reason\": \"optional\"}")
    public ResponseEntity<ApiResponse<UserSummaryDto>> updateRole(@PathVariable Long userId, @Valid @RequestBody RoleUpdateRequest request, Authentication authentication) {
        log.info("Update role: userId={}, newRole={}, reason={}, by={}", userId, request.getRole(), request.getReason(), authentication != null ? authentication.getName() : "unknown");
        UserSummaryDto user = this.userService.updateUserRole(userId, request.getRole());
        String adminEmail = authentication != null ? authentication.getName() : "unknown";
        this.auditLogService.log("USER_ROLE_UPDATE", "Admin added role " + request.getRole() + " to user: " + user.getEmail() + ". Reason: " + request.getReason(), adminEmail, this.getClientIp());
        return ResponseEntity.ok(ApiResponse.success("Role " + request.getRole() + " added to user", user));
    }

    @GetMapping(value={"/donors"})
    @Operation(summary="Get all donors \u2014 paginated + filtered", description="Filter by city and/or blood group.\nSorted by reliability score DESC.")
    public ResponseEntity<ApiResponse<Page<DonorSummaryDto>>> getDonors(@Parameter(description="Filter by city") @RequestParam(required=false) String city, @Parameter(description="Filter by blood group") @RequestParam(required=false) BloodGroup bloodGroup, @RequestParam(defaultValue="0") @Min(value=0L, message="Page must be >= 0") int page, @RequestParam(defaultValue="20") @Min(value=1L, message="Size must be >= 1") @Max(value=50L, message="Size must be <= 50") int size) {
        log.info("Get donors: city={}, bloodGroup={}, page={}, size={}", city, bloodGroup, page, size);
        Page<DonorSummaryDto> donors = this.donorService.getAllDonors(city, bloodGroup, page, size);
        return ResponseEntity.ok(ApiResponse.success("Donors fetched: " + donors.getTotalElements(), donors));
    }

    @GetMapping(value={"/donors/{donorId}"})
    @Operation(summary="Get single donor detail", description="Full donor info with eligibility + stats.")
    public ResponseEntity<ApiResponse<DonorSummaryDto>> getDonorById(@PathVariable Long donorId) {
        log.info("Get donor by id: {}", (Object)donorId);
        DonorSummaryDto donor = this.donorService.getDonorById(donorId);
        return ResponseEntity.ok(ApiResponse.success("Donor fetched", donor));
    }

    @PutMapping(value={"/donors/{donorId}/verify"})
    @Operation(summary="Verify a donor \u2014 +20 reliability boost", description="Admin verification adds +20 to reliability score.\nScore is capped at 100.\nUse to reward trustworthy donors.")
    public ResponseEntity<ApiResponse<DonorSummaryDto>> verifyDonor(@PathVariable Long donorId, Authentication authentication) {
        log.info("Verify donor: donorId={}, by={}", (Object)donorId, (Object)(authentication != null ? authentication.getName() : "unknown"));
        DonorSummaryDto donor = this.donorService.verifyDonor(donorId);
        return ResponseEntity.ok(ApiResponse.success("Donor verified \u2014 reliability score updated", donor));
    }

    @PutMapping(value={"/donors/{donorId}/reset-score"})
    @Operation(summary="Reset donor reliability score to 50", description="Resets reliability to neutral baseline (50).\nUse when score is unfairly low.")
    public ResponseEntity<ApiResponse<DonorSummaryDto>> resetScore(@PathVariable Long donorId, Authentication authentication) {
        log.info("Reset donor score: donorId={}, by={}", (Object)donorId, (Object)(authentication != null ? authentication.getName() : "unknown"));
        DonorSummaryDto donor = this.donorService.resetDonorScore(donorId);
        return ResponseEntity.ok(ApiResponse.success("Reliability score reset to 50", donor));
    }

    @GetMapping(value={"/analytics/blood-groups"})
    @Operation(summary="Blood group wise donor and demand analytics", description="Shows supply vs demand per blood group.\n\nsupplyDemandRatio:\n  < 1.0 \u2192 SHORTAGE (demand > supply)\n  > 1.0 \u2192 SURPLUS (supply > demand)\n  = 0   \u2192 No donors registered")
    public ResponseEntity<ApiResponse<List<BloodGroupStatsDto>>> getBloodGroupStats() {
        log.info("Get blood group stats");
        List<BloodGroupStatsDto> stats = this.analyticsService.getBloodGroupStats();
        return ResponseEntity.ok(ApiResponse.success("Blood group stats fetched", stats));
    }

    @GetMapping(value={"/analytics/cities"})
    @Operation(summary="City wise blood demand analytics", description="Top cities by active blood requests \u2014 last 30 days.\n\nIncludes available donors + fulfillment rate per city.\nUseful for resource allocation decisions.")
    public ResponseEntity<ApiResponse<List<CityDemandDto>>> getCityDemand() {
        log.info("Get city demand stats");
        List<CityDemandDto> cities = this.analyticsService.getCityDemandStats();
        return ResponseEntity.ok(ApiResponse.success("City demand stats fetched", cities));
    }

    @GetMapping(value={"/analytics/monthly-trend"})
    @Operation(summary="Monthly donation trend \u2014 last 12 months", description="Returns month-wise donation count.\n\nFormat: {\"Mar 2024\": 45, \"Apr 2024\": 62, ...}\nUseful for identifying seasonal demand patterns.")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMonthlyTrend() {
        log.info("Get monthly donation trend");
        Map<String, Long> trend = this.analyticsService.getMonthlyDonationTrend();
        return ResponseEntity.ok(ApiResponse.success("Monthly trend fetched", trend));
    }

    public AdminController(IDashboardService dashboardService, IAdminUserService userService, IAdminDonorService donorService, IAdminAnalyticsService analyticsService, AuditLogService auditLogService, HttpServletRequest httpServletRequest) {
        this.dashboardService = dashboardService;
        this.userService = userService;
        this.donorService = donorService;
        this.analyticsService = analyticsService;
        this.auditLogService = auditLogService;
        this.httpServletRequest = httpServletRequest;
    }
}

