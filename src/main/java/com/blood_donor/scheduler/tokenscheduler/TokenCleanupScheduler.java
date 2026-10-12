package com.blood_donor.scheduler.tokenscheduler;

import com.blood_donor.repository.user_repository.PasswordResetTokenRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TokenCleanupScheduler {
    private static final Logger log = LoggerFactory.getLogger(TokenCleanupScheduler.class);
    private final PasswordResetTokenRepository tokenRepository;

    @Scheduled(cron="0 0 3 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = this.tokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());
        if (deleted > 0) {
            log.info("\ud83e\uddf9 Token Cleanup: {} expired/used password reset tokens deleted", (Object)deleted);
        }
    }

    public TokenCleanupScheduler(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
}

