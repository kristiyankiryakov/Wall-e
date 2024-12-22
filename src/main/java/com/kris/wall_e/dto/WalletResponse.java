package com.kris.wall_e.dto;

import java.math.BigDecimal;

public record WalletResponse(
        UserResponseDto userDetails,
        BigDecimal balance
) {
}
