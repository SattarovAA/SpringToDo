package com.emobile.springtodo.controller;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.mapper.UserMapper;
import com.emobile.springtodo.model.dto.user.UserInsertRequest;
import com.emobile.springtodo.model.dto.user.UserListResponse;
import com.emobile.springtodo.model.dto.user.UserResponse;
import com.emobile.springtodo.model.dto.user.UserResponseWith;
import com.emobile.springtodo.model.dto.user.UserSaveRequest;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for working with entity user.
 *
 * @see UserSaveRequest
 * @see UserInsertRequest
 * @see UserResponse
 * @see UserResponseWith
 * @see UserListResponse
 */
@Tag(name = "UserController",
        description = "Controller for working with users.")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {
    /**
     * Service for working with entity user.
     */
    private final UserService userService;
    /**
     * Mapper for working with entity user.
     */
    private final UserMapper userMapper;

    /**
     * Get all users.
     *
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link UserListResponse}.
     */
    @Operation(
            summary = "Get all users.",
            description = "Only with admin access.",
            tags = {"user", "get"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = UserListResponse.class))
    })
    @ApiResponse(responseCode = "401")
    @ApiResponse(responseCode = "403")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @LazyLogger
    public ResponseEntity<UserListResponse> getAll(@Valid PageInfo pageInfo) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userMapper.modelListToModelListResponse(
                                userService.findAll(pageInfo)
                        )
                );
    }

    /**
     * Get a user object by specifying its {@code id}.
     * The response is user object with
     * id, username, password, email, content roles.
     *
     * @param id the {@code id} of the {@code user} to retrieve.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link UserResponse} with searched {@code id}.
     */
    @Operation(
            summary = "Get user by id.",
            description = "Get a User object by specifying its id. " +
                          "The response is User object with " +
                          "id, username, password, email, content roles.",
            tags = {"user", "get"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = UserResponseWith.class))
    })
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(path = "/{id}")
    @LazyLogger
    public ResponseEntity<UserResponseWith> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userMapper.modelToResponseWith(userService.findById(id)));
    }

    /**
     * Update user with {@code id} by {@link UserSaveRequest}.
     *
     * @param id           user {@code id} to update user.
     * @param modelRequest {@link UserSaveRequest} to update user.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link UserResponse} by updated user.
     */
    @Operation(
            summary = "Update user by specifying its id.",
            tags = {"user", "put"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = UserResponse.class))
    })
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping(path = "/{id}")
    @LazyLogger
    public ResponseEntity<UserResponse> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserInsertRequest modelRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userMapper.modelToResponse(
                                userService.update(
                                        id,
                                        userMapper.requestToModel(modelRequest)
                                )
                        )
                );
    }

    /**
     * Delete user by {@code id}.
     *
     * @param id user {@code id} to delete user.
     * @return {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT}.
     */
    @Operation(
            summary = "Delete user by specifying its id.",
            tags = {"user", "delete"})
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping(path = "/{id}")
    @LazyLogger
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
