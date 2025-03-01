package com.emobile.springtodo.model.dto.user;

import com.emobile.springtodo.model.dto.task.TaskResponse;
import com.emobile.springtodo.model.security.RoleType;

import java.util.List;

public record UserResponseWith(
        Long id,
        String username,
        String password,
        String email,
        RoleType role,
        List<TaskResponse> taskList
) {
}
