package com.emobile.springtodo.controller;

import com.emobile.springtodo.aop.LazyLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TestController",
        description = "Test Controller for secured API calls.")
@RequestMapping("api/test")
@RestController
public class AppController {
    /**
     * Testing endpoint with public access.
     *
     * @return Public response data text message.
     */
    @Operation(
            summary = "Testing endpoint with public access.",
            tags = {"test", "get", "access", "public"})
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404")
    @ApiResponse(responseCode = "500")
    @GetMapping("/all")
    @LazyLogger
    public String allAccess() {
        return "Public response data.";
    }

    /**
     * Testing endpoint with admin access.
     *
     * @return Admin response data text message.
     */
    @Operation(
            summary = "Testing endpoint with admin access.",
            tags = {"test", "get", "access"})
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403")
    @ApiResponse(responseCode = "404")
    @ApiResponse(responseCode = "500")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @LazyLogger
    public String adminAccess() {
        return "Admin response data.";
    }

    /**
     * Testing endpoint with manager access.
     *
     * @return Moderator response data text message.
     */
    @Operation(
            summary = "Testing endpoint with manager access.",
            tags = {"test", "get", "access"})
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403")
    @ApiResponse(responseCode = "404")
    @ApiResponse(responseCode = "500")
    @GetMapping("/manager")
    @PreAuthorize("hasRole('MODERATOR')")
    @LazyLogger
    public String managerAccess() {
        return "Moderator response data.";
    }

    /**
     * Testing endpoint with user access.
     *
     * @return User response data text message.
     */
    @Operation(
            summary = "Testing endpoint with user access.",
            tags = {"test", "get", "access"})
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403")
    @ApiResponse(responseCode = "404")
    @ApiResponse(responseCode = "500")
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    @LazyLogger
    public String userAccess() {
        return "User response data.";
    }

    /**
     * Testing endpoint with any auth access.
     *
     * @return Authenticated response data text message.
     */
    @Operation(
            summary = "Testing endpoint with any auth access.",
            description = "user or admin access.",
            tags = {"test", "get", "access"})
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "403")
    @ApiResponse(responseCode = "404")
    @ApiResponse(responseCode = "500")
    @GetMapping("/any")
    @PreAuthorize("isAuthenticated()")
    @LazyLogger
    public String anyAccess() {
        return "Authenticated response data.";
    }
}

