package com.paymentystem.account.infrastructure.adapter.output.persistence;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.port.output.AccountRepositoryPort;
import com.paymentystem.account.infrastructure.adapter.output.persistence.entity.AccountJpaEntity;
import com.paymentystem.account.infrastructure.adapter.output.persistence.mapper.AccountMapper;
import com.paymentystem.account.infrastructure.adapter.output.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper mapper;

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = mapper.toEntity(account);
        AccountJpaEntity saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Account> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndType(String userId, String type) {
        return jpaRepository.existsByUserIdAndType(userId, type);
    }
}