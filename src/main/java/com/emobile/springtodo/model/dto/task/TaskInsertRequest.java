package com.emobile.springtodo.model.dto.task;

import com.emobile.springtodo.model.entity.TaskStatus;
import jakarta.validation.constraints.Size;

public record TaskInsertRequest(
        @Size(max = MAX_NAME_SIZE,
                message = "Field name must be less than"
                          + MAX_NAME_SIZE)
        String name,
        @Size(max = MAX_DESCRIPTION_SIZE,
                message = "Field description must be less than"
                          + MAX_DESCRIPTION_SIZE)
        String description,
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