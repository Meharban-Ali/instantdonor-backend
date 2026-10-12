package com.blood_donor.repository.user_repository;

import com.blood_donor.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository
extends JpaRepository<AuditLog, Long> {
}

