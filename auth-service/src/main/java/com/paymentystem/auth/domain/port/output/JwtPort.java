package com.paymentystem.auth.domain.port.output;

import com.paymentystem.auth.domain.model.User;
import java.util.Optional;

public interface JwtPort {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    Optional<String> extractUserId(String token);
    boolean isValid(String token);
    long getExpirationSeconds();
}