package com.paymentystem.account.infrastructure.adapter.output.persistence.mapper;

import com.paymentystem.account.domain.model.Account;
import com.paymentystem.account.domain.valueobject.AccountNumber;
import com.paymentystem.account.infrastructure.adapter.output.persistence.entity.AccountJpaEntity;
import com.paymentystem.shared.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class AccountMapper {

    public AccountJpaEntity toEntity(Account account) {
        return AccountJpaEntity.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .accountNumber(account.getAccountNumber().value())
                .type(account.getType())
                .balance(account.getBalance().amount())
                .currency(account.getBalance().currency().getCurrencyCode())
                .dailyLimit(account.getDailyLimit().amount())
                .dailyWithdrawn(account.getDailyWithdrawn().amount())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .version(account.getVersion()) // ← propaga el version
                .build();
    }

    public Account toDomain(AccountJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());

        return Account.reconstruct(
                entity.getId(),
                entity.getUserId(),
                new AccountNumber(entity.getAccountNumber()),
                entity.getType(),
                new Money(entity.getBalance(), currency),
                new Money(entity.getDailyLimit(), currency),
                new Money(entity.getDailyWithdrawn(), currency),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion() // ← lee el version
        );
    }
}