package com.emobile.springtodo.controller.handler;

import lombok.Builder;

/**
 * Error Response type for ExceptionHandler.
 *
 * @param message     Error Response message.
 * @param description Error Response description.
 */
@Builder
public record ErrorResponseBody(
        String message,
        String description) {
}
