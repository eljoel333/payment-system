package com.paymentystem.account.domain.port.input;

public interface GetAccountUseCase {

    AccountDetails execute(String accountId, String requestingUserId);

    record AccountDetails(
            String accountId,
            String userId,
            String accountNumber,
            String type,
            String balance,
            String currency,
            String dailyLimit,
            String dailyWithdrawn,
            String status
    ) {}
}