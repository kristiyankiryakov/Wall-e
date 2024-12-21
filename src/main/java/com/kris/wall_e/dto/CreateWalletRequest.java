package com.kris.wall_e.dto;

import jakarta.validation.constraints.NotNull;

public record CreateWalletRequest(
        @NotNull(message = "userId must be present.")
        Long userId
) {
}
