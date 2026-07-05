package com.paymentystem.auth.application.service;

import com.paymentystem.auth.domain.model.User;
import com.paymentystem.auth.infrastructure.adapter.output.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailedLoginService {

    private final UserJpaRepository userJpaRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(User user) {
        userJpaRepository.incrementFailedAttempts(user.getId());
        log.warn("Intento fallido registrado — email: {}", user.getEmail());
    }
}