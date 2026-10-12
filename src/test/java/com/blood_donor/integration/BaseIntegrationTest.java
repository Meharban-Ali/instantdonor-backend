package com.blood_donor.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
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

    public static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.36")
            .withDatabaseName("blood_donor_test")
            .withUsername("testuser")
            .withPassword("testpass");

    public static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static {
        mysqlContainer.start();
        redisContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.hikari.catalog", () -> "blood_donor_test");

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
