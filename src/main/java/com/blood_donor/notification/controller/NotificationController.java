package com.blood_donor.notification.controller;

import com.blood_donor.exception.ApiException;
import com.blood_donor.model.dto.response.api_response.ApiResponse;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.notification.service.IEmailService;
import com.blood_donor.notification.service.INotificationService;
import com.blood_donor.service.donor_service.IBloodRequestQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(value={"/api/notification"})
@Tag(name="Notification", description="Manual notification trigger APIs \u2014 Admin/Hospital use")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final INotificationService notificationService;
    private final IEmailService emailService;
    private final IBloodRequestQueryService bloodRequestService;

    @PostMapping(value={"/alert/{requestId}"})
    @PreAuthorize(value="hasRole('ADMIN') or hasRole('HOSPITAL')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Manually trigger emergency alert", description="Send emergency notification to nearby donors.\n\nUse when:\n  Auto-notification missed\n  Additional donors needed\n  System recovery after downtime\n\nReturns 202 Accepted (async \u2014 background processing)")
    @ApiResponses(value={@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="202", description="Alert triggered \u2014 notifications being sent"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="404", description="Blood request not found"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="400", description="Request not active (cancelled/fulfilled)"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="403", description="Not authorized \u2014 ADMIN or HOSPITAL role required")})
    public ResponseEntity<ApiResponse<String>> triggerEmergencyAlert(@PathVariable @Parameter(description="Blood request ID", example="123") Long requestId, Authentication authentication) {
        log.info("Manual alert trigger: requestId={}, triggeredBy={}", (Object)requestId, (Object)(authentication != null ? authentication.getName() : "unknown"));
        BloodRequest request = this.bloodRequestService.findActiveById(requestId).orElseThrow(() -> ApiException.notFound("Blood request not found: " + requestId));
        if (!request.isActive()) {
            log.warn("Alert skipped \u2014 request not active: requestId={}, status={}", (Object)requestId, (Object)request.getStatus());
            throw ApiException.badRequest("Cannot trigger alert: request is " + request.getStatus().name() + " (must be ACTIVE)");
        }
        this.notificationService.sendEmergencyAlert(request);
        log.info("Emergency alert triggered: requestId={}, blood={}, city={}", new Object[]{requestId, request.getBloodGroup(), request.getCity()});
        return ResponseEntity.status((HttpStatusCode)HttpStatus.ACCEPTED).body(ApiResponse.success("Emergency alert triggered \u2014 notifications are being sent in the background", "ALERT_TRIGGERED"));
    }

    @PostMapping(value={"/re-alert/{requestId}"})
    @PreAuthorize(value="hasRole('ADMIN')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Re-trigger emergency alert", description="Send notification again \u2014 when first alert did not get enough donor responses.\nADMIN only.")
    public ResponseEntity<ApiResponse<String>> reNotifyDonors(@PathVariable Long requestId, Authentication authentication) {
        log.info("Re-notification triggered: requestId={}, by={}", (Object)requestId, (Object)(authentication != null ? authentication.getName() : "unknown"));
        BloodRequest request = this.bloodRequestService.findActiveById(requestId).orElseThrow(() -> ApiException.notFound("Blood request not found: " + requestId));
        if (!request.isActive()) {
            throw ApiException.badRequest("Cannot re-notify: request is " + request.getStatus());
        }
        this.notificationService.sendEmergencyAlert(request);
        log.info("Re-notification sent: requestId={}", (Object)requestId);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.ACCEPTED).body(ApiResponse.success("Re-notification triggered successfully", "RE_ALERT_TRIGGERED"));
    }

    @PostMapping(value={"/test-email"})
    @PreAuthorize(value="hasRole('ADMIN')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Send test email \u2014 Dev/Staging only", description="Test email delivery. Not available in production.\nADMIN role required.")
    public ResponseEntity<ApiResponse<String>> sendTestEmail(@RequestParam @Email(message="Invalid email address format") @NotBlank(message="Email address is required") @Parameter(description="Recipient email address", example="test@example.com") @Email(message="Invalid email address format") @NotBlank(message="Email address is required") String email, Authentication authentication) {
        log.info("Test email request: to={}, by={}", (Object)email, (Object)(authentication != null ? authentication.getName() : "unknown"));
        String activeProfile = System.getProperty("spring.profiles.active", "development");
        if ("production".equalsIgnoreCase(activeProfile)) {
            throw ApiException.forbidden("Test email endpoint is not available in production");
        }
        CompletableFuture<Boolean> result = this.emailService.sendHtmlEmail(email, "[TEST] Blood Donor Network \u2014 Email Test", "test-email", Map.of("recipientEmail", email, "testTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss")), "environment", activeProfile, "sentBy", authentication != null ? authentication.getName() : "system"));
        result.whenComplete((ok, ex) -> {
            if (Boolean.TRUE.equals(ok)) {
                log.info("Test email delivered: to={}", (Object)email);
            } else {
                log.warn("Test email failed: to={}", (Object)email);
            }
        });
        log.info("Test email dispatched: to={}", (Object)email);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.ACCEPTED).body(ApiResponse.success("Test email dispatched to: " + email + " \u2014 check inbox in a few seconds", "TEST_EMAIL_SENT"));
    }

    @GetMapping(value={"/status/{requestId}"})
    @PreAuthorize(value="hasRole('ADMIN') or hasRole('HOSPITAL')")
    @SecurityRequirement(name="bearerAuth")
    @Operation(summary="Notification status for a blood request", description="Check how many donors were notified and responded.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationStatus(@PathVariable Long requestId) {
        log.info("Notification status check: requestId={}", (Object)requestId);
        BloodRequest request = this.bloodRequestService.findActiveById(requestId).orElseThrow(() -> ApiException.notFound("Blood request not found: " + requestId));
        Map<String, Object> status = this.bloodRequestService.getNotificationStatus(requestId);
        return ResponseEntity.ok(ApiResponse.success("Notification status retrieved", status));
    }

    public NotificationController(INotificationService notificationService, IEmailService emailService, IBloodRequestQueryService bloodRequestService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
        this.bloodRequestService = bloodRequestService;
    }
}

