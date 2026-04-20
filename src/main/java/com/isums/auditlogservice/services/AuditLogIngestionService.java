package com.isums.auditlogservice.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.isums.auditlogservice.domains.entities.AuditLog;
import com.isums.auditlogservice.domains.repositories.AuditLogRepository;
import com.isums.observability.audit.AuditEvent;
import com.isums.observability.masking.SensitiveDataMasker;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuditLogIngestionService {

    private final AuditLogRepository repository;

    public AuditLogIngestionService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean ingest(AuditEvent event) {
        if (event.getEventId() == null) {
            throw new IllegalArgumentException("Audit eventId is required");
        }
        if (repository.existsByEventId(event.getEventId())) {
            return false;
        }
        repository.save(toEntity(event));
        return true;
    }

    private AuditLog toEntity(AuditEvent event) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        AuditLog log = new AuditLog();
        log.setEventId(event.getEventId());
        log.setEventVersion(event.getEventVersion());
        log.setTraceId(event.getTraceId());
        log.setSpanId(event.getSpanId());
        log.setRequestId(event.getRequestId());
        log.setCorrelationId(event.getCorrelationId());
        log.setActorUserId(event.getActorUserId());
        log.setActorUsername(event.getActorUsername());
        log.setActorRole(event.getActorRole());
        log.setActorType(event.getActorType());
        log.setTenantId(event.getTenantId());
        log.setHouseId(event.getHouseId());
        log.setAction(requireText(event.getAction(), "action"));
        log.setResourceType(event.getResourceType());
        log.setResourceId(event.getResourceId());
        log.setServiceName(requireText(event.getServiceName(), "serviceName"));
        log.setStatus(event.getStatus() == null ? "SUCCESS" : event.getStatus().name());
        log.setClientIp(event.getClientIp());
        log.setSourceIp(event.getSourceIp());
        log.setUserAgent(SensitiveDataMasker.maskString(event.getUserAgent()));
        log.setCloudflareRayId(event.getCloudflareRayId());
        log.setMetadata(SensitiveDataMasker.maskMap(event.getMetadata()));
        log.setErrorCode(event.getErrorCode());
        log.setErrorMessage(SensitiveDataMasker.maskString(event.getErrorMessage()));
        log.setIdempotencyKey(event.getIdempotencyKey());
        log.setOccurredAt(event.getOccurredAt() == null ? now : event.getOccurredAt());
        log.setIngestedAt(now);
        log.setCreatedAt(now);
        return log;
    }

    private String requireText(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Audit " + field + " is required");
        }
        return value;
    }
}
