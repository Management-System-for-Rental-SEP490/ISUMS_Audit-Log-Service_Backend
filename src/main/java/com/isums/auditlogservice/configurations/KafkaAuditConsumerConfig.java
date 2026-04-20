package com.isums.auditlogservice.configurations;

import com.isums.observability.config.ObservabilityProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaAuditConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaAuditConsumerConfig.class);

    @Bean
    public CommonErrorHandler auditErrorHandler(
            KafkaOperations<Object, Object> kafkaOperations,
            @Value("${isums.audit.dlq-topic:audit.events.dlq}") String dlqTopic) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaOperations,
                (ConsumerRecord<?, ?> record, Exception ex) -> {
                    log.error("Sending audit event to DLQ topic={} partition={} offset={}",
                            record.topic(), record.partition(), record.offset(), ex);
                    return new TopicPartition(dlqTopic, record.partition());
                });
        ExponentialBackOff backOff = new ExponentialBackOff(1_000L, 2.0);
        backOff.setMaxInterval(30_000L);
        backOff.setMaxElapsedTime(120_000L);
        return new DefaultErrorHandler(recoverer, backOff);
    }
}
