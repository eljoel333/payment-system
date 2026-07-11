package com.paymentystem.transaction.infrastructure.adapter.input.kafka;

import com.paymentystem.shared.domain.event.TransferCompensatedEvent;
import com.paymentystem.shared.domain.event.TransferCompletedEvent;
import com.paymentystem.shared.domain.event.TransferFailedEvent;
import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.port.output.TransactionRepositoryPort;
import com.paymentystem.transaction.infrastructure.adapter.output.messaging.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TransferResultConsumer {

    private final TransactionRepositoryPort transactionRepository;
    private final KafkaEventPublisher kafkaEventPublisher;  // ← agrega este


    @KafkaListener(
            topics = "transfer.failed",
            groupId = "transaction-service",
            containerFactory = "transferFailedListenerFactory"
    )
    public void onTransferFailed(@Payload TransferFailedEvent event) {
        log.info("📥 TRANSACTION SERVICE — TransferFailed");
        log.info("  transactionId : {}", event.getTransactionId());
        log.info("  reason        : {}", event.getReason());
        log.info("  failedStep    : {}", event.getFailedStep());

        transactionRepository.findById(event.getTransactionId())
                .ifPresentOrElse(
                        transaction -> {
                            transaction.markFailed(event.getReason());
                            transactionRepository.save(transaction);
                            log.info("  ❌ Transacción {} marcada como FAILED", event.getTransactionId());

                            // PATRÓN: Saga compensación
                            // Si el fallo fue en el crédito, devuelve el dinero al origen
                            if ("CREDIT".equals(event.getFailedStep())) {
                                log.info("  🔄 Iniciando compensación...");
                                TransferCompensatedEvent compensatedEvent = new TransferCompensatedEvent(
                                        transaction.getId(),
                                        transaction.getSourceAccountId(),
                                        transaction.getAmount().amount(),
                                        transaction.getAmount().currency().getCurrencyCode(),
                                        event.getReason(),
                                        event.getCorrelationId()
                                );
                                kafkaEventPublisher.publishTransferCompensated(compensatedEvent);
                            }
                        },
                        () -> log.warn("  Transaccion no encontrada: {}", event.getTransactionId())
                );
    }

    @KafkaListener(
            topics = "transfer.completed",
            groupId = "transaction-service",
            containerFactory = "kafkaListenerContainerFactory"

    )
    public void onTransferCompleted(@Payload TransferCompletedEvent event) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📥 TRANSACTION SERVICE — TransferCompleted");
        log.info("  transactionId : {}", event.getTransactionId());
        log.info("  correlationId : {}", event.getCorrelationId());

        transactionRepository.findById(event.getTransactionId())
                .ifPresentOrElse(
                        transaction -> {
                            transaction.markCompleted();
                            transactionRepository.save(transaction);
                            log.info("  ✅ Transacción {} marcada como COMPLETED",
                                    event.getTransactionId());
                        },
                        () -> log.warn("  ⚠️ Transacción no encontrada: {}",
                                event.getTransactionId())
                );
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

}