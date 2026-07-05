package com.paymentystem.transaction.domain.strategy;

import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.transaction.domain.model.TransactionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PATRÓN: Strategy + Registry.
 * Recibe TODAS las strategies disponibles via inyección de dependencias
 * y las indexa por tipo. El servicio solo llama resolve(type) — nunca
 * tiene un if/else para decidir qué strategy usar.
 */
@Component
public class TransactionStrategyResolver {

    private final Map<TransactionType, TransactionStrategy> strategies;

    public TransactionStrategyResolver(List<TransactionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(TransactionStrategy::type, Function.identity()));
    }

    public TransactionStrategy resolve(TransactionType type) {
        TransactionStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new DomainException("TRANSACTION_UNSUPPORTED_TYPE",
                    "Tipo de transacción no soportado: " + type);
        }
        return strategy;
    }
}