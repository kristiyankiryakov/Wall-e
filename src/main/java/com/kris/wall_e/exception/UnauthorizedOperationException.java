package com.kris.wall_e.exception;

import com.kris.wall_e.enums.ErrorCode;


public class UnauthorizedOperationException extends BaseBusinessException {
    public UnauthorizedOperationException(String message) {
        super(ErrorCode.UNAUTHORIZED_OPERATION, message);
    }
}
