package com.blood_donor.repository.user_repository;

import com.blood_donor.model.entity.PasswordResetToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository
extends JpaRepository<PasswordResetToken, Long> {
    public Optional<PasswordResetToken> findByToken(String var1);

    @Modifying
    @Query(value="DELETE FROM PasswordResetToken prt WHERE prt.user.id = :userId")
    public void deleteAllByUserId(@Param(value="userId") Long var1);

    @Modifying
    @Query(value="DELETE FROM PasswordResetToken prt\nWHERE prt.expiresAt < :now\nOR prt.used = true\n")
    public int deleteExpiredAndUsedTokens(@Param(value="now") LocalDateTime var1);

    public boolean existsByUserIdAndUsedFalseAndExpiresAtAfter(Long var1, LocalDateTime var2);
}

