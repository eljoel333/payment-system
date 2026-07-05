package com.paymentystem.auth.domain.port.input;

public interface RefreshTokenUseCase {

    TokenResult execute(String refreshToken);

    record TokenResult(String accessToken, String refreshToken, long expiresInSeconds) {}
}