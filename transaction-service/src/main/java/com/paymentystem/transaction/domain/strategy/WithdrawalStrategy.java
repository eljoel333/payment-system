package com.paymentystem.transaction.domain.strategy;

import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.model.TransactionType;
import com.paymentystem.transaction.domain.port.output.AccountServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PATRÓN: Strategy — implementación para WITHDRAWAL.
 * Solo debita la cuenta origen, sin acreditar destino.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalStrategy implements TransactionStrategy {

    private final AccountServicePort accountService;

    @Override
    public TransactionType type() {
        return TransactionType.WITHDRAWAL;
    }

    @Override
    public void execute(Transaction transaction, TransactionExecutionContext context) {
        log.info("Ejecutando WITHDRAWAL — correlationId: {}", transaction.getCorrelationId());

        accountService.debit(
                context.sourceAccountId(),
                context.amount(),
                transaction.getCorrelationId()
        );

        transaction.markCompleted();
        log.info("WITHDRAWAL completado — id: {}", transaction.getId());
    }
}