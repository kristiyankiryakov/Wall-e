package com.kris.wall_e.controller;

import com.kris.wall_e.exception.BaseException;
import com.kris.wall_e.exception.ErrorResponse;
import com.kris.wall_e.exception.UserAlreadyExistsException;
import com.kris.wall_e.exception.WalletForUserAlreadyExistsException;
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
                400, // HTTP 400 Bad Request
                "Validation failed: " + errors.toString(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        // Common handling logic for all exceptions extending BaseException
        ErrorResponse errorResponse = new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(UserAlreadyExistsException.class)
//    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                409, // HTTP 409 Conflict
//                ex.getMessage(),
//                LocalDateTime.now()
//        );
//        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(WalletForUserAlreadyExistsException.class)
//    public ResponseEntity<ErrorResponse> handleWalletForUserAlreadyExistsException(WalletForUserAlreadyExistsException ex) {
//
//        ErrorResponse errorResponse = new ErrorResponse(
//                409, // HTTP 409 Conflict
//                ex.getMessage(),
//                LocalDateTime.now()
//        );
//        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.CONFLICT);
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Generic error handler with an internal server error code
        ErrorResponse errorResponse = new ErrorResponse(
                500, // HTTP 500 Internal Server Error
                "An unexpected error occurred: " + ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
