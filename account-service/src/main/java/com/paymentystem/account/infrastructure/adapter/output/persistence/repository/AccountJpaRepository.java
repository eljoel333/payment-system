package com.paymentystem.account.infrastructure.adapter.output.persistence.repository;

import com.paymentystem.account.infrastructure.adapter.output.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, String> {
    List<AccountJpaEntity> findByUserId(String userId);
    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);
    boolean existsByUserIdAndType(String userId, String type);
}