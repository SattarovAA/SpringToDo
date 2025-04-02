package com.emobile.springtodo.exception;

/**
 * Exception for handle duplicate problem with unique fields.
 */
public class AlreadyExitsException extends RuntimeException {
    /**
     * Create new Object with message.
     *
     * @param message exception message.
     * @see RuntimeException#RuntimeException(String)
     */
    public AlreadyExitsException(String message) {
        super(message);
    }
}
