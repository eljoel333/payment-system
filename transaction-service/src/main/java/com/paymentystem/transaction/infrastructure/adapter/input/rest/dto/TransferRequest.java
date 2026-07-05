package com.paymentystem.transaction.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank(message = "La cuenta origen es requerida")
        String sourceAccountId,

        @NotBlank(message = "La cuenta destino es requerida")
        String targetAccountId,

        @NotNull(message = "El monto es requerido")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal amount,

        @NotNull(message = "La moneda es requerida")
        @Pattern(regexp = "MXN|USD", message = "Moneda no soportada")
        String currencyCode
) {}