package com.paymentystem.transaction.application.service;

import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.shared.domain.valueobject.Money;
import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.port.input.ProcessTransactionUseCase;
import com.paymentystem.transaction.domain.port.output.TransactionRepositoryPort;
import com.paymentystem.transaction.domain.strategy.TransactionExecutionContext;
import com.paymentystem.transaction.domain.strategy.TransactionStrategy;
import com.paymentystem.transaction.domain.strategy.TransactionStrategyResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTransactionService implements ProcessTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final TransactionStrategyResolver strategyResolver;

    @Override
    @Transactional
    public TransactionResult execute(TransactionCommand command) {

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("► TRANSACTION SERVICE — INICIO");
        log.info("  tipo        : {}", command.type());
        log.info("  origen      : {}", command.sourceAccountId());
        log.info("  destino     : {}", command.targetAccountId());
        log.info("  monto       : {} {}", command.amount(), command.currencyCode());
        log.info("  userId      : {}", command.requestingUserId());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Money amount = new Money(
                command.amount(),
                Currency.getInstance(command.currencyCode())
        );

        Transaction transaction = switch (command.type()) {
            case TRANSFER -> Transaction.createTransfer(
                    command.sourceAccountId(),
                    command.targetAccountId(),
                    amount
            );
            default -> throw new DomainException("TRANSACTION_UNSUPPORTED_TYPE",
                    "Tipo no soportado: " + command.type());
        };

        log.info("  [1/4] Transacción creada en dominio");
        log.info("        id            : {}", transaction.getId());
        log.info("        correlationId : {}", transaction.getCorrelationId());
        log.info("        status        : {}", transaction.getStatus());

        transactionRepository.save(transaction);
        log.info("  [2/4] Transacción persistida como PENDING en BD");

        TransactionStrategy strategy = strategyResolver.resolve(command.type());
        log.info("  [3/4] Strategy resuelta → {}", strategy.getClass().getSimpleName());

        TransactionExecutionContext context = new TransactionExecutionContext(
                command.sourceAccountId(),
                command.targetAccountId(),
                amount,
                command.requestingUserId()
        );

        try {
            strategy.execute(transaction, context);
        } catch (Exception ex) {
            transaction.markFailed(ex.getMessage());
            log.error("  [✗] Strategy falló: {}", ex.getMessage());
        }

        Transaction saved = transactionRepository.save(transaction);
        log.info("  [4/4] Transacción persistida como {} en BD", saved.getStatus());
        log.info("◄ TRANSACTION SERVICE — FIN → status: {}", saved.getStatus());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return new TransactionResult(
                saved.getId(),
                saved.getStatus().name(),
                saved.getAmount().toString(),
                saved.getCorrelationId(),
                saved.getFailureReason()
        );
    }
}