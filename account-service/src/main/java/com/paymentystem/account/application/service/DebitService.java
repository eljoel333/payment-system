package com.paymentystem.account.application.service;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.input.DebitUseCase;
import com.paymentystem.account.domain.port.output.AccountRepositoryPort;
import com.paymentystem.shared.domain.exception.DomainException;
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

    @Override
    @Transactional
    public DebitResult execute(DebitCommand command) {
        log.info("Debitando {} de cuenta {} — correlationId: {}",
                command.amount(), command.accountId(), command.correlationId());

        log.info("  ┌─ ACCOUNT SERVICE — DebitService");
        log.info("  │  accountId    : {}", command.accountId());
        log.info("  │  amount       : {}", command.amount());
        log.info("  │  correlationId: {}", command.correlationId());

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new DomainException("ACCOUNT_NOT_FOUND",
                        "Cuenta no encontrada"));

        log.info("  │  balance antes: {}", account.getBalance());

        Money debitAmount = new Money(command.amount(), account.getBalance().currency());

        // Regla de negocio vive en el dominio — valida fondos y límite diario
        account.debit(debitAmount);

        Account saved = accountRepository.save(account);

        log.info("  │  balance después: {}", saved.getBalance());
        log.info("  └─ Débito exitoso — nuevo balance: {}", saved.getBalance());


        return new DebitResult(
                saved.getId(),
                saved.getBalance().amount().toString(),
                saved.getBalance().currency().getCurrencyCode()
        );
    }
}