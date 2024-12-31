package com.kris.wall_e.exception;

import com.kris.wall_e.enums.ErrorCode;

public class ResourceNotFoundException extends BaseBusinessException {
    public ResourceNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}