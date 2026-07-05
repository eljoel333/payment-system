package com.paymentystem.account.infrastructure.adapter.input.rest.dto;

import com.paymentystem.account.domain.port.input.DebitUseCase;

public record DebitResponse(String accountId, String newBalance, String currency) {
    public static DebitResponse from(DebitUseCase.DebitResult result) {
        return new DebitResponse(result.accountId(), result.newBalance(), result.currency());
    }
}