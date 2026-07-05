package com.paymentystem.account.infrastructure.adapter.input.rest.dto;

import com.paymentystem.account.domain.port.input.GetAccountUseCase;

public record AccountDetailsResponse(
        String accountId,
        String accountNumber,
        String type,
        String balance,
        String currency,
        String dailyLimit,
        String dailyWithdrawn,
        String status
) {
    public static AccountDetailsResponse from(GetAccountUseCase.AccountDetails details) {
        return new AccountDetailsResponse(
                details.accountId(),
                details.accountNumber(),
                details.type(),
                details.balance(),
                details.currency(),
                details.dailyLimit(),
                details.dailyWithdrawn(),
                details.status()
        );
    }
}