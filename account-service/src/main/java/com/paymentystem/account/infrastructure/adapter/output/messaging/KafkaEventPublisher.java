package com.paymentystem.account.infrastructure.adapter.output.messaging;

import com.paymentystem.shared.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, String key, DomainEvent event) {
        log.info("📤 Publicando evento {} en topic {} — correlationId: {}",
                event.getEventType(), topic, event.getCorrelationId());
        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Evento publicado — offset: {}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Error publicando en {}: {}", topic, ex.getMessage());
                    }
                });
    }
}