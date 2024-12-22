package com.kris.wall_e.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "Deposit amount must not be null.")
        @Positive(message = "Deposit amount must be positive.")
        BigDecimal amount
) {
}
