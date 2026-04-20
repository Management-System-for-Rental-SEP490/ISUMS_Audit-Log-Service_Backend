package com.isums.auditlogservice.domains.dtos;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import com.isums.auditlogservice.domains.entities.AuditLog;

public record AuditLogResponse(
        UUID id,
        UUID eventId,
        int eventVersion,
        String traceId,
        String spanId,
        String requestId,
        String correlationId,
        String actorUserId,
        String actorUsername,
        String actorRole,
        String actorType,
        String tenantId,
        String houseId,
        String action,
        String resourceType,
        String resourceId,
        String serviceName,
        String status,
        String clientIp,
        String sourceIp,
        String userAgent,
        String cloudflareRayId,
        Map<String, Object> metadata,
        String errorCode,
        String errorMessage,
        String idempotencyKey,
        OffsetDateTime occurredAt,
        OffsetDateTime ingestedAt,
        OffsetDateTime createdAt) {

    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getEventId(),
                log.getEventVersion(),
                log.getTraceId(),
                log.getSpanId(),
                log.getRequestId(),
                log.getCorrelationId(),
                log.getActorUserId(),
                log.getActorUsername(),
                log.getActorRole(),
                log.getActorType(),
                log.getTenantId(),
                log.getHouseId(),
                log.getAction(),
                log.getResourceType(),
                log.getResourceId(),
                log.getServiceName(),
                log.getStatus(),
                log.getClientIp(),
                log.getSourceIp(),
                log.getUserAgent(),
                log.getCloudflareRayId(),
                log.getMetadata(),
                log.getErrorCode(),
                log.getErrorMessage(),
                log.getIdempotencyKey(),
                log.getOccurredAt(),
                log.getIngestedAt(),
                log.getCreatedAt());
    }
}
