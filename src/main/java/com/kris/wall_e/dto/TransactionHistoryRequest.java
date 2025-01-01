package com.kris.wall_e.dto;

import com.kris.wall_e.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionHistoryRequest(
        @NotNull(message = "Amount must not be null.")
        @Positive(message = "Amount must be positive.")
        BigDecimal amount,

        @NotBlank(message = "Transaction type is required.")
        TransactionType type
) {
}
