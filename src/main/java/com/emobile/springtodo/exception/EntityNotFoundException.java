package com.emobile.springtodo.exception;

import java.util.function.Supplier;

/**
 * Exception to handle entity not found problem.
 */
public class EntityNotFoundException extends RuntimeException {
    /**
     * Create new Object with message.
     *
     * @param message exception message.
     * @see RuntimeException#RuntimeException(String)
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Create new Object with message.
     * <br>
     * Uses in orElseThrow block.
     * <br>
     * Uses {@link #EntityNotFoundException(String)}
     *
     * @param message exception message.
     * @return Supplier with {@link EntityNotFoundException}.
     */
    public static Supplier<EntityNotFoundException> create(String message) {
        return () -> new EntityNotFoundException(message);
    }
}
