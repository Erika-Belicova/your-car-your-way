package com.yourcaryourway.backend.exception;

/**
 * Exception thrown when a message is sent to a conversation that is not active.
 */
public class ConversationNotActiveException extends RuntimeException {

    public ConversationNotActiveException(String message) {
        super(message);
    }

}