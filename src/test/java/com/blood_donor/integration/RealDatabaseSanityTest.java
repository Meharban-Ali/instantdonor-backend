package com.blood_donor.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Real DB & Cache Sanity Integration Test (Testcontainers)")
public class RealDatabaseSanityTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("Verify Flyway migrations V1 & V2 applied to MySQL and tables exist")
    void testFlywayMigrationsApplied() {
        Integer userTableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'blood_donor_test' AND table_name = 'users'",
                Integer.class
        );
        assertThat(userTableCount).isEqualTo(1);

        Integer donorIndexCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = 'blood_donor_test' AND table_name = 'donor_profiles' AND index_name = 'idx_donor_city_group_avail'",
                Integer.class
        );
        assertThat(donorIndexCount).isGreaterThan(0);
    }

    @Test
    @DisplayName("Verify Redis container is responsive")
    void testRedisConnectivity() {
        redisTemplate.opsForValue().set("test:tc:key", "testcontainers-active");
        String val = redisTemplate.opsForValue().get("test:tc:key");
        assertThat(val).isEqualTo("testcontainers-active");
        redisTemplate.delete("test:tc:key");
    }
}
