package com.paymentystem.account.application.service;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.input.GetAccountUseCase;
import com.paymentystem.account.domain.port.output.AccountRepositoryPort;
import com.paymentystem.shared.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {

    private final AccountRepositoryPort accountRepository;

    @Override
    @Transactional(readOnly = true)
    public AccountDetails execute(String accountId, String requestingUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new DomainException("ACCOUNT_NOT_FOUND",
                        "Cuenta no encontrada"));

        // Regla de seguridad: un usuario solo puede ver SUS propias cuentas
        if (!account.getUserId().equals(requestingUserId)) {
            throw new DomainException("ACCOUNT_FORBIDDEN",
                    "No tienes permiso para ver esta cuenta");
        }

        return new AccountDetails(
                account.getId(),
                account.getUserId(),
                account.getAccountNumber().value(),
                account.getType().name(),
                account.getBalance().amount().toString(),
                account.getBalance().currency().getCurrencyCode(),
                account.getDailyLimit().amount().toString(),
                account.getDailyWithdrawn().amount().toString(),
                account.getStatus().name()
        );
    }
}