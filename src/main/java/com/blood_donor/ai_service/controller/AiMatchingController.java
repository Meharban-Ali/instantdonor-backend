package com.blood_donor.ai_service.controller;

import com.blood_donor.ai_service.dto.request.AiDemandRequest;
import com.blood_donor.ai_service.dto.request.AiMatchRequest;
import com.blood_donor.ai_service.dto.response.AiDemandResponse;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.service.IAiMatchingService;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value={"/api/ai"})
@Tag(name="AI Matching", description="AI-powered donor matching and blood demand prediction")
public class AiMatchingController {
    private static final Logger log = LoggerFactory.getLogger(AiMatchingController.class);
    private final IAiMatchingService aiService;

    @PostMapping(value={"/match-donors"})
    @PreAuthorize(value="hasRole('ADMIN') or hasRole('HOSPITAL')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="AI Donor Matching", description="Find best donors using AI scoring algorithm.\n\nScoring Formula:\n  score = 0.40\u00d7distance + 0.30\u00d7reliability + 0.20\u00d7eligibility + 0.10\u00d7response_rate\n\nRanking:\n  CRITICAL \u2192 distance priority\n  NORMAL/URGENT \u2192 AI score priority\n  Eligible donors always first")
    public ResponseEntity<ApiResponse<AiMatchResponse>> matchDonors(@Valid @RequestBody AiMatchRequest request) {
        log.info("AI match request: bloodGroup={}, radius={}km, urgency={}, limit={}", new Object[]{request.getBloodGroup(), request.getRadiusKm(), request.getUrgency(), request.getLimit()});
        try {
            AiMatchResponse response = this.aiService.findBestDonors(request);
            log.info("AI match complete: found={}, time={}ms", (Object)response.getTotalFound(), (Object)response.getProcessingTimeMs());
            return ResponseEntity.ok(ApiResponse.success("AI matching complete", response));
        }
        catch (ApiException e) {
            log.warn("AI match ApiException: status={}, message={}", (Object)e.getStatus(), (Object)e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("AI match unexpected error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("Donor matching failed. Please try again.");
        }
    }

    @PostMapping(value={"/predict-demand"})
    @PreAuthorize(value="hasRole('ADMIN') or hasRole('HOSPITAL')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Blood Demand Prediction", description="Predict future blood demand for a city.\n\nAlgorithm:\n  Uses 90-day historical data\n  Compares recent_30 vs previous_30\n  daily_avg \u00d7 days_ahead = prediction\n\nConfidence:\n  0.75 = STABLE, 0.72 = INCREASING\n  0.68 = DECREASING, 0.15 = No data")
    public ResponseEntity<ApiResponse<AiDemandResponse>> predictDemand(@Valid @RequestBody AiDemandRequest request) {
        log.info("AI prediction request: city={}, bloodGroup={}, days={}", new Object[]{request.getCity(), request.getBloodGroup(), request.getDaysAhead()});
        try {
            AiDemandResponse response = this.aiService.predictDemand(request);
            log.info("AI prediction complete: city={}, predicted={}, confidence={}%, trend={}, urgent={}", new Object[]{response.getCity(), response.getPredictedRequests(), response.getConfidencePercent(), response.getTrend(), response.needsUrgentAction()});
            return ResponseEntity.ok(ApiResponse.success("Prediction ready", response));
        }
        catch (ApiException e) {
            log.warn("AI prediction ApiException: {}", (Object)e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("AI prediction unexpected error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("Demand prediction failed. Please try again.");
        }
    }

    @GetMapping(value={"/health"})
    @Operation(summary="AI Service Health Check", description="Check Python AI service connectivity.\n200=UP, 503=DOWN")
    public ResponseEntity<ApiResponse<Void>> aiHealth() {
        boolean isUp = this.aiService.isAiServiceHealthy();
        log.info("AI health check: {}", (Object)(isUp ? "UP" : "DOWN"));
        if (isUp) {
            return ResponseEntity.ok(ApiResponse.success("AI Service is UP and running"));
        }
        return ResponseEntity.status((HttpStatusCode)HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.failure("AI Service is DOWN"));
    }

    @GetMapping(value={"/match-donors/quick"})
    @PreAuthorize(value="hasRole('ADMIN') or hasRole('HOSPITAL')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Quick AI Donor Search", description="Quick search via query params \u2014 for testing.\nUse POST /match-donors for production.")
    public ResponseEntity<ApiResponse<AiMatchResponse>> quickMatch(@RequestParam BloodGroup bloodGroup, @RequestParam @Min(value=-90L) @Max(value=90L) @Min(value=-90L) @Max(value=90L) double latitude, @RequestParam @Min(value=-180L) @Max(value=180L) @Min(value=-180L) @Max(value=180L) double longitude, @RequestParam(defaultValue="25.0") @Min(value=1L) @Max(value=200L) @Min(value=1L) @Max(value=200L) double radiusKm, @RequestParam(defaultValue="NORMAL") UrgencyLevel urgency, @RequestParam(defaultValue="10") @Min(value=1L) @Max(value=50L) @Min(value=1L) @Max(value=50L) int limit) {
        log.info("Quick match: bloodGroup={}, radius={}km, urgency={}", new Object[]{bloodGroup, radiusKm, urgency});
        AiMatchRequest request = AiMatchRequest.from(bloodGroup, latitude, longitude, radiusKm, urgency, 1, limit, null);
        try {
            AiMatchResponse response = this.aiService.findBestDonors(request);
            return ResponseEntity.ok(ApiResponse.success("AI matching complete", response));
        }
        catch (Exception e) {
            log.error("Quick match error: {}", (Object)e.getMessage(), (Object)e);
            throw ApiException.internalServerError("Quick match failed.");
        }
    }

    public AiMatchingController(IAiMatchingService aiService) {
        this.aiService = aiService;
    }
}

