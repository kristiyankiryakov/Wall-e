package com.kris.wall_e.exception;

import com.kris.wall_e.enums.ErrorCode;

public class InvalidCredentialsException extends BaseBusinessException {
    public InvalidCredentialsException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }
}