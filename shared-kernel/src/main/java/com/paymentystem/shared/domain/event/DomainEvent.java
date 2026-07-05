package com.paymentystem.shared.domain.event;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent {

    private final String eventId;
    private final String eventType;
    private final String aggregateId;
    private final String aggregateType;
    private final Instant occurredOn;
    private final String correlationId;

    protected DomainEvent(String aggregateId, String aggregateType) {
        this.eventId       = UUID.randomUUID().toString();
        this.aggregateId   = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType     = this.getClass().getSimpleName();
        this.occurredOn    = Instant.now();
        this.correlationId = UUID.randomUUID().toString();
    }

    protected DomainEvent(String aggregateId, String aggregateType, String correlationId) {
        this.eventId       = UUID.randomUUID().toString();
        this.aggregateId   = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType     = this.getClass().getSimpleName();
        this.occurredOn    = Instant.now();
        this.correlationId = correlationId;
    }

    public String getEventId()       { return eventId; }
    public String getEventType()     { return eventType; }
    public String getAggregateId()   { return aggregateId; }
    public String getAggregateType() { return aggregateType; }
    public Instant getOccurredOn()   { return occurredOn; }
    public String getCorrelationId() { return correlationId; }
}