package com.paymentystem.auth.domain.port.input;

public interface LogoutUserUseCase {
    void execute(String accessToken, String userId);
}