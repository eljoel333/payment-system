package com.paymentystem.auth.infrastructure.adapter.input.rest;

import com.paymentystem.auth.domain.port.input.LoginUserUseCase;
import com.paymentystem.auth.domain.port.input.LogoutUserUseCase;
import com.paymentystem.auth.domain.port.input.RefreshTokenUseCase;
import com.paymentystem.auth.domain.port.input.RegisterUserUseCase;
import com.paymentystem.auth.infrastructure.adapter.input.rest.dto.LoginRequest;
import com.paymentystem.auth.infrastructure.adapter.input.rest.dto.LoginResponse;
import com.paymentystem.auth.infrastructure.adapter.input.rest.dto.RegisterRequest;
import com.paymentystem.auth.infrastructure.adapter.input.rest.dto.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUserUseCase loginUseCase;
    private final RegisterUserUseCase registerUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUseCase;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        var result = registerUseCase.execute(
                new RegisterUserUseCase.RegisterCommand(
                        request.email(),
                        request.password(),
                        request.fullName()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RegisterResponse.from(result));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = loginUseCase.execute(
                new LoginUserUseCase.LoginCommand(
                        request.email(),
                        request.password()
                )
        );
        return ResponseEntity.ok(LoginResponse.from(result));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestHeader("X-Refresh-Token") String refreshToken) {
        var result = refreshTokenUseCase.execute(refreshToken);
        return ResponseEntity.ok(new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.expiresInSeconds()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        logoutUseCase.execute(jwt.getTokenValue(), jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(new java.util.LinkedHashMap<>() {{
            put("userId", jwt.getSubject());
            put("email", jwt.getClaim("email"));
            put("roles", jwt.getClaim("roles"));
        }});
    }
}