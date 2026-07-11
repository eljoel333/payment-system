package com.paymentystem.account.infrastructure.adapter.input.kafka;

import com.paymentystem.account.application.service.DebitService;
import com.paymentystem.account.application.service.DepositService;
import com.paymentystem.account.domain.port.input.DebitUseCase;
import com.paymentystem.account.domain.port.input.DepositUseCase;
import com.paymentystem.account.infrastructure.adapter.output.messaging.KafkaEventPublisher;
import com.paymentystem.shared.domain.event.AccountDebitedEvent;
import com.paymentystem.shared.domain.event.TransferFailedEvent;
import com.paymentystem.shared.domain.event.TransferInitiatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * PATRÓN: Adapter de entrada — consume eventos de Kafka.
 * El dominio no sabe que existe Kafka — el consumer traduce
 * el evento a un comando del dominio.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferEventConsumer {

    private final DebitUseCase debitUseCase;
    private final DepositUseCase depositUseCase;
    private final KafkaEventPublisher kafkaEventPublisher;  // ← agrega este


    /**
     * Consume TransferInitiatedEvent — ejecuta el débito en cuenta origen.
     */
    @KafkaListener(
            topics = "transfer.initiated",
            groupId = "account-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTransferInitiated(@Payload TransferInitiatedEvent event) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📥 ACCOUNT SERVICE — Consumiendo TransferInitiatedEvent");
        log.info("  transactionId : {}", event.getTransactionId());
        log.info("  correlationId : {}", event.getCorrelationId());
        log.info("  origen        : {}", event.getSourceAccountId());
        log.info("  destino       : {}", event.getTargetAccountId());
        log.info("  monto         : {} {}", event.getAmount(), event.getCurrency());

        try {
            debitUseCase.execute(new DebitUseCase.DebitCommand(
                    event.getSourceAccountId(),
                    event.getTargetAccountId(),
                    "system",
                    event.getAmount(),
                    event.getCorrelationId(),
                    event.getTransactionId()  // ← agrega este
            ));
            log.info("  ✅ Débito ejecutado exitosamente");
        } catch (Exception ex) {
            log.error("  ❌ Error ejecutando débito: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Consume AccountDebitedEvent — ejecuta el crédito en cuenta destino.
     */
    @KafkaListener(
            topics = "account.debited",
            groupId = "account-service",
            containerFactory = "accountDebitedListenerFactory"
    )
    public void onAccountDebited(@Payload AccountDebitedEvent event) {
        log.info("📥 ACCOUNT SERVICE — Consumiendo AccountDebitedEvent");
        log.info("  transactionId : {}", event.getTransactionId());
        log.info("  destino       : {}", event.getTargetAccountId());
        log.info("  monto         : {} {}", event.getAmount(), event.getCurrency());

        try {
            depositUseCase.execute(new DepositUseCase.DepositCommand(
                    event.getTargetAccountId(),
                    "system",
                    event.getAmount(),
                    event.getCorrelationId(),
                    event.getTransactionId()
            ));
            log.info("  ✅ Crédito ejecutado exitosamente");
        } catch (Exception ex) {
            log.error("  ❌ Error ejecutando crédito: {} — iniciando compensación", ex.getMessage());

            // PATRÓN: Saga compensación — publica evento de fallo para revertir el débito
            TransferFailedEvent failedEvent = new TransferFailedEvent(
                    event.getTransactionId(),
                    ex.getMessage(),
                    "CREDIT",
                    event.getCorrelationId()
            );
            kafkaEventPublisher.publish("transfer.failed", event.getCorrelationId(), failedEvent);
            throw ex;
        }
    }
}