package com.paymentystem.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class TransferCompensatedEvent extends DomainEvent {

    private final String transactionId;
    private final String sourceAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final String reason;

    @JsonCreator
    public TransferCompensatedEvent(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("sourceAccountId") String sourceAccountId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency,
            @JsonProperty("reason") String reason,
            @JsonProperty("correlationId") String correlationId) {
        super(transactionId, "Transaction", correlationId);
        this.transactionId   = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.amount          = amount;
        this.currency        = currency;
        this.reason          = reason;
    }

    public String getTransactionId()   { return transactionId; }
    public String getSourceAccountId() { return sourceAccountId; }
    public BigDecimal getAmount()      { return amount; }
    public String getCurrency()        { return currency; }
    public String getReason()          { return reason; }
}