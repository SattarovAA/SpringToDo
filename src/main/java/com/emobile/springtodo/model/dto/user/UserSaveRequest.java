package com.emobile.springtodo.model.dto.user;

import com.emobile.springtodo.model.security.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for working with entity user.
 */
@Schema(description = "User save entity.")
public record UserSaveRequest(
        @NotBlank(message = "Field username must be filled!")
        @Size(max = MAX_USERNAME_SIZE,
                message = "Field username must be less than"
                          + MAX_USERNAME_SIZE)
        @Schema(description = "User username", example = "user")
        String username,
        @NotBlank(message = "Field password must be filled!")
        @Size(max = MAX_PASSWORD_SIZE,
                message = "Field password must be less than"
                          + MAX_PASSWORD_SIZE)
        @Schema(description = "User password", example = "pass")
        String password,
        @NotNull(message = "Field email must be filled!")
        @Email(message = "Field email must email format!")
        @Schema(description = "User email", example = "user@email.com")
        String email,
        @NotNull(message = "Field role must be filled!")
        @Schema(description = "User security role")
        RoleType role
) {
    private static final int MAX_USERNAME_SIZE = 20;
    private static final int MAX_PASSWORD_SIZE = 20;
}
