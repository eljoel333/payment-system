package com.paymentystem.auth.infrastructure.adapter.output.cache;

import com.paymentystem.auth.domain.port.output.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenBlacklistAdapter implements TokenBlacklistPort {

    private static final String PREFIX = "blacklist:";
    private final StringRedisTemplate redis;

    @Override
    public void blacklist(String token, long ttlSeconds) {
        redis.opsForValue().set(PREFIX + token, "1", Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(PREFIX + token));
    }
}