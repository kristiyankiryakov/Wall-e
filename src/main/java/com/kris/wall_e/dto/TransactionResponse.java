package com.kris.wall_e.dto;

import com.kris.wall_e.entity.TransactionType;

import java.math.BigDecimal;

public record TransactionResponse(
        Long walletId,
        Long userId,
        BigDecimal previousBalance,
        BigDecimal currentBalance,
        TransactionType transactionType,
        BigDecimal transactionAmount
) {
}
