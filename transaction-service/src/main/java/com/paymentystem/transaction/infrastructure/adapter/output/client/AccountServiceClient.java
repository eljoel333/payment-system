package com.paymentystem.transaction.infrastructure.adapter.output.client;

import com.paymentystem.shared.domain.exception.DomainException;
import com.paymentystem.shared.domain.valueobject.Money;
import com.paymentystem.transaction.domain.port.output.AccountServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Component
public class AccountServiceClient implements AccountServicePort {

    private final RestClient restClient;
    private final String accountServiceUrl;

    public AccountServiceClient(
            @Value("${services.account.url:http://localhost:8082}") String accountServiceUrl) {
        this.accountServiceUrl = accountServiceUrl;
        this.restClient = RestClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }

    @Override
    public void debit(String accountId, Money amount, String correlationId) {
        String url = accountServiceUrl + "/api/v1/accounts/" + accountId + "/debit";
        log.info("     ┌─ HTTP POST {}", url);
        log.info("     │  body: amount={} correlationId={}", amount.amount(), correlationId);

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri("/api/v1/accounts/{accountId}/debit", accountId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + extractToken())
                    .body(Map.of(
                            "amount", amount.amount(),
                            "correlationId", correlationId
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("     └─ HTTP {} ✓", response.getStatusCode());
        } catch (RestClientException ex) {
            log.error("     └─ HTTP ERROR: {}", ex.getMessage());
            throw new DomainException("ACCOUNT_DEBIT_FAILED",
                    "No se pudo debitar la cuenta: " + ex.getMessage());
        }
    }

    @Override
    public void credit(String accountId, Money amount, String correlationId) {
        String url = accountServiceUrl + "/api/v1/accounts/" + accountId + "/deposit";
        log.info("     ┌─ HTTP POST {}", url);
        log.info("     │  body: amount={} correlationId={}", amount.amount(), correlationId);

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri("/api/v1/accounts/{accountId}/deposit", accountId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + extractToken())
                    .body(Map.of(
                            "amount", amount.amount(),
                            "correlationId", correlationId
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("     └─ HTTP {} ✓", response.getStatusCode());
        } catch (RestClientException ex) {
            log.error("     └─ HTTP ERROR: {}", ex.getMessage());
            throw new DomainException("ACCOUNT_CREDIT_FAILED",
                    "No se pudo acreditar la cuenta: " + ex.getMessage());
        }
    }

    // Extrae el JWT del contexto de seguridad de Spring —
    // el token que el usuario mandó al transaction-service
    // se propaga automáticamente al account-service
    private String extractToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        throw new DomainException("AUTH_TOKEN_MISSING",
                "No hay token disponible para llamar al account-service");
    }
}