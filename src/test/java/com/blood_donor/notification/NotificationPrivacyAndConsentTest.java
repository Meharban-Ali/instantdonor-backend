package com.blood_donor.notification;

import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.blood_donor.model.dto.request.donor_request.DonorPreferencesRequest;
import com.blood_donor.model.dto.response.donor_response.DonorResponse;
import com.blood_donor.model.entity.BloodRequest;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.monitoring.CustomMetricsService;
import com.blood_donor.notification.properties.MailProperties;
import com.blood_donor.notification.properties.NotificationProperties;
import com.blood_donor.notification.service.IEmailService;
import com.blood_donor.notification.service.ISmsService;
import com.blood_donor.notification.service_Impl.AiDonorMatchingStrategy;
import com.blood_donor.notification.service_Impl.EmailMessageBuilder;
import com.blood_donor.notification.service_Impl.EmailService;
import com.blood_donor.notification.service_Impl.EmailValidator;
import com.blood_donor.notification.service_Impl.NotificationService;
import com.blood_donor.notification.service_Impl.NotificationTracker;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.service.donor_service_impl.DonorProfileService;
import com.blood_donor.service.mapper.DonorMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Privacy, Consent, and Rate-Limiting Unit Tests")
class NotificationPrivacyAndConsentTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private IEmailService emailService;

    @Mock
    private ISmsService smsService;

    @Mock
    private AiDonorMatchingStrategy matchingStrategy;

    @Mock
    private NotificationTracker notificationTracker;

    @Mock
    private DonorProfileRepository donorRepo;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private CustomMetricsService customMetricsService;

    private NotificationProperties notificationProperties;
    private MailProperties mailProperties;

    @BeforeEach
    void setUp() {
        notificationProperties = new NotificationProperties();
        notificationProperties.setEnabled(true);
        notificationProperties.setMaxDonorsToNotify(5);
        notificationProperties.setMaxSmsPerDonorPerDay(3);
        notificationProperties.setGlobalDailySmsCap(500);

        mailProperties = new MailProperties();
        mailProperties.setFromAddress("noreply@blooddonor.org");
        mailProperties.setFromName("AI Blood Donor Network");
    }

    @Test
    @DisplayName("EmailMessageBuilder should set Reply-To when configured")
    void testReplyToConfigured() throws Exception {
        mailProperties.setReplyTo("support@blooddonor.org");
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailMessageBuilder builder = new EmailMessageBuilder(mailSender, mailProperties);

        MimeMessage built = builder.buildHtmlMessage("recipient@example.com", "Test", "<p>Hello</p>");
        assertThat(built.getReplyTo()).hasSize(1);
        assertThat(built.getReplyTo()[0].toString()).contains("support@blooddonor.org");

        SimpleMailMessage simple = builder.buildSimpleMessage("recipient@example.com", "Test", "Hello");
        assertThat(simple.getReplyTo()).isEqualTo("support@blooddonor.org");
    }

    @Test
    @DisplayName("EmailMessageBuilder should not set Reply-To when empty")
    void testReplyToNotConfigured() throws Exception {
        mailProperties.setReplyTo("");
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        EmailMessageBuilder builder = new EmailMessageBuilder(mailSender, mailProperties);

        MimeMessage built = builder.buildHtmlMessage("recipient@example.com", "Test", "<p>Hello</p>");
        assertThat(built.getReplyTo()).hasSize(1);
        // Defaults to From address if no Reply-To header is set
        assertThat(built.getReplyTo()[0].toString()).contains("noreply@blooddonor.org");

        SimpleMailMessage simple = builder.buildSimpleMessage("recipient@example.com", "Test", "Hello");
        assertThat(simple.getReplyTo()).isNull();
    }

    @Test
    @DisplayName("EmailService should gracefully skip when mail is unconfigured")
    void testEmailServiceGracefulSkipWhenUnconfigured() {
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        EmailValidator validator = new EmailValidator();
        EmailMessageBuilder builder = new EmailMessageBuilder(mailSender, mailProperties);

        EmailService service = new EmailService(mailSender, templateEngine, mailProperties, builder, validator, customMetricsService);
        ReflectionTestUtils.setField(service, "mailHost", "");
        ReflectionTestUtils.setField(service, "mailUsername", "");
        ReflectionTestUtils.setField(service, "mailPassword", "");
        ReflectionTestUtils.setField(service, "smtpAuth", true);

        assertThat(service.isMailConfigured()).isFalse();

        CompletableFuture<Boolean> result = service.sendSimpleEmail("test@example.com", "Subject", "Body");
        assertThat(result.join()).isFalse();
        verifyNoInteractions(mailSender);
    }

    @Test
    @DisplayName("Emergency alert must redact patientName and contactPhone, and respect donor consent flags")
    void testEmergencyAlertPrivacyAndConsent() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        NotificationService service = new NotificationService(
                emailService,
                smsService,
                matchingStrategy,
                notificationTracker,
                donorRepo,
                notificationProperties,
                redisTemplate,
                customMetricsService
        );
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(service, "smsEmergencyTemplate", "%s: %s blood needed at %s, %s. Units: %d. Open app to respond: %s");

        // Blood Request with sensitive PII
        User requester = User.builder().id(99L).name("Requester User").email("req@example.com").phone("9111111111").build();
        BloodRequest request = BloodRequest.builder()
                .id(42L)
                .patientName("Secret Patient John Doe")
                .contactPhone("9988776655")
                .bloodGroup(BloodGroup.O_POSITIVE)
                .hospitalName("City General Hospital")
                .city("Mumbai")
                .unitsRequired(2)
                .urgency(UrgencyLevel.CRITICAL)
                .createdBy(requester)
                .build();

        // Donor 1: Opted out of Email, Opted in to SMS
        User user1 = User.builder().id(101L).name("Donor One").email("donor1@example.com").phone("+919876543210").build();
        DonorProfile donor1 = DonorProfile.builder()
                .id(1L)
                .user(user1)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .emailAlertsEnabled(false)
                .smsAlertsEnabled(true)
                .build();

        // Donor 2: Opted in to Email, Opted out of SMS
        User user2 = User.builder().id(102L).name("Donor Two").email("donor2@example.com").phone("+919876543211").build();
        DonorProfile donor2 = DonorProfile.builder()
                .id(2L)
                .user(user2)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .emailAlertsEnabled(true)
                .smsAlertsEnabled(false)
                .build();

        AiDonorResult r1 = new AiDonorResult();
        r1.setDonorId(1L);
        r1.setDistanceKm(5.0);

        AiDonorResult r2 = new AiDonorResult();
        r2.setDonorId(2L);
        r2.setDistanceKm(3.0);

        when(donorRepo.findById(1L)).thenReturn(Optional.of(donor1));
        when(donorRepo.findById(2L)).thenReturn(Optional.of(donor2));
        when(matchingStrategy.findDonors(any(), anyInt())).thenReturn(List.of(r1, r2));
        doNothing().when(notificationTracker).trackNotification(any(), any());
        when(emailService.sendHtmlEmail(anyString(), anyString(), anyString(), anyMap()))
                .thenReturn(CompletableFuture.completedFuture(true));
        when(smsService.sendSms(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(true));

        // Act
        service.sendEmergencyAlert(request);

        // Verify Donor 1: SMS sent, Email skipped
        ArgumentCaptor<String> smsCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> phoneCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsService, times(1)).sendSms(phoneCaptor.capture(), smsCaptor.capture());
        assertThat(phoneCaptor.getValue()).isEqualTo("+919876543210");

        String smsBody = smsCaptor.getValue();
        assertThat(smsBody).doesNotContain("Secret Patient John Doe");
        assertThat(smsBody).doesNotContain("9988776655");
        assertThat(smsBody).contains("CRITICAL-URGENT");
        assertThat(smsBody).contains("City General Hospital");
        assertThat(smsBody).contains("http://localhost:3000/requests/42");

        // Verify Donor 2: Email sent, SMS skipped
        ArgumentCaptor<String> emailToCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, Object>> templateVarsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(emailService, times(1)).sendHtmlEmail(emailToCaptor.capture(), anyString(), eq("emergency-alert"), templateVarsCaptor.capture());
        assertThat(emailToCaptor.getValue()).isEqualTo("donor2@example.com");

        Map<String, Object> vars = templateVarsCaptor.getValue();
        assertThat(vars.get("patientName")).isEqualTo("A patient in need");
        assertThat(vars.get("patientName")).isNotEqualTo("Secret Patient John Doe");
        assertThat(vars.get("contactPhone")).isEqualTo("Available in app upon acceptance");
        assertThat(vars.get("contactPhone")).isNotEqualTo("9988776655");
    }

    @Test
    @DisplayName("SMS rate-limiting should skip SMS when per-donor or global daily cap is exceeded")
    void testSmsRateLimiting() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        NotificationService service = new NotificationService(
                emailService,
                smsService,
                matchingStrategy,
                notificationTracker,
                donorRepo,
                notificationProperties,
                redisTemplate,
                customMetricsService
        );
        ReflectionTestUtils.setField(service, "frontendUrl", "http://localhost:3000");

        BloodRequest request = BloodRequest.builder()
                .id(55L)
                .patientName("A patient")
                .bloodGroup(BloodGroup.A_POSITIVE)
                .hospitalName("Hope Hospital")
                .city("Delhi")
                .urgency(UrgencyLevel.URGENT)
                .build();

        User user = User.builder().id(201L).name("Donor Cap").email("donorcap@example.com").phone("+919123456789").build();
        DonorProfile donor = DonorProfile.builder()
                .id(10L)
                .user(user)
                .bloodGroup(BloodGroup.A_POSITIVE)
                .emailAlertsEnabled(false)
                .smsAlertsEnabled(true)
                .build();

        AiDonorResult r = new AiDonorResult();
        r.setDonorId(10L);
        r.setDistanceKm(2.0);

        when(donorRepo.findById(10L)).thenReturn(Optional.of(donor));
        when(matchingStrategy.findDonors(any(), anyInt())).thenReturn(List.of(r));
        doNothing().when(notificationTracker).trackNotification(any(), any());

        // Case 1: Global count exceeded (501 > 500)
        when(valueOperations.increment(startsWith("sms:limit:global:"))).thenReturn(501L);

        service.sendEmergencyAlert(request);

        verify(customMetricsService, times(1)).recordSmsGlobalLimitExceeded();
        verifyNoInteractions(smsService);

        // Case 2: Global ok (1), Donor count exceeded (4 > 3)
        when(valueOperations.increment(startsWith("sms:limit:global:"))).thenReturn(1L);
        when(valueOperations.increment(startsWith("sms:limit:donor:10:"))).thenReturn(4L);

        service.sendEmergencyAlert(request);

        verify(customMetricsService, times(1)).recordSmsDonorLimitExceeded();
        verifyNoInteractions(smsService);
    }

    @Test
    @DisplayName("DonorProfileService should update communication preferences and timestamp")
    void testUpdatePreferences() {
        DonorProfileRepository repo = mock(DonorProfileRepository.class);
        DonorMapper mapper = new DonorMapper();
        DonorProfileService profileService = new DonorProfileService(repo, mapper);

        User user = User.builder().id(50L).build();
        DonorProfile profile = DonorProfile.builder()
                .id(15L)
                .user(user)
                .bloodGroup(BloodGroup.O_POSITIVE)
                .smsAlertsEnabled(true)
                .emailAlertsEnabled(true)
                .consentUpdatedAt(LocalDateTime.now().minusDays(10))
                .build();

        when(repo.findByUserId(50L)).thenReturn(Optional.of(profile));
        when(repo.save(any(DonorProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DonorPreferencesRequest req = new DonorPreferencesRequest();
        req.setSmsAlertsEnabled(false);
        req.setEmailAlertsEnabled(true);

        DonorResponse res = profileService.updatePreferences(50L, req);

        assertThat(res.getSmsAlertsEnabled()).isFalse();
        assertThat(res.getEmailAlertsEnabled()).isTrue();
        assertThat(res.getConsentUpdatedAt()).isNotNull();
        assertThat(profile.isSmsAlertsEnabled()).isFalse();
        assertThat(profile.isEmailAlertsEnabled()).isTrue();
    }
}
