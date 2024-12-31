package com.kris.wall_e.dto;

import com.kris.wall_e.enums.ErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

public record ErrorResponse(
        String message,
        LocalDateTime timestamp,
        Optional<String> code,
        Optional<String> path
) {

    public ErrorResponse(String message) {
        this(message, LocalDateTime.now(), Optional.empty(), Optional.empty());
    }


    public ErrorResponse(String message, ErrorCode code, String path) {
        this(message, LocalDateTime.now(), Optional.of(code.name()), Optional.of(path));
    }
}
