package com.emobile.springtodo.model.dto.user;

import com.emobile.springtodo.model.security.RoleType;

/**
 * Response DTO for working with entity user.
 *
 * @param id       user id.
 * @param username user username.
 * @param password user password.
 * @param email    user email.
 * @param role     authentication {@link RoleType}.
 */
public record UserResponse(
        Long id,
        String username,
        String password,
        String email,
        RoleType role
) {
}
