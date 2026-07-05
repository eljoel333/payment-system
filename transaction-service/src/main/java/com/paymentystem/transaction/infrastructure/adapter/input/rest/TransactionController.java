package com.paymentystem.transaction.infrastructure.adapter.input.rest;

import com.paymentystem.transaction.domain.model.TransactionType;
import com.paymentystem.transaction.domain.port.input.ProcessTransactionUseCase;
import com.paymentystem.transaction.infrastructure.adapter.input.rest.dto.TransferRequest;
import com.paymentystem.transaction.infrastructure.adapter.input.rest.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ProcessTransactionUseCase processTransactionUseCase;

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var result = processTransactionUseCase.execute(
                new ProcessTransactionUseCase.TransactionCommand(
                        TransactionType.TRANSFER,
                        request.sourceAccountId(),
                        request.targetAccountId(),
                        request.amount(),
                        request.currencyCode(),
                        jwt.getSubject()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransactionResponse.from(result));
    }
}