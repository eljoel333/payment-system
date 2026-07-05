package com.paymentystem.account.infrastructure.adapter.input.rest.dto;

import com.paymentystem.account.domain.port.input.DepositUseCase;

public record DepositResponse(String accountId, String newBalance, String currency) {
    public static DepositResponse from(DepositUseCase.DepositResult result) {
        return new DepositResponse(result.accountId(), result.newBalance(), result.currency());
    }
}