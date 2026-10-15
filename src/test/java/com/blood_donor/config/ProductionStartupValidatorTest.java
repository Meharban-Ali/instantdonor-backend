package com.blood_donor.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.*;

class ProductionStartupValidatorTest {

    @Test
    @DisplayName("Prod profile: Startup fails if AI_SERVICE_API_KEY is missing or blank")
    void testProd_FailsWhenAiKeyMissing() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");
        env.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db");
        env.setProperty("spring.datasource.username", "root");
        env.setProperty("spring.datasource.password", "secret");
        env.setProperty("spring.data.redis.host", "localhost");
        env.setProperty("spring.data.redis.port", "6379");
        env.setProperty("jwt.secret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!");

        ProductionStartupValidator validator = new ProductionStartupValidator(env);
        // Set jwtSecret via reflection or field
        org.springframework.test.util.ReflectionTestUtils.setField(
                validator, "jwtSecret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!"
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validator.run(new DefaultApplicationArguments()));
        assertTrue(ex.getMessage().contains("AI_SERVICE_API_KEY"), "Error must mention AI_SERVICE_API_KEY");
    }

    @Test
    @DisplayName("Prod profile: Startup fails if AI_SERVICE_API_KEY is shorter than 32 characters")
    void testProd_FailsWhenAiKeyTooShort() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");
        env.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db");
        env.setProperty("spring.datasource.username", "root");
        env.setProperty("spring.datasource.password", "secret");
        env.setProperty("spring.data.redis.host", "localhost");
        env.setProperty("spring.data.redis.port", "6379");
        env.setProperty("jwt.secret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!");
        env.setProperty("app.ai-service.api-key", "short-key-only-20-chars");

        ProductionStartupValidator validator = new ProductionStartupValidator(env);
        org.springframework.test.util.ReflectionTestUtils.setField(
                validator, "jwtSecret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!"
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validator.run(new DefaultApplicationArguments()));
        assertTrue(ex.getMessage().contains("too short"), "Error must state key is too short");
        assertTrue(ex.getMessage().contains("32"), "Error must specify 32 characters requirement");
    }

    @Test
    @DisplayName("Prod profile: Startup fails if AI_SERVICE_API_KEY uses known default value")
    void testProd_FailsWhenAiKeyUsesKnownDefault() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");
        env.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db");
        env.setProperty("spring.datasource.username", "root");
        env.setProperty("spring.datasource.password", "secret");
        env.setProperty("spring.data.redis.host", "localhost");
        env.setProperty("spring.data.redis.port", "6379");
        env.setProperty("jwt.secret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!");
        env.setProperty("app.ai-service.api-key", "local-dev-api-key-change-in-production");

        ProductionStartupValidator validator = new ProductionStartupValidator(env);
        org.springframework.test.util.ReflectionTestUtils.setField(
                validator, "jwtSecret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!"
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> validator.run(new DefaultApplicationArguments()));
        assertTrue(ex.getMessage().contains("known insecure default"), "Error must block known default");
    }

    @Test
    @DisplayName("Prod profile: Startup succeeds with secure 32+ character AI key")
    void testProd_SucceedsWithSecureKey() {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");
        env.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/db");
        env.setProperty("spring.datasource.username", "root");
        env.setProperty("spring.datasource.password", "secret");
        env.setProperty("spring.data.redis.host", "localhost");
        env.setProperty("spring.data.redis.port", "6379");
        env.setProperty("jwt.secret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!");
        env.setProperty("app.ai-service.api-key", "a_very_secure_random_production_api_key_32_chars_long");

        ProductionStartupValidator validator = new ProductionStartupValidator(env);
        org.springframework.test.util.ReflectionTestUtils.setField(
                validator, "jwtSecret", "ThisIsAVeryLongSecureJwtSecretKeyWithAtLeast32BytesLength!"
        );

        assertDoesNotThrow(() -> validator.run(new DefaultApplicationArguments()));
    }
}
