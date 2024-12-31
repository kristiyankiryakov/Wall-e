package com.kris.wall_e.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistsException extends BaseException {

    public AlreadyExistsException(String message){
        super(message);
    }

}
