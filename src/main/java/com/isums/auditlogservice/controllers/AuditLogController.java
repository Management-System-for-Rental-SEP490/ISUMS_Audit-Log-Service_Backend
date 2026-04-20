package com.isums.auditlogservice.controllers;

import java.util.UUID;

import com.isums.auditlogservice.domains.dtos.AuditLogResponse;
import com.isums.auditlogservice.domains.entities.AuditLog;
import com.isums.auditlogservice.domains.repositories.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogRepository repository;

    public AuditLogController(AuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<AuditLogResponse> getByEventId(@PathVariable UUID eventId) {
        return repository.findByEventId(eventId)
                .map(AuditLogResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<AuditLogResponse> search(
            @RequestParam(required = false) String actorUserId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return repository.findAll(spec(actorUserId, action, resourceType, resourceId, traceId, requestId, serviceName, status), pageable)
                .map(AuditLogResponse::from);
    }

    private Specification<AuditLog> spec(
            String actorUserId,
            String action,
            String resourceType,
            String resourceId,
            String traceId,
            String requestId,
            String serviceName,
            String status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (StringUtils.hasText(actorUserId)) {
                predicate = cb.and(predicate, cb.equal(root.get("actorUserId"), actorUserId));
            }
            if (StringUtils.hasText(action)) {
                predicate = cb.and(predicate, cb.equal(root.get("action"), action));
            }
            if (StringUtils.hasText(resourceType)) {
                predicate = cb.and(predicate, cb.equal(root.get("resourceType"), resourceType));
            }
            if (StringUtils.hasText(resourceId)) {
                predicate = cb.and(predicate, cb.equal(root.get("resourceId"), resourceId));
            }
            if (StringUtils.hasText(traceId)) {
                predicate = cb.and(predicate, cb.equal(root.get("traceId"), traceId));
            }
            if (StringUtils.hasText(requestId)) {
                predicate = cb.and(predicate, cb.equal(root.get("requestId"), requestId));
            }
            if (StringUtils.hasText(serviceName)) {
                predicate = cb.and(predicate, cb.equal(root.get("serviceName"), serviceName));
            }
            if (StringUtils.hasText(status)) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }
            return predicate;
        };
    }
}
