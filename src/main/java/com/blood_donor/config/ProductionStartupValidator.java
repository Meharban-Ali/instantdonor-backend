package com.blood_donor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ProductionStartupValidator implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ProductionStartupValidator.class);
    private final Environment environment;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    public ProductionStartupValidator(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean isProd = Arrays.asList(this.environment.getActiveProfiles()).contains("prod");

        // Validate JWT Secret
        if (this.jwtSecret == null || this.jwtSecret.isBlank()) {
            throw new IllegalStateException("FATAL STARTUP ERROR: JWT secret (jwt.secret / JWT_SECRET) is missing or blank.");
        }

        // HMAC-SHA384 requires at least 48 bytes (384 bits) of key material
        byte[] secretBytes = this.jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 48) {
            throw new IllegalStateException(
                    "FATAL STARTUP ERROR: JWT secret is too short for HMAC-SHA384. Minimum required length is 48 bytes (384 bits), found "
                            + secretBytes.length + " bytes."
            );
        }

        if (isProd) {
            log.info("Production profile detected — validating required production environment variables...");
            List<String> missingVars = new ArrayList<>();

            this.checkRequiredProperty("DB_URL", "spring.datasource.url", missingVars);
            this.checkRequiredProperty("DB_USERNAME", "spring.datasource.username", missingVars);
            this.checkRequiredProperty("DB_PASSWORD", "spring.datasource.password", missingVars);
            this.checkRequiredProperty("REDIS_HOST", "spring.data.redis.host", missingVars);
            this.checkRequiredProperty("REDIS_PORT", "spring.data.redis.port", missingVars);

            if (!missingVars.isEmpty()) {
                throw new IllegalStateException(
                        "FATAL STARTUP ERROR: Missing required production environment variables / configuration properties: "
                                + missingVars
                );
            }
            log.info("Production configuration successfully validated (all required secrets and hosts verified).");
        }
    }

    private void checkRequiredProperty(String envName, String propName, List<String> missingVars) {
        String propValue = this.environment.getProperty(propName);
        if (propValue == null || propValue.isBlank() || propValue.contains("${" + envName + "}")) {
            missingVars.add(envName + " (property: " + propName + ")");
        }
    }
}
