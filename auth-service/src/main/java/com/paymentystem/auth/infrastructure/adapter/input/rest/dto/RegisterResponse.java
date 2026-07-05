package com.paymentystem.auth.infrastructure.adapter.input.rest.dto;

import com.paymentystem.auth.domain.model.Role;
import com.paymentystem.auth.domain.port.input.RegisterUserUseCase;

import java.util.Set;

public record RegisterResponse(
        String userId,
        String email,
        Set<Role> roles
) {
    public static RegisterResponse from(RegisterUserUseCase.RegisterResult result) {
        return new RegisterResponse(
                result.userId(),
                result.email(),
                result.roles()
        );
    }
}