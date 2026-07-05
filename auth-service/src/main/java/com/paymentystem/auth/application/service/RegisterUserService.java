package com.paymentystem.auth.application.service;

import com.paymentystem.auth.domain.model.User;
import com.paymentystem.auth.domain.port.input.RegisterUserUseCase;
import com.paymentystem.auth.domain.port.output.PasswordEncoderPort;
import com.paymentystem.auth.domain.port.output.UserRepositoryPort;
import com.paymentystem.shared.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    @Transactional
    public RegisterResult execute(RegisterCommand command) {
        log.info("Registrando nuevo usuario: {}", command.email());

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("► AUTH SERVICE — RegisterUserService INICIO");
        log.info("  email    : {}", command.email());
        log.info("  fullName : {}", command.fullName());

        if (userRepository.existsByEmail(command.email())) {
            throw new DomainException("AUTH_EMAIL_EXISTS", "El email ya está registrado");
        }

        log.info("  [1/3] Email disponible ✓");

        String hashedPassword = passwordEncoder.encode(command.password());
        log.info("  [2/3] Password hasheado con BCrypt ✓");

        User user = User.create(command.email(), hashedPassword, command.fullName());
        log.info("  [3/3] Usuario creado en dominio");
        log.info("        id     : {}", user.getId());
        log.info("        roles  : {}", user.getRoles());
        log.info("        status : {}", user.getStatus());

        User saved = userRepository.save(user);
        log.info("◄ AUTH SERVICE — RegisterUserService FIN");
        log.info("  userId guardado: {}", saved.getId());
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        //log.info("Usuario registrado exitosamente: {}", saved.getId());
        return new RegisterResult(saved.getId(), saved.getEmail(), saved.getRoles());
    }
}