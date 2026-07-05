package com.paymentystem.transaction.infrastructure.adapter.input.rest.dto;

import com.paymentystem.transaction.domain.port.input.ProcessTransactionUseCase;

public record TransactionResponse(
        String transactionId,
        String status,
        String amount,
        String correlationId,
        String failureReason
) {
    public static TransactionResponse from(ProcessTransactionUseCase.TransactionResult result) {
        return new TransactionResponse(
                result.transactionId(),
                result.status(),
                result.amount(),
                result.correlationId(),
                result.failureReason()
        );
    }
}