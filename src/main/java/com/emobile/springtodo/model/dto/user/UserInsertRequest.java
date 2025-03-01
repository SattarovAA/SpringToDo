package com.emobile.springtodo.model.dto.user;

import com.emobile.springtodo.model.security.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;

/**
 * Request DTO for working with entity user.
 *
 * @param username user username.
 * @param password user password.
 * @param email    user email.
 * @param role     authentication {@link RoleType}.
 */
public record UserInsertRequest(
        @Size(max = MAX_USERNAME_SIZE,
                message = "Field username must be less than"
                          + MAX_USERNAME_SIZE)
        String username,
        @Size(max = MAX_PASSWORD_SIZE,
                message = "Field password must be less than"
                          + MAX_PASSWORD_SIZE)
        String password,
        @Email(message = "Field email must email format!")
        String email,
        RoleType role
) {
    private static final int MAX_USERNAME_SIZE = 20;
    private static final int MAX_PASSWORD_SIZE = 20;
}
