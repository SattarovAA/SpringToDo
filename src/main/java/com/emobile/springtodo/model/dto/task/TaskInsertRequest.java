package com.emobile.springtodo.model.dto.task;

import com.emobile.springtodo.model.entity.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for working with entity Task.
 */
@Schema(description = "Task insert entity.")
public record TaskInsertRequest(
        @Size(max = MAX_NAME_SIZE,
                message = "Field name must be less than"
                          + MAX_NAME_SIZE)
        @Schema(description = "Task name", example = "TaskName")
        String name,
        @Size(max = MAX_DESCRIPTION_SIZE,
                message = "Field description must be less than"
                          + MAX_DESCRIPTION_SIZE)
        @Schema(description = "Task description", example = "TaskDescription")
        String description,
        @Schema(description = "Task status")
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