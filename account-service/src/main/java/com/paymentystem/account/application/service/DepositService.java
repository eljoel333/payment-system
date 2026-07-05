package com.paymentystem.account.application.service;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.input.DepositUseCase;
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
public class DepositService implements DepositUseCase {

    private final AccountRepositoryPort accountRepository;

    @Override
    @Transactional
    public DepositResult execute(DepositCommand command) {
        //log.info("Depósito de {} a cuenta {}", command.amount(), command.accountId());

        log.info("  ┌─ ACCOUNT SERVICE — DepositService - Depósito de {} a cuenta {}", command.amount(), command.accountId());
        log.info("  │  accountId : {}", command.accountId());
        log.info("  │  amount    : {}", command.amount());

        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new DomainException("ACCOUNT_NOT_FOUND",
                        "Cuenta no encontrada"));

        // Regla de seguridad — solo el dueño puede depositar a su cuenta
        if (!account.getUserId().equals(command.requestingUserId())) {
            throw new DomainException("ACCOUNT_FORBIDDEN",
                    "No tienes permiso sobre esta cuenta");
        }

        Money depositAmount = new Money(command.amount(), account.getBalance().currency());

        // Regla de negocio vive en el dominio — Account.credit()
        account.credit(depositAmount);

        Account saved = accountRepository.save(account);

        log.info("  │  balance después: {}", saved.getBalance());
        log.info("  └─ DepositService ✓");

       // log.info("Depósito exitoso — nuevo balance: {}", saved.getBalance());

        return new DepositResult(
                saved.getId(),
                saved.getBalance().amount().toString(),
                saved.getBalance().currency().getCurrencyCode()
        );
    }
}