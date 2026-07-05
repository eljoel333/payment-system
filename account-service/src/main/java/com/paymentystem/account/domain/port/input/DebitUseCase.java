package com.paymentystem.account.domain.port.input;

import java.math.BigDecimal;

public interface DebitUseCase {

    DebitResult execute(DebitCommand command);

    record DebitCommand(String accountId, String requestingUserId,
                        BigDecimal amount, String correlationId) {}

    record DebitResult(String accountId, String newBalance, String currency) {}
}