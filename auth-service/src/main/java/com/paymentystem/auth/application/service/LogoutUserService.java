package com.paymentystem.auth.application.service;

import com.paymentystem.auth.domain.port.input.LogoutUserUseCase;
import com.paymentystem.auth.domain.port.output.JwtPort;
import com.paymentystem.auth.domain.port.output.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutUserService implements LogoutUserUseCase {

    private final TokenBlacklistPort blacklist;
    private final JwtPort jwt;

    @Override
    public void execute(String accessToken, String userId) {
        blacklist.blacklist(accessToken, jwt.getExpirationSeconds());
        log.info("Logout exitoso para userId: {}", userId);
    }
}