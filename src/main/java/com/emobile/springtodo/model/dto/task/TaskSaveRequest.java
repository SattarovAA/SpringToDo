package com.emobile.springtodo.model.dto.task;

import com.emobile.springtodo.model.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for working with entity Task.
 */
public record TaskSaveRequest(
        @NotBlank(message = "Field name must be filled!")
        @Size(max = MAX_NAME_SIZE,
                message = "Field name must be less than"
                          + MAX_NAME_SIZE)
        String name,
        @NotBlank(message = "Field description must be filled!")
        @Size(max = MAX_DESCRIPTION_SIZE,
                message = "Field description must be less than"
                          + MAX_DESCRIPTION_SIZE)
        String description,
        @NotNull(message = "Field status must be filled!")
        TaskStatus status
) {
    /**
     * Maximum size of the name field.
     */
    private static final int MAX_NAME_SIZE = 20;
    /**
     * Maximum size of the description field.
     */
    private static final int MAX_DESCRIPTION_SIZE = 20;
}
