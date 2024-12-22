package com.kris.wall_e.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "Amount must not be null.")
        @Positive(message = "Amount must be positive.")
        BigDecimal amount
) {
}
