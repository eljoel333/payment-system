package com.paymentystem.transaction.domain.port.output;

import com.paymentystem.shared.domain.valueobject.Money;

/**
 * Puerto de salida — define lo que el transaction-service
 * necesita del account-service. El dominio no sabe si se
 * comunica por HTTP, Kafka o cualquier otro medio.
 */
public interface AccountServicePort {
    void debit(String accountId, Money amount, String correlationId);
    void credit(String accountId, Money amount, String correlationId);
}