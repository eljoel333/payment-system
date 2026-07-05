package com.paymentystem.account.application.service;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.input.OpenAccountUseCase;
import com.paymentystem.account.domain.port.output.AccountRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAccountService implements OpenAccountUseCase {

    private final AccountRepositoryPort accountRepository;

    @Override
    @Transactional
    public OpenAccountResult execute(OpenAccountCommand command) {
        log.info("Abriendo cuenta {} para userId: {}", command.type(), command.userId());

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("► ACCOUNT SERVICE — OpenAccountService INICIO");
        log.info("  userId   : {}", command.userId());
        log.info("  tipo     : {}", command.type());
        log.info("  moneda   : {}", command.currencyCode());

        // PATRÓN: Factory Method — Account.open() encapsula las
        // reglas de creación (número de cuenta, límite por defecto, etc.)
        Account account = Account.open(
                command.userId(),
                command.type(),
                command.currencyCode()
        );

        log.info("  [1/2] Cuenta creada en dominio");
        log.info("        id             : {}", account.getId());
        log.info("        accountNumber  : {}", account.getAccountNumber().value());
        log.info("        balance inicial: {}", account.getBalance());
        log.info("        límite diario  : {}", account.getDailyLimit());
        log.info("        status         : {}", account.getStatus());

        Account saved = accountRepository.save(account);

        log.info("  [2/2] Cuenta persistida en BD ✓");
        log.info("◄ ACCOUNT SERVICE — OpenAccountService FIN");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        log.info("Cuenta abierta exitosamente: {}", saved.getId());

        return new OpenAccountResult(
                saved.getId(),
                saved.getAccountNumber().value(),
                saved.getBalance().toString(),
                saved.getStatus().name()
        );
    }
}