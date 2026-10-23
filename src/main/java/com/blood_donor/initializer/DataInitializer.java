package com.blood_donor.initializer;

import com.blood_donor.initializer.service.IAdminInitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final IAdminInitializerService adminInitializerService;

    @Override
    public void run(String... args) {
        log.info("🚀 Starting data initialization...");

        try {
            // Admin initialization
            adminInitializerService.initializeAdmin();
            log.info("✅ Data initialization completed successfully");

        } catch (IllegalStateException e) {
            log.error("❌ Fatal startup configuration failure in admin bootstrap: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ Data initialization failed: {}", e.getMessage());
        }
    }
}
