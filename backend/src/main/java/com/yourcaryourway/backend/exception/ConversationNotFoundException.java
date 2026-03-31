package com.yourcaryourway.backend.exception;

/**
 * Exception thrown when a conversation with the specified identifier
 * is not found in the system.
 */
public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(String message) {
        super(message);
    }

}