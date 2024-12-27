package com.kris.wall_e.dto;

import jakarta.validation.constraints.NotNull;

public record WalletRequest(
        @NotNull(message = "userId must be present.")
        Long userId
) {
}
