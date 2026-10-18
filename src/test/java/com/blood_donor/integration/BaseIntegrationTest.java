package com.blood_donor.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
public abstract class BaseIntegrationTest {

    static {
        if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
            System.setProperty("DOCKER_HOST", "npipe:////./pipe/dockerDesktopLinuxEngine");
            System.setProperty("api.version", "1.44");
        }
    }

    public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("blood_donor_test")
            .withUsername("testuser")
            .withPassword("testpass");

    public static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static {
        postgresContainer.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @org.springframework.beans.factory.annotation.Autowired
    protected com.blood_donor.repository.donor_repository.response_repository.DonorRequestResponseRepository baseResponseRepo;

    @org.springframework.beans.factory.annotation.Autowired
    protected com.blood_donor.repository.donor_repository.request_repository.BloodRequestRepository baseRequestRepo;

    @org.springframework.beans.factory.annotation.Autowired
    protected com.blood_donor.repository.donor_profile_repo.DonorProfileRepository baseDonorRepo;

    @org.springframework.beans.factory.annotation.Autowired
    protected com.blood_donor.repository.user_repository.UserRepository baseUserRepo;

    protected void cleanDatabase() {
        baseResponseRepo.deleteAll();
        baseRequestRepo.deleteAll();
        baseDonorRepo.deleteAll();
        baseUserRepo.deleteAll();
    }
}
