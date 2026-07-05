package com.paymentystem.auth.domain.port.input;

public interface LoginUserUseCase {

    LoginResult execute(LoginCommand command);

    record LoginCommand(String email, String password) {}

    record LoginResult(
            String accessToken,
            String refreshToken,
            long expiresInSeconds,
            String userId,
            String email
    ) {}
}