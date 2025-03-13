package com.emobile.springtodo.controller;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.mapper.UserMapper;
import com.emobile.springtodo.model.dto.user.UserSaveRequest;
import com.emobile.springtodo.model.dto.util.SimpleResponse;
import com.emobile.springtodo.service.security.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Controller for new user registration.
 *
 * @see UserSaveRequest
 */
@Tag(name = "AuthenticationController",
        description = "User authentication controller.")
@RequiredArgsConstructor
@RequestMapping("api/auth")
@RestController
public class AuthController {
    /**
     * Service for work with new user.
     */
    private final SecurityService securityService;
    /**
     * Mapper for user entity.
     */
    private final UserMapper userMapper;

    /**
     * Map {@link UserSaveRequest} to user in {@link UserMapper} and register it.
     *
     * @param userSaveRequest {@link UserSaveRequest} for registration.
     * @return {@link ResponseEntity} with {@link SimpleResponse} if created.
     */
    @Operation(
            summary = "Register new User.",
            tags = {"auth", "post", "register", "public"})
    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = SimpleResponse.class))
    })
    @ApiResponse(responseCode = "400")
    @PostMapping("/register")
    @LazyLogger
    public ResponseEntity<SimpleResponse> registerUser(
            @RequestBody @Valid UserSaveRequest userSaveRequest) {
        securityService.registerNewUser(
                userMapper.requestToModel(userSaveRequest)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleResponse("User created!"));
    }
}
