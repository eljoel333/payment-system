package com.paymentystem.account.domain.port.input;

import java.math.BigDecimal;

public interface DepositUseCase {

    DepositResult execute(DepositCommand command);

    record DepositCommand(String accountId, String requestingUserId, BigDecimal amount) {}

    record DepositResult(String accountId, String newBalance, String currency) {}
}