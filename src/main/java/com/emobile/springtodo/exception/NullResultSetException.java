package com.emobile.springtodo.exception;

import java.util.function.Supplier;

/**
 * Exception for handle unsupported null result in sql query.
 */
public class NullResultSetException extends RuntimeException {
    /**
     * Create new Object with message.
     *
     * @param message exception message.
     * @see RuntimeException#RuntimeException(String)
     */
    public NullResultSetException(String message) {
        super(message);
    }

    /**
     * Create new Object with message.
     * <br>
     * Uses in orElseThrow block.
     * <br>
     * Uses {@link #NullResultSetException(String)}
     *
     * @param message exception message.
     * @return Supplier with {@link NullResultSetException}.
     */
    public static Supplier<NullResultSetException> create(String message) {
        return () -> new NullResultSetException(message);
    }
}
