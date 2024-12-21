package com.kris.wall_e.controller;

import com.kris.wall_e.exception.BaseException;
import com.kris.wall_e.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        ErrorResponse errorResponse = new ErrorResponse(
                400,
                "Validation failed: " + errors.toString(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                500,
                "An unexpected error occurred: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
