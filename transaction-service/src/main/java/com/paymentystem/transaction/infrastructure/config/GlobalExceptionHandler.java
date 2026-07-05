package com.paymentystem.transaction.infrastructure.config;

import com.paymentystem.shared.domain.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
        log.warn("Domain exception: {} — {}", ex.getCode(), ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("code", ex.getCode());
        body.put("message", ex.getMessage());
        body.put("timestamp", Instant.now().toString());

        HttpStatus status = switch (ex.getCode()) {
            case "TRANSACTION_SAME_ACCOUNT",
                 "TRANSACTION_INVALID_AMOUNT",
                 "TRANSACTION_UNSUPPORTED_TYPE" -> HttpStatus.BAD_REQUEST;
            case "ACCOUNT_DEBIT_FAILED",
                 "ACCOUNT_CREDIT_FAILED"        -> HttpStatus.UNPROCESSABLE_ENTITY;
            default                             -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("code", "VALIDATION_ERROR");
        body.put("message", "Datos de entrada inválidos");
        body.put("errors", errors);
        body.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}