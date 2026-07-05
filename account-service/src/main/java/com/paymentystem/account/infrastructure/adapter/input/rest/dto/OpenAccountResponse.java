package com.paymentystem.account.infrastructure.adapter.input.rest.dto;

import com.paymentystem.account.domain.port.input.OpenAccountUseCase;

public record OpenAccountResponse(
        String accountId,
        String accountNumber,
        String balance,
        String status
) {
    public static OpenAccountResponse from(OpenAccountUseCase.OpenAccountResult result) {
        return new OpenAccountResponse(
                result.accountId(),
                result.accountNumber(),
                result.balance(),
                result.status()
        );
    }
}