package com.emobile.springtodo.model.dto.user;

import java.util.List;

/**
 * List Response DTO for working with entity user.
 *
 * @param users content of {@link UserResponse}.
 */
public record UserListResponse(
        List<UserResponse> users
) {
}
