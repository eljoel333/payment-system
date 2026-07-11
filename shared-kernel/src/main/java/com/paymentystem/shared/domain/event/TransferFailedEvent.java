package com.paymentystem.shared.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferFailedEvent extends DomainEvent {

    private final String transactionId;
    private final String reason;
    private final String failedStep;

    @JsonCreator
    public TransferFailedEvent(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("reason") String reason,
            @JsonProperty("failedStep") String failedStep,
            @JsonProperty("correlationId") String correlationId) {
        super(transactionId, "Transaction", correlationId);
        this.transactionId = transactionId;
        this.reason        = reason;
        this.failedStep    = failedStep;
    }

    public String getTransactionId() { return transactionId; }
    public String getReason()        { return reason; }
    public String getFailedStep()    { return failedStep; }
}