package com.isums.auditlogservice.infrastructures.listeners;

import com.isums.auditlogservice.services.AuditLogIngestionService;
import com.isums.observability.audit.AuditEvent;
import com.isums.observability.kafka.KafkaMdcSupport;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditLogIngestionService ingestionService;

    public AuditEventListener(AuditLogIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @KafkaListener(topics = "${isums.audit.topic:audit.events}", groupId = "${spring.kafka.consumer.group-id:audit-log-service}")
    public void consume(ConsumerRecord<String, AuditEvent> record, Acknowledgment acknowledgment) {
        try {
            KafkaMdcSupport.copyHeadersToMdc(record);
            AuditEvent event = record.value();
            boolean inserted = ingestionService.ingest(event);
            if (inserted) {
                log.info("audit_event ingested eventId={} action={} serviceName={}",
                        event.getEventId(), event.getAction(), event.getServiceName());
            } else {
                log.info("audit_event duplicate_ignored eventId={}", event.getEventId());
            }
            acknowledgment.acknowledge();
        } finally {
            MDC.clear();
        }
    }
}
