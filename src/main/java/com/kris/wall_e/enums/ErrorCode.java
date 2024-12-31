package com.kris.wall_e.enums;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    ALREADY_EXISTS(HttpStatus.CONFLICT, "Resource already exists"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    INSUFFICIENT_FUNDS(HttpStatus.PAYMENT_REQUIRED, "Insufficient funds"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "Invalid argument provided"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."),
    UNAUTHORIZED_OPERATION(HttpStatus.FORBIDDEN, "Operation not permitted");

    private final HttpStatus status;
    private final String defaultMessage;

}
