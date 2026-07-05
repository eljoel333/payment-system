package com.paymentystem.transaction.domain.strategy;

import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.model.TransactionType;
import com.paymentystem.transaction.domain.port.output.AccountServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PATRÓN: Strategy — implementación para TRANSFER.
 * Debita la cuenta origen y acredita la cuenta destino.
 * Si el débito falla, nunca llega al crédito — consistencia garantizada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransferStrategy implements TransactionStrategy {

    private final AccountServicePort accountService;

    @Override
    public TransactionType type() {
        return TransactionType.TRANSFER;
    }

    @Override
    public void execute(Transaction transaction, TransactionExecutionContext context) {
        log.info("Ejecutando TRANSFER — correlationId: {}", transaction.getCorrelationId());

        log.info("  ┌─ TransferStrategy.execute()");
        log.info("  │  correlationId : {}", transaction.getCorrelationId());
        log.info("  │  monto         : {}", context.amount());

        log.info("  │  → [STEP 1] Debitando {} de cuenta origen {}", context.amount(), context.sourceAccountId());

        // Paso 1 — debita cuenta origen
        accountService.debit(
                context.sourceAccountId(),
                context.amount(),
                transaction.getCorrelationId()
        );
        log.info("  │  ✓ [STEP 1] Débito exitoso");

        log.info("  │  → [STEP 2] Acreditando {} a cuenta destino {}",
                context.amount(), context.targetAccountId());


        // Paso 2 — acredita cuenta destino
        // Si este paso falla después del débito, entra el Saga de compensación
        accountService.credit(
                context.targetAccountId(),
                context.amount(),
                transaction.getCorrelationId()
        );

        log.info("  │  ✓ [STEP 2] Crédito exitoso");

        transaction.markCompleted();
        log.info("  └─ TransferStrategy completada ✓ — id: {}", transaction.getId());
    }
}