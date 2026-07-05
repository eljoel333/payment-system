package com.paymentystem.transaction.domain.model;

import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.shared.domain.valueobject.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * Agregado raíz Transaction — representa una operación de dinero
 * (transferencia, depósito o retiro) entre una o dos cuentas.
 */
public class Transaction {

    private final String id;
    private final TransactionType type;
    private final String sourceAccountId;
    private final String targetAccountId; // null en DEPOSIT/WITHDRAWAL
    private final Money amount;
    private TransactionStatus status;
    private String failureReason;
    private final String correlationId;
    private final Instant createdAt;
    private Instant completedAt;

    // PATRÓN: Builder
    // Transaction tiene 10 campos, varios opcionales según el tipo
    // (targetAccountId solo aplica a TRANSFER). Un constructor con
    // 10 parámetros sería ilegible y propenso a errores de orden.
    private Transaction(Builder builder) {
        this.id              = builder.id;
        this.type             = builder.type;
        this.sourceAccountId  = builder.sourceAccountId;
        this.targetAccountId  = builder.targetAccountId;
        this.amount           = builder.amount;
        this.status            = builder.status;
        this.failureReason     = builder.failureReason;
        this.correlationId    = builder.correlationId;
        this.createdAt        = builder.createdAt;
        this.completedAt       = builder.completedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    // PATRÓN: Factory Method — combina con Builder para crear
    // una transacción de TRANSFER nueva con reglas de negocio aplicadas.
    public static Transaction createTransfer(String sourceAccountId, String targetAccountId, Money amount) {
        if (sourceAccountId.equals(targetAccountId)) {
            throw new DomainException("TRANSACTION_SAME_ACCOUNT",
                    "No puedes transferir a la misma cuenta");
        }
        if (!amount.isPositive()) {
            throw new DomainException("TRANSACTION_INVALID_AMOUNT",
                    "El monto debe ser mayor a cero");
        }

        return Transaction.builder()
                .id(UUID.randomUUID().toString())
                .type(TransactionType.TRANSFER)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .correlationId(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .build();
    }

    // Reconstrucción desde BD
    public static Transaction reconstruct(String id, TransactionType type,
                                          String sourceAccountId, String targetAccountId, Money amount,
                                          TransactionStatus status, String failureReason, String correlationId,
                                          Instant createdAt, Instant completedAt) {
        return Transaction.builder()
                .id(id).type(type)
                .sourceAccountId(sourceAccountId).targetAccountId(targetAccountId)
                .amount(amount).status(status).failureReason(failureReason)
                .correlationId(correlationId).createdAt(createdAt).completedAt(completedAt)
                .build();
    }

    // ── Reglas de negocio ──────────────────────────────────

    public void markCompleted() {
        if (this.status != TransactionStatus.PENDING) {
            throw new DomainException("TRANSACTION_INVALID_STATE",
                    "Solo una transacción PENDING puede completarse");
        }
        this.status      = TransactionStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void markFailed(String reason) {
        if (this.status != TransactionStatus.PENDING) {
            throw new DomainException("TRANSACTION_INVALID_STATE",
                    "Solo una transacción PENDING puede fallar");
        }
        this.status        = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.completedAt   = Instant.now();
    }

    // ── Getters ───────────────────────────────────────────

    public String getId()                 { return id; }
    public TransactionType getType()      { return type; }
    public String getSourceAccountId()    { return sourceAccountId; }
    public String getTargetAccountId()    { return targetAccountId; }
    public Money getAmount()              { return amount; }
    public TransactionStatus getStatus()  { return status; }
    public String getFailureReason()      { return failureReason; }
    public String getCorrelationId()      { return correlationId; }
    public Instant getCreatedAt()         { return createdAt; }
    public Instant getCompletedAt()       { return completedAt; }

    // ── Builder ───────────────────────────────────────────

    public static class Builder {
        private String id;
        private TransactionType type;
        private String sourceAccountId;
        private String targetAccountId;
        private Money amount;
        private TransactionStatus status;
        private String failureReason;
        private String correlationId;
        private Instant createdAt;
        private Instant completedAt;

        public Builder id(String id)                             { this.id = id; return this; }
        public Builder type(TransactionType type)                { this.type = type; return this; }
        public Builder sourceAccountId(String sourceAccountId)   { this.sourceAccountId = sourceAccountId; return this; }
        public Builder targetAccountId(String targetAccountId)   { this.targetAccountId = targetAccountId; return this; }
        public Builder amount(Money amount)                      { this.amount = amount; return this; }
        public Builder status(TransactionStatus status)          { this.status = status; return this; }
        public Builder failureReason(String failureReason)       { this.failureReason = failureReason; return this; }
        public Builder correlationId(String correlationId)       { this.correlationId = correlationId; return this; }
        public Builder createdAt(Instant createdAt)               { this.createdAt = createdAt; return this; }
        public Builder completedAt(Instant completedAt)           { this.completedAt = completedAt; return this; }

        public Transaction build() {
            return new Transaction(this);
        }
    }
}