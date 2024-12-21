package com.kris.wall_e.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int errorCode,
        String message,
        LocalDateTime timestamp
) {
}
