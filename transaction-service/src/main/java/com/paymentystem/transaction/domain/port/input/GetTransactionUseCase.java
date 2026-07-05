package com.paymentystem.transaction.domain.port.input;

import java.util.List;

public interface GetTransactionUseCase {

    List<TransactionResult> findByAccount(String accountId, String requestingUserId);

    record TransactionResult(
            String transactionId,
            String type,
            String sourceAccountId,
            String targetAccountId,
            String amount,
            String status,
            String createdAt
    ) {}
}