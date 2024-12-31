package com.kris.wall_e.dto;

import com.kris.wall_e.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionResponse(
        Long walletId,
        String owner,
        BigDecimal previousBalance,
        BigDecimal currentBalance,
        TransactionType transactionType,
        BigDecimal transactionAmount
) {
}
