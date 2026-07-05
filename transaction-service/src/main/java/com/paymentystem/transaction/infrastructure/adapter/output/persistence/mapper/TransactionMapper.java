package com.paymentystem.transaction.infrastructure.adapter.output.persistence.mapper;

import com.paymentystem.shared.domain.valueobject.Money;
import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.infrastructure.adapter.output.persistence.entity.TransactionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class TransactionMapper {

    public TransactionJpaEntity toEntity(Transaction transaction) {
        return TransactionJpaEntity.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .sourceAccountId(transaction.getSourceAccountId())
                .targetAccountId(transaction.getTargetAccountId())
                .amount(transaction.getAmount().amount())
                .currency(transaction.getAmount().currency().getCurrencyCode())
                .status(transaction.getStatus())
                .failureReason(transaction.getFailureReason())
                .correlationId(transaction.getCorrelationId())
                .createdAt(transaction.getCreatedAt())
                .completedAt(transaction.getCompletedAt())
                .build();
    }

    public Transaction toDomain(TransactionJpaEntity entity) {
        Money amount = new Money(
                entity.getAmount(),
                Currency.getInstance(entity.getCurrency())
        );
        return Transaction.reconstruct(
                entity.getId(),
                entity.getType(),
                entity.getSourceAccountId(),
                entity.getTargetAccountId(),
                amount,
                entity.getStatus(),
                entity.getFailureReason(),
                entity.getCorrelationId(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}