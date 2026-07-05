package com.paymentystem.account.domain.port.input;

import com.paymentystem.account.domain.model.AccountType;

public interface OpenAccountUseCase {

    OpenAccountResult execute(OpenAccountCommand command);

    record OpenAccountCommand(String userId, AccountType type, String currencyCode) {}

    record OpenAccountResult(
            String accountId,
            String accountNumber,
            String balance,
            String status
    ) {}
}