package com.paymentystem.auth.application.service;

import com.paymentystem.auth.domain.model.User;
import com.paymentystem.auth.domain.port.input.RefreshTokenUseCase;
import com.paymentystem.auth.domain.port.output.JwtPort;
import com.paymentystem.auth.domain.port.output.UserRepositoryPort;
import com.paymentystem.shared.domain.exception.DomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtPort jwt;
    private final UserRepositoryPort userRepository;

    @Override
    public TokenResult execute(String refreshToken) {
        if (!jwt.isValid(refreshToken)) {
            throw new DomainException("AUTH_INVALID_REFRESH",
                    "Refresh token inválido o expirado");
        }

        String userId = jwt.extractUserId(refreshToken)
                .orElseThrow(() -> new DomainException("AUTH_INVALID_REFRESH",
                        "Token sin subject"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DomainException("AUTH_NOT_FOUND",
                        "Usuario no encontrado"));

        user.validateCanLogin();

        return new TokenResult(
                jwt.generateAccessToken(user),
                jwt.generateRefreshToken(user),
                jwt.getExpirationSeconds()
        );
    }
}