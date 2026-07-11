package com.paymentystem.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TransferInitiatedEvent extends DomainEvent {

    private final String sourceAccountId;
    private final String targetAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final String transactionId;

    @JsonCreator
    public TransferInitiatedEvent(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("sourceAccountId") String sourceAccountId,
            @JsonProperty("targetAccountId") String targetAccountId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("correlationId") String correlationId) {
        super(transactionId, "Transaction", correlationId);
        this.transactionId   = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount          = amount;
        this.currency        = currency;
    }

    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public BigDecimal getAmount()      { return amount; }
    public String getCurrency()        { return currency; }
    public String getTransactionId()   { return transactionId; }
}