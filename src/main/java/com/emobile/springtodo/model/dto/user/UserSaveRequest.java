package com.emobile.springtodo.model.dto.user;

import com.emobile.springtodo.model.security.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for working with entity user.
 *
 * @param username user username.
 * @param password user password.
 * @param email    user email.
 * @param role     authentication {@link RoleType}.
 */
public record UserSaveRequest(
        @NotBlank(message = "Field username must be filled!")
        @Size(max = MAX_USERNAME_SIZE,
                message = "Field username must be less than"
                          + MAX_USERNAME_SIZE)
        String username,
        @NotBlank(message = "Field password must be filled!")
        @Size(max = MAX_PASSWORD_SIZE,
                message = "Field password must be less than"
                          + MAX_PASSWORD_SIZE)
        String password,
        @NotNull(message = "Field email must be filled!")
        @Email(message = "Field email must email format!")
        String email,
        @NotNull(message = "Field role must be filled!")
        RoleType role
) {
    private static final int MAX_USERNAME_SIZE = 20;
    private static final int MAX_PASSWORD_SIZE = 20;
}
