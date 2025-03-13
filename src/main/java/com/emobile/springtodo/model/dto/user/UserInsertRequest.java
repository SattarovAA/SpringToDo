package com.emobile.springtodo.model.dto.user;

import com.emobile.springtodo.model.security.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for working with entity user.
 */
@Schema(description = "User insert entity.")
public record UserInsertRequest(
        @Size(max = MAX_USERNAME_SIZE,
                message = "Field username must be less than"
                          + MAX_USERNAME_SIZE)
        @Schema(description = "User username", example = "user")
        String username,
        @Size(max = MAX_PASSWORD_SIZE,
                message = "Field password must be less than"
                          + MAX_PASSWORD_SIZE)
        @Schema(description = "User password", example = "pass")
        String password,
        @Email(message = "Field email must email format!")
        @Schema(description = "User email", example = "user@email.com")
        String email,
        @Schema(description = "User security role")
        RoleType role
) {
    private static final int MAX_USERNAME_SIZE = 20;
    private static final int MAX_PASSWORD_SIZE = 20;
}
