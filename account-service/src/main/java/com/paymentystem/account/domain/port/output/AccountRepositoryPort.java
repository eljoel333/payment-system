package com.paymentystem.account.domain.port.output;

import com.paymentystem.account.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {
    Account save(Account account);
    Optional<Account> findById(String id);
    List<Account> findByUserId(String userId);
    boolean existsByUserIdAndType(String userId, String type);
}