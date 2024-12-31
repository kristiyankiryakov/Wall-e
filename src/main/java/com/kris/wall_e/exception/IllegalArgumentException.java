package com.kris.wall_e.exception;

import com.kris.wall_e.enums.ErrorCode;

public class IllegalArgumentException extends BaseBusinessException {
    public IllegalArgumentException(String message) {
        super(ErrorCode.ILLEGAL_ARGUMENT, message);
    }
}
