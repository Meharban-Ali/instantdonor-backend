package com.blood_donor.controller.donor_controller;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.model.dto.request.donor_request.CreateBloodRequestDto;
import com.blood_donor.model.dto.request.donor_request.RespondToRequestDto;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import com.blood_donor.model.dto.response.donor_response.BloodRequestResponse;
import com.blood_donor.service.donor_service.IBloodRequestCreationService;
import com.blood_donor.service.donor_service.IBloodRequestQueryService;
import com.blood_donor.service.donor_service.IBloodRequestResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value={"/api/request"})
@Tag(name="Blood Request", description="Emergency blood request management APIs")
public class BloodRequestController {
    private static final Logger log = LoggerFactory.getLogger(BloodRequestController.class);
    private final IBloodRequestCreationService creationService;
    private final IBloodRequestQueryService queryService;
    private final IBloodRequestResponseService responseService;

    @PostMapping
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Create emergency blood request", description="Creates blood request. CRITICAL=6hrs, URGENT=24hrs, NORMAL=48hrs expiry.")
    public ResponseEntity<ApiResponse<BloodRequestResponse>> createRequest(@Valid @RequestBody CreateBloodRequestDto dto) {
        BloodRequestResponse response = this.creationService.createRequest(dto);
        log.info("Blood request created via API");
        return ResponseEntity.status((HttpStatusCode)HttpStatus.CREATED).body(ApiResponse.success("Blood request created successfully", response));
    }

    @GetMapping(value={"/active"})
    @Operation(summary="Get active blood requests", description="Public endpoint. Returns paginated active requests. CRITICAL shown first. Redis cached.")
    public ResponseEntity<ApiResponse<Page<BloodRequestResponse>>> getActiveRequests(@RequestParam(defaultValue="0") @Min(value=0L, message="Page must be >= 0") @Min(value=0L, message="Page must be >= 0") int page, @RequestParam(defaultValue="10") @Min(value=1L, message="Size must be >= 1") @Max(value=50L, message="Size must be <= 50") @Min(value=1L, message="Size must be >= 1") @Max(value=50L, message="Size must be <= 50") int size) {
        Page<BloodRequestResponse> requests = this.queryService.getActiveRequests(page, size);
        return ResponseEntity.ok(ApiResponse.success("Active requests fetched \u2014 total: " + requests.getTotalElements(), requests));
    }

    @GetMapping(value={"/search"})
    @Operation(summary="Search active requests by city and blood group", description="Public endpoint. Filter by city and/or blood group. Both optional \u2014 returns all if neither provided.")
    public ResponseEntity<ApiResponse<Page<BloodRequestResponse>>> searchRequests(@RequestParam(required=false) String city, @RequestParam(required=false) BloodGroup bloodGroup, @RequestParam(defaultValue="0") @Min(value=0L) @Min(value=0L) int page, @RequestParam(defaultValue="10") @Min(value=1L) @Max(value=50L) @Min(value=1L) @Max(value=50L) int size) {
        Page<BloodRequestResponse> requests = this.queryService.searchRequests(city, bloodGroup, page, size);
        log.info("Search: city={}, bloodGroup={}, results={}", new Object[]{city, bloodGroup, requests.getTotalElements()});
        return ResponseEntity.ok(ApiResponse.success("Requests found", requests));
    }

    @GetMapping(value={"/{id}"})
    @Operation(summary="Get blood request by ID", description="Public endpoint. Returns full request details with tracking info.")
    public ResponseEntity<ApiResponse<BloodRequestResponse>> getById(@PathVariable Long id) {
        BloodRequestResponse response = this.queryService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success("Request fetched", response));
    }

    @GetMapping(value={"/my", "/my-requests"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Get my blood requests history", description="Returns all requests created by current user. All statuses included.")
    public ResponseEntity<ApiResponse<Page<BloodRequestResponse>>> getMyRequests(@RequestParam(defaultValue="0") @Min(value=0L) @Min(value=0L) int page, @RequestParam(defaultValue="10") @Min(value=1L) @Max(value=50L) @Min(value=1L) @Max(value=50L) int size) {
        Page<BloodRequestResponse> requests = this.queryService.getMyRequests(page, size);
        return ResponseEntity.ok(ApiResponse.success("Your requests fetched", requests));
    }

    @PostMapping(value={"/respond"})
    @PreAuthorize(value="hasRole('DONOR')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Respond to a blood request", description="Donor accepts or rejects. Reliability score updated. Enough accepts triggers FULFILLED status.")
    public ResponseEntity<ApiResponse<BloodRequestResponse>> respondToRequest(@Valid @RequestBody RespondToRequestDto dto) {
        BloodRequestResponse response = this.responseService.respondToRequest(dto);
        String message = Boolean.TRUE.equals(dto.getAccept()) ? "Request accepted successfully" : "Request rejected";
        log.info("Donor responded: requestId={}, accepted={}", (Object)dto.getRequestId(), (Object)dto.getAccept());
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @PatchMapping(value={"/{id}/fulfill"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Mark request as fulfilled", description="Mark blood request as fulfilled. Creator or Admin only. Use when blood is arranged outside the platform.")
    public ResponseEntity<ApiResponse<BloodRequestResponse>> markFulfilled(@PathVariable Long id) {
        BloodRequestResponse response = this.creationService.markFulfilled(id);
        log.info("Request marked fulfilled via API: id={}", (Object)id);
        return ResponseEntity.ok(ApiResponse.success("Request marked as fulfilled", response));
    }

    @PatchMapping(value={"/{id}/cancel"})
    @PreAuthorize(value="isAuthenticated()")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Cancel a blood request", description="Cancel blood request. Creator or Admin only. Cannot cancel FULFILLED requests.")
    public ResponseEntity<ApiResponse<BloodRequestResponse>> cancelRequest(@PathVariable Long id) {
        BloodRequestResponse response = this.creationService.cancelRequest(id);
        log.info("Request cancelled via API: id={}", (Object)id);
        return ResponseEntity.ok(ApiResponse.success("Request cancelled successfully", response));
    }

    public BloodRequestController(IBloodRequestCreationService creationService, IBloodRequestQueryService queryService, IBloodRequestResponseService responseService) {
        this.creationService = creationService;
        this.queryService = queryService;
        this.responseService = responseService;
    }
}

