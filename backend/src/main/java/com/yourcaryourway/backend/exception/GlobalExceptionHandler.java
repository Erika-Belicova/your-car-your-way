package com.yourcaryourway.backend.exception;

import com.yourcaryourway.backend.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 * Catches and handles various exceptions,
 * returning appropriate HTTP status codes and error messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exception) {
        logger.error("GeneralException: ", exception);
        // 500 general exception
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred"));
    }

    @ExceptionHandler(JwtGenerationException.class)
    public ResponseEntity<ErrorResponse> handleJwtGenerationException(JwtGenerationException exception) {
        logger.error("JwtGenerationException: ", exception);
        // 500 server error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(exception.getMessage()));
    }

}