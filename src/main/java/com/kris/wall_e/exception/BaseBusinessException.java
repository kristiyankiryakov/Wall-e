package com.kris.wall_e.exception;


import com.kris.wall_e.enums.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BaseBusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BaseBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
