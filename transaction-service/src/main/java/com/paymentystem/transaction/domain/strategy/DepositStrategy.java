package com.paymentystem.transaction.domain.strategy;

import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.model.TransactionType;
import com.paymentystem.transaction.domain.port.output.AccountServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PATRÓN: Strategy — implementación para DEPOSIT.
 * Solo acredita la cuenta destino, sin débito de origen.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DepositStrategy implements TransactionStrategy {

    private final AccountServicePort accountService;

    @Override
    public TransactionType type() {
        return TransactionType.DEPOSIT;
    }

    @Override
    public void execute(Transaction transaction, TransactionExecutionContext context) {
        log.info("Ejecutando DEPOSIT — correlationId: {}", transaction.getCorrelationId());

        accountService.credit(
                context.sourceAccountId(),
                context.amount(),
                transaction.getCorrelationId()
        );

        transaction.markCompleted();
        log.info("DEPOSIT completado — id: {}", transaction.getId());
    }
}