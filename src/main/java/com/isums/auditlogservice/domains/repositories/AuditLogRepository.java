package com.isums.auditlogservice.domains.repositories;

import java.util.Optional;
import java.util.UUID;

import com.isums.auditlogservice.domains.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID>, JpaSpecificationExecutor<AuditLog> {

    boolean existsByEventId(UUID eventId);

    Optional<AuditLog> findByEventId(UUID eventId);
}
