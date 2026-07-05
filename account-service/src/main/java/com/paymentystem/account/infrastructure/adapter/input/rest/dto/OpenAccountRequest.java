package com.paymentystem.account.infrastructure.adapter.input.rest.dto;

import com.paymentystem.account.domain.model.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OpenAccountRequest(
        @NotNull(message = "El tipo de cuenta es requerido")
        AccountType type,

        @NotNull(message = "La moneda es requerida")
        @Pattern(regexp = "MXN|USD", message = "Moneda no soportada — usa MXN o USD")
        String currencyCode
) {}