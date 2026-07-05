package com.paymentystem.account.domain.model;

import com.paymentystem.account.domain.valueobject.AccountNumber;
import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

public class Account {

    private final String id;
    private final String userId;
    private final AccountNumber accountNumber;
    private final AccountType type;
    private Money balance;
    private Money dailyLimit;
    private Money dailyWithdrawn;
    private AccountStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final Long version; // ← nuevo — necesario para Optimistic Locking

    private Account(String id, String userId, AccountNumber accountNumber,
                    AccountType type, Money balance, Money dailyLimit,
                    Money dailyWithdrawn, AccountStatus status,
                    Instant createdAt, Instant updatedAt, Long version) {
        this.id             = id;
        this.userId         = userId;
        this.accountNumber  = accountNumber;
        this.type           = type;
        this.balance        = balance;
        this.dailyLimit     = dailyLimit;
        this.dailyWithdrawn = dailyWithdrawn;
        this.status         = status;
        this.createdAt      = createdAt;
        this.updatedAt      = updatedAt;
        this.version        = version;
    }

    // PATRÓN: Factory Method — cuenta nueva, version null (Hibernate la asigna)
    public static Account open(String userId, AccountType type, String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        Money zero = new Money(BigDecimal.ZERO, currency);
        Money defaultDailyLimit = new Money(BigDecimal.valueOf(50000), currency);

        return new Account(
                UUID.randomUUID().toString(),
                userId,
                AccountNumber.generate(),
                type,
                zero,
                defaultDailyLimit,
                zero,
                AccountStatus.ACTIVE,
                Instant.now(),
                Instant.now(),
                null
        );
    }

    // PATRÓN: Factory Method (reconstrucción) — recibe el version de la BD
    public static Account reconstruct(String id, String userId, AccountNumber accountNumber,
                                      AccountType type, Money balance, Money dailyLimit,
                                      Money dailyWithdrawn, AccountStatus status,
                                      Instant createdAt, Instant updatedAt, Long version) {
        return new Account(id, userId, accountNumber, type, balance,
                dailyLimit, dailyWithdrawn, status, createdAt, updatedAt, version);
    }

    public void validateCanOperate() {
        if (status == AccountStatus.FROZEN) {
            throw new DomainException("ACCOUNT_FROZEN", "La cuenta está congelada");
        }
        if (status == AccountStatus.CLOSED) {
            throw new DomainException("ACCOUNT_CLOSED", "La cuenta está cerrada");
        }
    }

    public void debit(Money amount) {
        validateCanOperate();

        if (!balance.isGreaterThanOrEqual(amount)) {
            throw new DomainException("INSUFFICIENT_FUNDS", "Fondos insuficientes");
        }

        Money projectedWithdrawn = dailyWithdrawn.add(amount);
        if (projectedWithdrawn.isGreaterThan(dailyLimit)) {
            throw new DomainException("DAILY_LIMIT_EXCEEDED",
                    "Se excede el límite diario de retiro");
        }

        this.balance        = balance.subtract(amount);
        this.dailyWithdrawn = projectedWithdrawn;
        this.updatedAt      = Instant.now();
    }

    public void credit(Money amount) {
        validateCanOperate();
        this.balance   = balance.add(amount);
        this.updatedAt = Instant.now();
    }

    public void freeze() {
        if (this.status == AccountStatus.CLOSED) {
            throw new DomainException("ACCOUNT_CLOSED", "No se puede congelar una cuenta cerrada");
        }
        this.status     = AccountStatus.FROZEN;
        this.updatedAt  = Instant.now();
    }

    public void unfreeze() {
        if (this.status != AccountStatus.FROZEN) {
            throw new DomainException("ACCOUNT_NOT_FROZEN", "La cuenta no está congelada");
        }
        this.status     = AccountStatus.ACTIVE;
        this.updatedAt  = Instant.now();
    }

    public void resetDailyWithdrawn() {
        this.dailyWithdrawn = new Money(BigDecimal.ZERO, balance.currency());
        this.updatedAt      = Instant.now();
    }

    public String getId()                  { return id; }
    public String getUserId()              { return userId; }
    public AccountNumber getAccountNumber(){ return accountNumber; }
    public AccountType getType()           { return type; }
    public Money getBalance()              { return balance; }
    public Money getDailyLimit()           { return dailyLimit; }
    public Money getDailyWithdrawn()       { return dailyWithdrawn; }
    public AccountStatus getStatus()       { return status; }
    public Instant getCreatedAt()          { return createdAt; }
    public Instant getUpdatedAt()          { return updatedAt; }
    public Long getVersion()               { return version; }
}