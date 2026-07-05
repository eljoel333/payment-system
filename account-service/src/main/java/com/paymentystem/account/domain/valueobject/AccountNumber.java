package com.paymentystem.account.domain.valueobject;

import com.paymentystem.shared.domain.exception.DomainException;

import java.security.SecureRandom;

/**
 * PATRÓN: Value Object (DDD).
 * Un número de cuenta no es "solo un String" — tiene reglas propias
 * (12 dígitos exactos) y es inmutable. Dos AccountNumber con el mismo
 * valor son iguales (records dan equals/hashCode gratis).
 *
 * También aplica PATRÓN: Factory Method en generate().
 */
public record AccountNumber(String value) {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int LENGTH = 12;

    public AccountNumber {
        if (value == null || !value.matches("\\d{12}")) {
            throw new DomainException("INVALID_ACCOUNT_NUMBER",
                    "El número de cuenta debe tener exactamente 12 dígitos");
        }
    }

    public static AccountNumber generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return new AccountNumber(sb.toString());
    }

    @Override
    public String toString() {
        return value;
    }
}