package com.paymentystem.transaction.domain.strategy;

import com.paymentystem.shared.domain.valueobject.Money;

/**
 * Contexto compartido entre todas las strategies.
 * Encapsula los datos externos que una transacción necesita
 * sin acoplar las strategies a la infraestructura directamente.
 */
public record TransactionExecutionContext(
        String sourceAccountId,
        String targetAccountId,
        Money amount,
        String requestingUserId
) {}