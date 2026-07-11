package com.paymentystem.account.infrastructure.adapter.input.kafka;

import com.paymentystem.account.domain.port.input.DepositUseCase;
import com.paymentystem.shared.domain.event.TransferCompensatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * PATRÓN: Saga compensación
 * Consume TransferCompensatedEvent y devuelve el dinero a la cuenta origen.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompensationEventConsumer {

    private final DepositUseCase depositUseCase;

    @KafkaListener(
            topics = "transfer.compensated",
            groupId = "account-service",
            containerFactory = "compensationListenerFactory"
    )
    public void onTransferCompensated(@Payload TransferCompensatedEvent event) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("📥 COMPENSACION — TransferCompensatedEvent");
        log.info("  transactionId : {}", event.getTransactionId());
        log.info("  origen        : {}", event.getSourceAccountId());
        log.info("  monto         : {} {}", event.getAmount(), event.getCurrency());
        log.info("  razon         : {}", event.getReason());

        try {
            // Devuelve el dinero a la cuenta origen
            depositUseCase.execute(new DepositUseCase.DepositCommand(
                    event.getSourceAccountId(),
                    "system-compensation",
                    event.getAmount(),
                    event.getCorrelationId(),
                    event.getTransactionId()
            ));
            log.info("  ✅ Compensacion exitosa — dinero devuelto a cuenta origen");
        } catch (Exception ex) {
            log.error("  ❌ Error en compensacion: {}", ex.getMessage());
            throw ex;
        }
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}