package com.paymentystem.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AccountDebitedEvent extends DomainEvent {

    private final String transactionId;
    private final String sourceAccountId;
    private final String targetAccountId;
    private final BigDecimal amount;
    private final String currency;

    @JsonCreator
    public AccountDebitedEvent(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("sourceAccountId") String sourceAccountId,
            @JsonProperty("targetAccountId") String targetAccountId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("correlationId") String correlationId) {
        super(transactionId, "Account", correlationId);
        this.transactionId   = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount          = amount;
        this.currency        = currency;
    }

    public String getTransactionId()   { return transactionId; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public BigDecimal getAmount()      { return amount; }
    public String getCurrency()        { return currency; }
}