package com.paymentystem.auth.application.service;

import com.paymentystem.auth.domain.model.User;
import com.paymentystem.auth.domain.port.input.LoginUserUseCase;
import com.paymentystem.auth.domain.port.output.JwtPort;
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
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtPort jwt;
    private final FailedLoginService failedLoginService;

    @Override
    @Transactional
    public LoginResult execute(LoginCommand command) {
        log.info("Intento de login: {}", command.email());

        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        log.info("► AUTH SERVICE — LoginUserService INICIO");
        log.info("  email: {}", command.email());

        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new DomainException("AUTH_NOT_FOUND",
                        "Credenciales inválidas"));

        user.validateCanLogin();
        log.info("  [2/4] Validación de status ✓ — puede loguearse");


        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            failedLoginService.record(user);
            throw new DomainException("AUTH_INVALID", "Credenciales inválidas");
        }


        log.info("  [3/4] Contraseña validada con BCrypt ✓");


        user.recordSuccessfulLogin();
        userRepository.save(user);

        String accessToken  = jwt.generateAccessToken(user);
        String refreshToken = jwt.generateRefreshToken(user);

        log.info("  [4/4] JWT generado");
        log.info("        expira en: {}s", jwt.getExpirationSeconds());
        log.info("◄ AUTH SERVICE — LoginUserService FIN ✓");
        log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        log.info("Login exitoso para userId: {}", user.getId());
        return new LoginResult(accessToken, refreshToken,
                jwt.getExpirationSeconds(), user.getId(), user.getEmail());
    }
}