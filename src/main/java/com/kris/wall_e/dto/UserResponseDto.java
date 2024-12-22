package com.kris.wall_e.dto;

public record UserResponseDto(
        Long userId,
        String name,
        String email
) {
}
