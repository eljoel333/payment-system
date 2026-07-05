package com.paymentystem.auth.infrastructure.security;

import com.paymentystem.auth.domain.model.User;
import com.paymentystem.auth.domain.port.output.JwtPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAdapter implements JwtPort {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;

    public JwtAdapter(
            @Value("${security.jwt.access-secret}") String accessSecret,
            @Value("${security.jwt.refresh-secret}") String refreshSecret,
            @Value("${security.jwt.access-expiration-seconds:3600}") long accessExpirationSeconds,
            @Value("${security.jwt.refresh-expiration-seconds:604800}") long refreshExpirationSeconds
    ) {
        this.accessKey               = new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.refreshKey              = new SecretKeySpec(refreshSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
    }

    @Override
    public String generateAccessToken(User user) {
        String roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("type", "ACCESS")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(accessExpirationSeconds)))
                .signWith(accessKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId())
                .claim("type", "REFRESH")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(refreshExpirationSeconds)))
                .signWith(refreshKey)
                .compact();
    }

    @Override
    public Optional<String> extractUserId(String token) {
        return parseClaims(token).map(Claims::getSubject);
    }

    @Override
    public boolean isValid(String token) {
        return parseClaims(token).isPresent();
    }

    @Override
    public long getExpirationSeconds() {
        return accessExpirationSeconds;
    }

    private Optional<Claims> parseClaims(String token) {
        try {
            return Optional.of(
                    Jwts.parser()
                            .verifyWith(accessKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
            );
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Token inválido: {}", e.getMessage());
            return Optional.empty();
        }
    }
}