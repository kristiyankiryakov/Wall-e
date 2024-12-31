package com.kris.wall_e.dto;

import java.math.BigDecimal;

public record WalletResponse(
        Long id,
        String name,
        String owner,
        BigDecimal balance
) {
}
