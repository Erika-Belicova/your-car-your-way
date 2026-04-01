package com.yourcaryourway.backend.exception;

/**
 * Exception thrown when JWT token validation fails.
 */
public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message) {
        super(message);
    }
}
