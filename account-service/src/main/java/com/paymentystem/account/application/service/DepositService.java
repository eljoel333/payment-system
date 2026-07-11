package com.paymentystem.account.application.service;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.input.DepositUseCase;
import com.paymentystem.account.domain.port.output.AccountRepositoryPort;
import com.paymentystem.account.infrastructure.adapter.output.messaging.KafkaEventPublisher;
import com.paymentystem.account.infrastructure.config.KafkaConfig;
import com.paymentystem.shared.domain.event.TransferCompletedEvent;
import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.shared.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService implements DepositUseCase {

    private final AccountRepositoryPort accountRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Override
    @Transactional
    public DepositResult execute(DepositCommand command) {
        log.info("  ┌─ ACCOUNT SERVICE — DepositService");
        log.info("  │  accountId : {}", command.accountId());
        log.info("  │  amount    : {}", command.amount());

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new DomainException("ACCOUNT_NOT_FOUND",
                        "Cuenta no encontrada"));

        log.info("  │  balance antes: {}", account.getBalance());

        Money depositAmount = new Money(command.amount(), account.getBalance().currency());
        account.credit(depositAmount);

        Account saved = accountRepository.save(account);

        log.info("  │  balance después: {}", saved.getBalance());
        log.info("  └─ DepositService ✓");

        // Si viene de una transferencia (correlationId no nulo) publica TransferCompleted
        if (command.correlationId() != null) {
            TransferCompletedEvent event = new TransferCompletedEvent(
                    command.transactionId(),    // ← transactionId real
                    null,
                    command.accountId(),
                    command.correlationId()
            );
            kafkaEventPublisher.publish(
                    KafkaConfig.TRANSFER_COMPLETED,
                    command.correlationId(),
                    event
            );
        }

        return new DepositResult(
                saved.getId(),
                saved.getBalance().amount().toString(),
                saved.getBalance().currency().getCurrencyCode()
        );
    }
}