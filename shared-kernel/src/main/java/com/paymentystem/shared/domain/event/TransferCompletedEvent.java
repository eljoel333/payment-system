package com.paymentystem.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferCompletedEvent extends DomainEvent {

    private final String transactionId;
    private final String sourceAccountId;
    private final String targetAccountId;

    @JsonCreator
    public TransferCompletedEvent(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("sourceAccountId") String sourceAccountId,
            @JsonProperty("targetAccountId") String targetAccountId,
            @JsonProperty("correlationId") String correlationId) {
        super(transactionId, "Transaction", correlationId);
        this.transactionId   = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

    public String getTransactionId()   { return transactionId; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
}