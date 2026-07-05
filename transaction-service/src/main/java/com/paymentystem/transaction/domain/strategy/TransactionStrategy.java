package com.paymentystem.transaction.domain.strategy;

import com.paymentystem.transaction.domain.model.Transaction;
import com.paymentystem.transaction.domain.model.TransactionType;


/**
 * PATRÓN: Strategy
 * Define el contrato que cada tipo de transacción debe cumplir.
 * El servicio de aplicación trabaja con esta interfaz — nunca
 * sabe qué tipo concreto está ejecutando.
 */
public interface TransactionStrategy {
    TransactionType type();
    void execute(Transaction transaction, TransactionExecutionContext context);
}