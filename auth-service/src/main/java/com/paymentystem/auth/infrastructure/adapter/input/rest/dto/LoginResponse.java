package com.paymentystem.auth.infrastructure.adapter.input.rest.dto;

import com.paymentystem.auth.domain.port.input.LoginUserUseCase;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {
    public static LoginResponse from(LoginUserUseCase.LoginResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.expiresInSeconds()
        );
    }
}