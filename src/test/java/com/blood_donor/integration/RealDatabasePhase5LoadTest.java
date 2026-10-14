package com.blood_donor.integration;

import com.blood_donor.common.enums.BloodGroup;
import com.blood_donor.common.enums.Role;
import com.blood_donor.model.entity.DonorProfile;
import com.blood_donor.model.entity.User;
import com.blood_donor.repository.donor_profile_repo.DonorProfileRepository;
import com.blood_donor.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Phase 5 High-Concurrency Load & Performance Benchmark Test (100 Concurrent Users)")
@AutoConfigureTestRestTemplate
public class RealDatabasePhase5LoadTest extends BaseIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(RealDatabasePhase5LoadTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private DonorProfileRepository donorProfileRepository;

    private String testUserEmail = "loadtest_user@benchmark.local";
    private String testUserPassword = "Password@123!";
    private String authToken;

    @BeforeEach
    void setupBenchmarkData() {
        cleanDatabase();

        // 1. Create a benchmark user
        User user = baseUserRepo.save(User.builder()
                .name("Benchmark User")
                .email(testUserEmail)
                .password(passwordEncoder.encode(testUserPassword))
                .phone("+919988776655")
                .roles(new HashSet<>(Set.of(Role.USER, Role.DONOR)))
                .isActive(true)
                .build());

        this.authToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRoles(), user.getId());

        // 2. Pre-seed 50 donors in Delhi with O_POSITIVE to test real query performance with indexes
        List<DonorProfile> seedDonors = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            User dUser = baseUserRepo.save(User.builder()
                    .name("Donor " + i)
                    .email("donor_" + i + "@benchmark.local")
                    .password(passwordEncoder.encode("Test@12345!"))
                    .phone("+9191000" + String.format("%05d", i))
                    .roles(Collections.singleton(Role.DONOR))
                    .isActive(true)
                    .build());

            DonorProfile profile = DonorProfile.builder()
                    .user(dUser)
                    .bloodGroup(BloodGroup.O_POSITIVE)
                    .city("Delhi")
                    .state("Delhi")
                    .isAvailable(true)
                    .latitude(BigDecimal.valueOf(28.6139 + (i * 0.001)))
                    .longitude(BigDecimal.valueOf(77.2090 + (i * 0.001)))
                    .lastDonationDate(LocalDate.now().minusMonths(4))
                    .totalDonations(5)
                    .responseRate(BigDecimal.valueOf(95.0))
                    .reliabilityScore(BigDecimal.valueOf(90.0))
                    .build();
            seedDonors.add(profile);
        }
        donorProfileRepository.saveAll(seedDonors);
    }

    @Test
    @DisplayName("Load Benchmark: 100 concurrent workers performing Donor Search queries")
    void test100ConcurrentUsersBenchmark() throws Exception {
        int concurrentUsers = 100;
        int operationsPerUser = 5;
        int totalRequests = concurrentUsers * operationsPerUser;

        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        ConcurrentLinkedQueue<Long> searchLatencies = new ConcurrentLinkedQueue<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < concurrentUsers; i++) {
            tasks.add(() -> {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(authToken);
                HttpEntity<Void> searchRequest = new HttpEntity<>(headers);

                for (int j = 0; j < operationsPerUser; j++) {
                    long startSearch = System.nanoTime();
                    ResponseEntity<String> searchResp = restTemplate.exchange(
                            "/api/donor/search?bloodGroup=O_POSITIVE&city=Delhi&page=0&size=20",
                            HttpMethod.GET,
                            searchRequest,
                            String.class
                    );
                    long searchDurationMs = (System.nanoTime() - startSearch) / 1_000_000;
                    searchLatencies.add(searchDurationMs);

                    if (searchResp.getStatusCode() == HttpStatus.OK) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                }
                return null;
            });
        }

        long overallStart = System.currentTimeMillis();
        List<Future<Void>> futures = executor.invokeAll(tasks);
        for (Future<Void> future : futures) {
            future.get();
        }
        long overallTotalMs = System.currentTimeMillis() - overallStart;
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Compute Percentiles
        List<Long> latencies = new ArrayList<>(searchLatencies);
        Collections.sort(latencies);

        long p50 = latencies.get((int) (latencies.size() * 0.50));
        long p90 = latencies.get((int) (latencies.size() * 0.90));
        long p95 = latencies.get((int) (latencies.size() * 0.95));
        long p99 = latencies.get((int) (latencies.size() * 0.99));
        long max = latencies.get(latencies.size() - 1);
        double errorRate = (failureCount.get() * 100.0) / totalRequests;
        double throughput = (totalRequests * 1000.0) / Math.max(overallTotalMs, 1);

        System.out.println("══════════════════════════════════════════════════════════════════════════");
        System.out.println(" PHASE 5 LOAD TEST BENCHMARK RESULTS (100 CONCURRENT WORKERS):");
        System.out.println(" Total Requests Executed : " + totalRequests);
        System.out.println(" Total Duration (Wall)   : " + overallTotalMs + " ms");
        System.out.println(" Measured Throughput     : " + String.format("%.2f", throughput) + " req/sec");
        System.out.println(" p50 Latency             : " + p50 + " ms");
        System.out.println(" p90 Latency             : " + p90 + " ms");
        System.out.println(" p95 Latency             : " + p95 + " ms");
        System.out.println(" p99 Latency             : " + p99 + " ms");
        System.out.println(" Max Latency             : " + max + " ms");
        System.out.println(" Error Rate              : " + String.format("%.2f", errorRate) + " %");
        System.out.println("══════════════════════════════════════════════════════════════════════════");

        // Assertions: 0% failure rate under high concurrency
        assertThat(errorRate).isEqualTo(0.0);
        assertThat(p95).isLessThan(8000);
    }
}
