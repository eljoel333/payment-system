package com.paymentystem.account.domain.port.input;

import java.math.BigDecimal;

public interface DebitUseCase {

    DebitResult execute(DebitCommand command);

    record DebitCommand(
            String accountId,
            String targetAccountId,
            String requestingUserId,
            BigDecimal amount,
            String correlationId,
            String transactionId    // ← agrega este campo
    ) {}

    record DebitResult(String accountId, String newBalance, String currency) {}
}