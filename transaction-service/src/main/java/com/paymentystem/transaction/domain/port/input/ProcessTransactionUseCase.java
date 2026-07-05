package com.paymentystem.transaction.domain.port.input;

import com.paymentystem.transaction.domain.model.TransactionType;

import java.math.BigDecimal;

public interface ProcessTransactionUseCase {

    TransactionResult execute(TransactionCommand command);

    record TransactionCommand(
            TransactionType type,
            String sourceAccountId,
            String targetAccountId,
            BigDecimal amount,
            String currencyCode,
            String requestingUserId
    ) {}

    record TransactionResult(
            String transactionId,
            String status,
            String amount,
            String correlationId,
            String failureReason
    ) {}
}