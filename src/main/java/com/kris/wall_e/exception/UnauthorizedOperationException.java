package com.kris.wall_e.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedOperationException extends BaseException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
