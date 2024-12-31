package com.kris.wall_e.exception;

import com.kris.wall_e.enums.ErrorCode;

public class InsufficientFundsException extends BaseBusinessException {
    public InsufficientFundsException(String message) {
        super(ErrorCode.INSUFFICIENT_FUNDS, message);
    }
}
