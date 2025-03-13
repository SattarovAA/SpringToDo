package com.emobile.springtodo.model.dto.util;

import jakarta.validation.constraints.NotBlank;

/**
 * Simple Request message DTO.
 *
 * @param message String message.
 */
public record SimpleRequest(
        @NotBlank(message = "Field message must be filled!")
        String message
) {
}
