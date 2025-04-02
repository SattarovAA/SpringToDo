package com.emobile.springtodo.controller.handler;

/**
 * Error Response type for ExceptionHandler.
 *
 * @param errorMessage Error Response message.
 */
public record ErrorResponse(String errorMessage) {
}
