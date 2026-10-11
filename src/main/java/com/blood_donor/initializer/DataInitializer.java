package com.blood_donor.initializer; // ✅ Bug Fix 1

import com.blood_donor.initializer.service.IAdminInitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// ✅ S: Sirf initialization orchestration ka kaam
// ✅ O: Naya initializer = naya CommandLineRunner — yahan change nahi
// ✅ D: IAdminInitializerService interface pe depend
//
// Explanation:
// App start hone par ek baar run hota hai
// @Order(1) — sabse pehle run hoga
// @Profile("!test") — test mein skip hoga
// Actual logic AdminInitializerService mein hai
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1) // ✅ Sabse pehle run hoga
@Profile("!test") // ✅ Test environment mein skip
public class DataInitializer implements CommandLineRunner {

    // ✅ D: Interface pe depend — concrete class nahi
    private final IAdminInitializerService adminInitializerService;

    // ✅ S: Sirf service call karo — logic yahan nahi
    // Explanation:
    // CommandLineRunner.run() → app start pe ek baar execute
    // AdminInitializerService.initializeAdmin() → actual logic
    // Alag concerns alag classes mein — SRP follow
    @Override
    public void run(String... args) {
        log.info("🚀 Starting data initialization...");

        try {
            // Admin initialization
            adminInitializerService.initializeAdmin();
            log.info("✅ Data initialization completed successfully");

        } catch (Exception e) {
            // ✅ Startup fail mat karo — warn karo
            // Explanation:
            // Admin creation fail hone pe app crash nahi hona chahiye
            // Log karo aur continue karo
            log.error("❌ Data initialization failed: {}", e.getMessage());
        }
    }
}