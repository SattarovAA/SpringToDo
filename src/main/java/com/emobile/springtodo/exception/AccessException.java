package com.emobile.springtodo.exception;

/**
 * Exception to handle access problems.
 */
public class AccessException extends RuntimeException {
    /**
     * Create new Object with message.
     *
     * @param message exception message.
     * @see RuntimeException#RuntimeException(String)
     */
    public AccessException(String message) {
        super(message);
    }
}
