package com.paymentystem.transaction.application.service;

import com.paymentystem.shared.domain.event.TransferInitiatedEvent;
import com.paymentystem.shared.domain.valueobject.Money;
import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.port.input.ProcessTransactionUseCase;
import com.paymentystem.transaction.domain.port.output.TransactionRepositoryPort;
import com.paymentystem.transaction.infrastructure.adapter.output.messaging.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTransactionService implements ProcessTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    @Transactional
    public TransactionResult execute(TransactionCommand command) {
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("► TRANSACTION SERVICE — INICIO");
        log.info("  tipo        : {}", command.type());
        log.info("  origen      : {}", command.sourceAccountId());
        log.info("  destino     : {}", command.targetAccountId());
        log.info("  monto       : {} {}", command.amount(), command.currencyCode());

        Money amount = new Money(
                command.amount(),
                Currency.getInstance(command.currencyCode())
        );

        // PATRÓN: Factory Method
        Transaction transaction = Transaction.createTransfer(
                command.sourceAccountId(),
                command.targetAccountId(),
                amount
        );

        // Persiste en PENDING
        Transaction saved = transactionRepository.save(transaction);
        log.info("  [1/2] Transacción {} guardada como PENDING", saved.getId());

        // PATRÓN: Outbox — publica evento en Kafka
        // Si Kafka falla, la transacción en BD hace rollback — nunca inconsistencia
        TransferInitiatedEvent event = new TransferInitiatedEvent(
                saved.getId(),
                saved.getSourceAccountId(),
                saved.getTargetAccountId(),
                saved.getAmount().amount(),
                saved.getAmount().currency().getCurrencyCode(),
                saved.getCorrelationId()
        );

        kafkaEventPublisher.publishTransferInitiated(event);
        log.info("  [2/2] Evento TransferInitiated publicado en Kafka");
        log.info("◄ TRANSACTION SERVICE — FIN (procesando asincrónicamente)");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return new TransactionResult(
                saved.getId(),
                saved.getStatus().name(),
                saved.getAmount().toString(),
                saved.getCorrelationId(),
                null
        );
    }
}