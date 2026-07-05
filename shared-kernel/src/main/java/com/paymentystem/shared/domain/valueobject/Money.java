package com.paymentystem.shared.domain.valueobject;

import com.paymentystem.shared.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "El monto no puede ser nulo");
        Objects.requireNonNull(currency, "La moneda no puede ser nula");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("MONEY_NEGATIVE", "El monto no puede ser negativo");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(double amount, String currencyCode) {
        return new Money(
                BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP),
                Currency.getInstance(currencyCode)
        );
    }

    public static Money ofMXN(double amount) {
        return Money.of(amount, "MXN");
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("MONEY_INSUFFICIENT", "Fondos insuficientes");
        }
        return new Money(result, this.currency);
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new DomainException(
                    "MONEY_CURRENCY_MISMATCH",
                    "No se pueden operar monedas distintas: %s vs %s"
                            .formatted(this.currency.getCurrencyCode(), other.currency.getCurrencyCode())
            );
        }
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(currency.getCurrencyCode(), amount.toPlainString());
    }
}