package com.kris.wall_e.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWallet(
        @NotBlank(message = "userId must be present.")
        Long userId
) {
}
