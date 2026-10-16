package com.blood_donor.ai_service.dto;

import com.blood_donor.ai_service.dto.response.AiDonorResult;
import com.blood_donor.ai_service.dto.response.AiMatchResponse;
import com.blood_donor.ai_service.dto.response.ScoreBreakdown;
import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.urgencyRequest.UrgencyLevel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AI Match Response Serialization & Round-Trip Tests")
class AiSerializationRoundTripTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("FastAPI inbound JSON deserializes WRITE_ONLY fields into memory")
    void testFastApiInboundDeserializationPreservesWriteOnlyFields() throws Exception {
        String fastApiJson = """
            {
                "donor_id": 42,
                "user_id": 105,
                "rank": 1,
                "name": "Jane Donor",
                "phone_masked": "98****3210",
                "blood_group": "A_POSITIVE",
                "city": "Mumbai",
                "latitude": 19.0760,
                "longitude": 72.8777,
                "distance_km": 4.5,
                "reliability_score": 0.95,
                "response_rate": 0.90,
                "total_donations": 8,
                "is_available": true,
                "is_eligible": true,
                "last_donation_date": "2026-06-01",
                "next_eligible_date": "2026-09-01",
                "ai_score": 0.92,
                "score_breakdown": {
                    "distance_score": 0.85,
                    "reliability_score": 0.95,
                    "urgency_boost": 0.1,
                    "final_score": 0.92
                }
            }
            """;

        AiDonorResult donor = objectMapper.readValue(fastApiJson, AiDonorResult.class);

        // Verify in-memory state loaded from AI service
        assertThat(donor.getDonorId()).isEqualTo(42L);
        assertThat(donor.getUserId()).isEqualTo(105L);
        assertThat(donor.getLatitude()).isEqualTo(19.0760);
        assertThat(donor.getLongitude()).isEqualTo(72.8777);
        assertThat(donor.getDistanceKm()).isEqualTo(4.5);
        assertThat(donor.getAiScore()).isEqualTo(0.92);
        assertThat(donor.getPhoneMasked()).isEqualTo("98****3210");
    }

    @Test
    @DisplayName("Serialization to outbound JSON omits WRITE_ONLY fields (userId, latitude, longitude)")
    void testOutboundSerializationOmitsSensitiveWriteOnlyFields() throws Exception {
        AiDonorResult donor = new AiDonorResult();
        donor.setDonorId(42L);
        donor.setUserId(105L);
        donor.setLatitude(19.0760);
        donor.setLongitude(72.8777);
        donor.setName("Jane Donor");
        donor.setDistanceKm(4.5);
        donor.setAiScore(0.92);
        donor.setPhoneMasked("98****3210");

        AiMatchResponse matchResponse = new AiMatchResponse();
        matchResponse.setRequestId(1001L);
        matchResponse.setBloodGroup("A_POSITIVE");
        matchResponse.setUrgency("CRITICAL");
        matchResponse.setTotalFound(1);
        matchResponse.setDonors(List.of(donor));

        String serializedJson = objectMapper.writeValueAsString(matchResponse);
        JsonNode rootNode = objectMapper.readTree(serializedJson);
        JsonNode donorNode = rootNode.path("donors").get(0);

        // Privacy assertion: WRITE_ONLY fields must NOT be leaked in serialized output
        assertThat(donorNode.has("user_id")).isFalse();
        assertThat(donorNode.has("latitude")).isFalse();
        assertThat(donorNode.has("longitude")).isFalse();

        // Safe fields must be present
        assertThat(donorNode.path("donor_id").asLong()).isEqualTo(42L);
        assertThat(donorNode.path("distance_km").asDouble()).isEqualTo(4.5);
        assertThat(donorNode.path("phone_masked").asText()).isEqualTo("98****3210");
        assertThat(donorNode.path("ai_score").asDouble()).isEqualTo(0.92);
    }

    @Test
    @DisplayName("Round-trip serialization preserves all business & notification logic fields")
    void testRoundTripSerializationPreservesNotificationLogic() throws Exception {
        AiDonorResult originalDonor = new AiDonorResult();
        originalDonor.setDonorId(42L);
        originalDonor.setUserId(105L);
        originalDonor.setLatitude(19.0760);
        originalDonor.setLongitude(72.8777);
        originalDonor.setName("Jane Donor");
        originalDonor.setDistanceKm(4.5);
        originalDonor.setAiScore(0.92);
        originalDonor.setPhoneMasked("98****3210");
        originalDonor.setAvailable(true);
        originalDonor.setEligible(true);

        AiMatchResponse originalResponse = new AiMatchResponse();
        originalResponse.setRequestId(1001L);
        originalResponse.setBloodGroup("A_POSITIVE");
        originalResponse.setTotalFound(1);
        originalResponse.setDonors(List.of(originalDonor));

        // 1. Outbound serialization (e.g., if cached or transmitted)
        String json = objectMapper.writeValueAsString(originalResponse);

        // 2. Inbound deserialization back to object
        AiMatchResponse roundTripResponse = objectMapper.readValue(json, AiMatchResponse.class);

        assertThat(roundTripResponse.getTotalFound()).isEqualTo(1);
        assertThat(roundTripResponse.getDonors()).hasSize(1);

        AiDonorResult roundTripDonor = roundTripResponse.getDonors().get(0);

        // Core fields needed by NotificationService (donorRepo.findById(donorId) and distanceKm) are intact
        assertThat(roundTripDonor.getDonorId()).isEqualTo(42L);
        assertThat(roundTripDonor.getDistanceKm()).isEqualTo(4.5);
        assertThat(roundTripDonor.getPhoneMasked()).isEqualTo("98****3210");
        assertThat(roundTripDonor.getAiScore()).isEqualTo(0.92);

        // Sensitive WRITE_ONLY fields are null after round-trip, which is safe since NotificationService
        // resolves DonorProfile directly from MySQL database and never relies on cached/serialized userId or coords
        assertThat(roundTripDonor.getUserId()).isNull();
        assertThat(roundTripDonor.getLatitude()).isNull();
        assertThat(roundTripDonor.getLongitude()).isNull();
    }
}
