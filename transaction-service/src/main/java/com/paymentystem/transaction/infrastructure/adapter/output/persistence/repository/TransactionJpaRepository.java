package com.paymentystem.transaction.infrastructure.adapter.output.persistence.repository;

import com.paymentystem.transaction.infrastructure.adapter.output.persistence.entity.TransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, String> {
    List<TransactionJpaEntity> findBySourceAccountId(String sourceAccountId);
}