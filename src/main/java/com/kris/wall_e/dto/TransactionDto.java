package com.kris.wall_e.dto;

import com.kris.wall_e.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionDto(
        Long id,
        BigDecimal amount,
        TransactionType type,
        Long walletId
) {
}
