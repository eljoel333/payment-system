package com.paymentystem.transaction.infrastructure.adapter.output.persistence;

import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.port.output.TransactionRepositoryPort;
import com.paymentystem.transaction.infrastructure.adapter.output.persistence.entity.TransactionJpaEntity;
import com.paymentystem.transaction.infrastructure.adapter.output.persistence.mapper.TransactionMapper;
import com.paymentystem.transaction.infrastructure.adapter.output.persistence.repository.TransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionJpaEntity entity = mapper.toEntity(transaction);
        TransactionJpaEntity saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findBySourceAccountId(String accountId) {
        return jpaRepository.findBySourceAccountId(accountId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}