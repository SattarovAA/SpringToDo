package com.emobile.springtodo.exception;

/**
 * Exception for handle unsupported cascade deleting.
 */
public class DeleteEntityWithReferenceException extends RuntimeException {
    /**
     * Create new Object with message.
     *
     * @param message exception message.
     * @see RuntimeException#RuntimeException(String)
     */
    public DeleteEntityWithReferenceException(String message) {
        super(message);
    }
}
