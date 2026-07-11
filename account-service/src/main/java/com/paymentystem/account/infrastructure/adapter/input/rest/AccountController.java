package com.paymentystem.account.infrastructure.adapter.input.rest;

import com.paymentystem.account.domain.port.input.GetAccountUseCase;
import com.paymentystem.account.domain.port.input.OpenAccountUseCase;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.AccountDetailsResponse;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.OpenAccountRequest;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.OpenAccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.paymentystem.account.domain.port.input.DepositUseCase;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.DepositRequest;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.DepositResponse;

import com.paymentystem.account.domain.port.input.DebitUseCase;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.DebitRequest;
import com.paymentystem.account.infrastructure.adapter.input.rest.dto.DebitResponse;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final OpenAccountUseCase openAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final DepositUseCase depositUseCase;

    private final DebitUseCase debitUseCase;



    @PostMapping("/{accountId}/debit")
    public ResponseEntity<DebitResponse> debit(
            @PathVariable String accountId,
            @Valid @RequestBody DebitRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var result = debitUseCase.execute(
                new DebitUseCase.DebitCommand(
                        accountId,
                        null,
                        jwt.getSubject(),
                        request.amount(),
                        request.correlationId(),
                        null
                )
        );
        return ResponseEntity.ok(DebitResponse.from(result));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OpenAccountResponse> open(
            @Valid @RequestBody OpenAccountRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var result = openAccountUseCase.execute(
                new OpenAccountUseCase.OpenAccountCommand(
                        jwt.getSubject(),
                        request.type(),
                        request.currencyCode()
                )
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OpenAccountResponse.from(result));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailsResponse> get(
            @PathVariable String accountId,
            @AuthenticationPrincipal Jwt jwt) {

        var result = getAccountUseCase.execute(accountId, jwt.getSubject());
        return ResponseEntity.ok(AccountDetailsResponse.from(result));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @PathVariable String accountId,
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        var result = depositUseCase.execute(
                new DepositUseCase.DepositCommand(
                        accountId,
                        jwt.getSubject(),
                        request.amount(),
                        null,
                        null    // ← transactionId null en REST
                )
        );
        return ResponseEntity.ok(DepositResponse.from(result));
    }
}