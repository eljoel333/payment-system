package com.paymentystem.transaction.infrastructure.adapter.output.messaging;

import com.paymentystem.shared.domain.event.DomainEvent;
import com.paymentystem.transaction.infrastructure.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * PATRÓN: Adapter de salida — publica eventos de dominio en Kafka.
 * El dominio no sabe que existe Kafka — solo conoce el puerto EventPublisherPort.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    public void publishTransferCompensated(DomainEvent event) {
        publish(KafkaConfig.TRANSFER_COMPENSATED, event.getCorrelationId(), event);
    }

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTransferInitiated(DomainEvent event) {
        publish(KafkaConfig.TRANSFER_INITIATED, event.getCorrelationId(), event);
    }

    public void publishTransferCompleted(DomainEvent event) {
        publish(KafkaConfig.TRANSFER_COMPLETED, event.getCorrelationId(), event);
    }

    public void publishTransferFailed(DomainEvent event) {
        publish(KafkaConfig.TRANSFER_FAILED, event.getCorrelationId(), event);
    }

    private void publish(String topic, String key, DomainEvent event) {
        log.info("📤 Publicando evento {} en topic {} — correlationId: {}",
                event.getEventType(), topic, event.getCorrelationId());
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Evento publicado en topic {} — offset: {}",
                                topic, result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Error publicando en topic {}: {}", topic, ex.getMessage());
                    }
                });
    }
}