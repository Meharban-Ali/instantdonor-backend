package com.blood_donor.notification.service_Impl;

import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.monitoring.CustomMetricsService;
import com.blood_donor.notification.properties.NotificationProperties;
import com.blood_donor.notification.service.IEmailService;
import com.blood_donor.notification.service.INotificationService;
import com.blood_donor.notification.service.ISmsService;
import com.blood_donor.notification.service_Impl.AiDonorMatchingStrategy;
import com.blood_donor.notification.service_Impl.NotificationTracker;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.utils.PiiMasker;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService
implements INotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final IEmailService emailService;
    private final ISmsService smsService;
    private final AiDonorMatchingStrategy matchingStrategy;
    private final NotificationTracker notificationTracker;
    private final DonorProfileRepository donorRepo;
    private final NotificationProperties notificationProperties;
    private final StringRedisTemplate redisTemplate;
    private final CustomMetricsService customMetricsService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.sms.templates.emergency:%s: %s blood needed at %s, %s. Units: %d. Open app to respond: %s}")
    private String smsEmergencyTemplate;

    @Override
    @Async(value="notificationExecutor")
    public void sendEmergencyAlert(BloodRequest request) {
        if (!this.notificationProperties.isEnabled()) {
            log.info("Notifications disabled — skipping alert: requestId={}", request.getId());
            return;
        }
        log.info("Emergency alert pipeline started: requestId={}, blood={}, city={}", request.getId(), request.getBloodGroup(), request.getCity());
        try {
            List<AiDonorResult> matchedDonors = this.matchingStrategy.findDonors(request, this.notificationProperties.getMaxDonorsToNotify());
            if (matchedDonors.isEmpty()) {
                log.warn("No donors found for alert: requestId={}, city={}", request.getId(), request.getCity());
                return;
            }
            int notified = this.processNotifications(matchedDonors, request);
            log.info("Emergency alerts complete: notified={}, requestId={}", notified, request.getId());
        }
        catch (Exception e) {
            log.error("Emergency alert pipeline failed: requestId={}, error={}", request.getId(), e.getMessage(), e);
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public void sendPasswordResetEmail(String toEmail, String userName, String token) {
        String resetUrl = this.frontendUrl + "/reset-password?token=" + token;
        this.emailService.sendHtmlEmail(toEmail, "🔑 Password Reset Request — Blood Donor Network", "password-reset", Map.of("userName", userName, "resetUrl", resetUrl, "expiryMin", "30", "appName", "Blood Donor Network"));
        log.info("Password reset email sent to: {}", PiiMasker.maskEmail(toEmail));
    }

    private int processNotifications(List<AiDonorResult> matchedDonors, BloodRequest request) {
        int notified = 0;
        int smsSent = 0;
        int maxNotify = this.notificationProperties.getMaxDonorsToNotify();
        int maxSms = this.notificationProperties.getMaxSmsRecipients();
        for (AiDonorResult donorResult : matchedDonors) {
            if (notified >= maxNotify) break;
            DonorProfile donor = this.donorRepo.findById(donorResult.getDonorId()).orElse(null);
            if (donor == null) {
                log.warn("Donor profile not found: donorId={}", donorResult.getDonorId());
                continue;
            }
            this.notificationTracker.trackNotification(donor, request);

            if (donor.isEmailAlertsEnabled()) {
                this.sendDonorEmergencyEmail(donor, request, donorResult.getDistanceKm());
            } else {
                log.debug("Email alert skipped — donor opted out: donorId={}", donor.getId());
            }

            if (smsSent < maxSms) {
                if (donor.isSmsAlertsEnabled()) {
                    if (this.canSendSms(donor.getId())) {
                        this.sendDonorEmergencySms(donor, request);
                        ++smsSent;
                    }
                } else {
                    log.debug("SMS alert skipped — donor opted out: donorId={}", donor.getId());
                }
            }
            ++notified;
        }
        return notified;
    }

    private boolean canSendSms(Long donorId) {
        if (this.redisTemplate == null) {
            return true;
        }
        String today = LocalDate.now().toString();
        String globalKey = "sms:limit:global:" + today;
        String donorKey = "sms:limit:donor:" + donorId + ":" + today;
        try {
            Long globalCount = this.redisTemplate.opsForValue().increment(globalKey);
            if (globalCount != null && globalCount == 1L) {
                this.redisTemplate.expire(globalKey, Duration.ofDays(1));
            }
            if (globalCount != null && globalCount > this.notificationProperties.getGlobalDailySmsCap()) {
                log.warn("Global daily SMS cap reached ({}/day). Skipping SMS to donorId={}",
                        this.notificationProperties.getGlobalDailySmsCap(), donorId);
                if (this.customMetricsService != null) {
                    this.customMetricsService.recordSmsGlobalLimitExceeded();
                }
                return false;
            }

            Long donorCount = this.redisTemplate.opsForValue().increment(donorKey);
            if (donorCount != null && donorCount == 1L) {
                this.redisTemplate.expire(donorKey, Duration.ofDays(1));
            }
            if (donorCount != null && donorCount > this.notificationProperties.getMaxSmsPerDonorPerDay()) {
                log.warn("Donor daily SMS cap reached ({}/day). Skipping SMS to donorId={}",
                        this.notificationProperties.getMaxSmsPerDonorPerDay(), donorId);
                if (this.customMetricsService != null) {
                    this.customMetricsService.recordSmsDonorLimitExceeded();
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Redis check failed for SMS rate limit, allowing SMS fallback: {}", e.getMessage());
            return true;
        }
    }

    @Override
    @Async(value="notificationExecutor")
    public void sendDonorAcceptedConfirmation(DonorProfile donor, BloodRequest request) {
        User donorUser = donor.getUser();
        User requester = request.getCreatedBy();
        this.emailService.sendHtmlEmail(donorUser.getEmail(), this.notificationProperties.getEmailSubjectPrefix() + " Thank you for accepting the blood request!", "donor-confirmed", Map.ofEntries(Map.entry("donorName", donorUser.getName()), Map.entry("patientName", request.getPatientName()), Map.entry("bloodGroup", request.getBloodGroup().getDisplayName()), Map.entry("hospitalName", request.getHospitalName()), Map.entry("city", request.getCity()), Map.entry("contactPhone", this.safeValue(request.getContactPhone())), Map.entry("requestId", request.getId().toString()))).whenComplete((ok, ex) -> log.info("Donor confirmation email: donorId={}, success={}", donor.getId(), ok));
        this.emailService.sendHtmlEmail(requester.getEmail(), this.notificationProperties.getEmailSubjectPrefix() + " A donor has accepted your blood request!", "requester-donor-found", Map.ofEntries(Map.entry("donorName", donorUser.getName()), Map.entry("contactNote", "Please contact donor through the app"), Map.entry("patientName", request.getPatientName()), Map.entry("bloodGroup", request.getBloodGroup().getDisplayName()), Map.entry("hospitalName", request.getHospitalName()), Map.entry("requestId", request.getId().toString()))).whenComplete((ok, ex) -> log.info("Requester notification email: requestId={}, success={}", request.getId(), ok));
        log.info("Acceptance confirmation sent: donorId={}, requestId={}", donor.getId(), request.getId());
    }

    @Override
    @Async(value="notificationExecutor")
    public void sendRequestFulfilledNotification(BloodRequest request) {
        User requester = request.getCreatedBy();
        this.emailService.sendHtmlEmail(requester.getEmail(), this.notificationProperties.getEmailSubjectPrefix() + " Blood request fulfilled for " + request.getPatientName(), "request-fulfilled", Map.ofEntries(Map.entry("patientName", request.getPatientName()), Map.entry("bloodGroup", request.getBloodGroup().getDisplayName()), Map.entry("hospitalName", request.getHospitalName()), Map.entry("requestId", request.getId().toString()), Map.entry("fulfilledAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))))).whenComplete((ok, ex) -> log.info("Fulfilled notification: requestId={}, success={}", request.getId(), ok));
    }

    private void sendDonorEmergencyEmail(DonorProfile donor, BloodRequest request, double distanceKm) {
        User user = donor.getUser();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.debug("No email for donor: donorId={}", donor.getId());
            return;
        }
        UrgencyLevel urgency = request.getUrgency();
        String urgencyLabel = switch (urgency) {
            case UrgencyLevel.CRITICAL -> "[CRITICAL]";
            case UrgencyLevel.URGENT -> "[URGENT]";
            default -> "[NORMAL]";
        };
        this.emailService.sendHtmlEmail(user.getEmail(), this.notificationProperties.getEmailSubjectPrefix() + " " + urgencyLabel + " Blood Request — " + request.getBloodGroup().getDisplayName() + " needed in " + request.getCity(), "emergency-alert", Map.ofEntries(Map.entry("donorName", user.getName()), Map.entry("patientName", "A patient in need"), Map.entry("bloodGroup", request.getBloodGroup().getDisplayName()), Map.entry("hospitalName", request.getHospitalName()), Map.entry("city", request.getCity()), Map.entry("urgency", urgency.name()), Map.entry("urgencyLabel", urgencyLabel), Map.entry("unitsRequired", String.valueOf(request.getUnitsRequired())), Map.entry("distanceKm", String.format("%.1f", distanceKm)), Map.entry("requestId", request.getId().toString()), Map.entry("contactPhone", "Available in app upon acceptance"))).whenComplete((ok, ex) -> {
            if (!Boolean.TRUE.equals(ok)) {
                log.warn("Emergency email failed: donorId={}, requestId={}", donor.getId(), request.getId());
            }
        });
    }

    private void sendDonorEmergencySms(DonorProfile donor, BloodRequest request) {
        User user = donor.getUser();
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            log.debug("No phone for donor: donorId={}", donor.getId());
            return;
        }
        String urgencyPrefix = request.getUrgency() == UrgencyLevel.CRITICAL ? "CRITICAL-URGENT" : "URGENT";
        String appLink = this.frontendUrl + "/requests/" + request.getId();
        String smsText = String.format(this.smsEmergencyTemplate,
                urgencyPrefix,
                request.getBloodGroup().getDisplayName(),
                request.getHospitalName(),
                request.getCity(),
                request.getUnitsRequired(),
                appLink);
        this.smsService.sendSms(user.getPhone(), smsText).whenComplete((ok, ex) -> {
            if (!Boolean.TRUE.equals(ok)) {
                log.warn("Emergency SMS failed: donorId={}, requestId={}", donor.getId(), request.getId());
            }
        });
    }

    private String safeValue(String value) {
        return value != null && !value.isBlank() ? value : "N/A";
    }

    public NotificationService(IEmailService emailService,
                               ISmsService smsService,
                               AiDonorMatchingStrategy matchingStrategy,
                               NotificationTracker notificationTracker,
                               DonorProfileRepository donorRepo,
                               NotificationProperties notificationProperties,
                               @Autowired(required = false) StringRedisTemplate redisTemplate,
                               @Autowired(required = false) CustomMetricsService customMetricsService) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.matchingStrategy = matchingStrategy;
        this.notificationTracker = notificationTracker;
        this.donorRepo = donorRepo;
        this.notificationProperties = notificationProperties;
        this.redisTemplate = redisTemplate;
        this.customMetricsService = customMetricsService;
    }
}


