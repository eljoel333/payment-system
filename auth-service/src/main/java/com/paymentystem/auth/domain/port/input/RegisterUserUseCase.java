package com.paymentystem.auth.domain.port.input;

import com.paymentystem.auth.domain.model.Role;
import java.util.Set;

public interface RegisterUserUseCase {

    RegisterResult execute(RegisterCommand command);

    record RegisterCommand(String email, String password, String fullName) {}

    record RegisterResult(String userId, String email, Set<Role> roles) {}
}