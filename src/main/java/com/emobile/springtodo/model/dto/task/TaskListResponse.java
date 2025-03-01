package com.emobile.springtodo.model.dto.task;

import java.util.List;

/**
 * List Response DTO for working with entity task.
 *
 * @param taskList content of {@link TaskResponse}.
 */
public record TaskListResponse(
        List<TaskResponse> taskList
) {
}
