package com.paymentystem.auth.domain.port.output;

public interface TokenBlacklistPort {
    void blacklist(String token, long ttlSeconds);
    boolean isBlacklisted(String token);
}