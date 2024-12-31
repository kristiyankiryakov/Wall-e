package com.kris.wall_e.exception;

import com.kris.wall_e.enums.ErrorCode;

public class AlreadyExistsException extends BaseBusinessException {
    public AlreadyExistsException(String message) {
        super(ErrorCode.ALREADY_EXISTS, message);
    }
}