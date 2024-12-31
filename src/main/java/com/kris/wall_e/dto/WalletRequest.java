package com.kris.wall_e.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WalletRequest(
        @NotBlank(message = "wallet name is required.")
        String walletName
) {
}
