package com.paymentystem.auth.infrastructure.adapter.output.persistence;

import com.paymentystem.auth.domain.model.User;
import com.paymentystem.auth.domain.port.output.UserRepositoryPort;
import com.paymentystem.auth.infrastructure.adapter.output.persistence.entity.UserJpaEntity;
import com.paymentystem.auth.infrastructure.adapter.output.persistence.mapper.UserMapper;
import com.paymentystem.auth.infrastructure.adapter.output.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity saved  = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}