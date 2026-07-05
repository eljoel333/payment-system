package com.paymentystem.transaction.domain.port.output;

import com.paymentystem.transaction.domain.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(String id);
    List<Transaction> findBySourceAccountId(String accountId);
}