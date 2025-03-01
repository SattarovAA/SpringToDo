package com.emobile.springtodo.model.dto.task;

import com.emobile.springtodo.model.entity.TaskStatus;

public record TaskResponse(
        Long id,
        String name,
        String description,
        TaskStatus status,
        String createdAt,
        String updatedAt,
        Long authorId
) {
}
