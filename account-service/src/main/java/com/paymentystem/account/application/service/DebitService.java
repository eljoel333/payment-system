package com.paymentystem.account.application.service;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.input.DebitUseCase;
import com.paymentystem.account.domain.port.output.AccountRepositoryPort;
import com.paymentystem.account.infrastructure.adapter.output.messaging.KafkaEventPublisher;
import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.shared.domain.event.AccountDebitedEvent;
import com.paymentystem.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebitService implements DebitUseCase {

    private final AccountRepositoryPort accountRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    @Transactional
    public DebitResult execute(DebitCommand command) {
        log.info("  ┌─ ACCOUNT SERVICE — DebitService");
        log.info("  │  accountId    : {}", command.accountId());
        log.info("  │  amount       : {}", command.amount());
        log.info("  │  correlationId: {}", command.correlationId());

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new DomainException("ACCOUNT_NOT_FOUND",
                        "Cuenta no encontrada"));

        log.info("  │  balance antes: {}", account.getBalance());

        Money debitAmount = new Money(command.amount(), account.getBalance().currency());
        account.debit(debitAmount);

        Account saved = accountRepository.save(account);

        log.info("  │  balance después: {}", saved.getBalance());
        log.info("  └─ Débito exitoso ✓");

        // Publica evento AccountDebited para que account-service acredite la cuenta destino
        AccountDebitedEvent event = new AccountDebitedEvent(
                command.transactionId(),    // ← transactionId real
                command.accountId(),
                command.targetAccountId(),
                command.amount(),
                saved.getBalance().currency().getCurrencyCode(),
                command.correlationId()
        );
        kafkaEventPublisher.publish("account.debited", command.correlationId(), event);

        return new DebitResult(
                saved.getId(),
                saved.getBalance().amount().toString(),
                saved.getBalance().currency().getCurrencyCode()
        );
    }
}