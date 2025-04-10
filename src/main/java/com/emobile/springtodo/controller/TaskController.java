package com.emobile.springtodo.controller;

import com.emobile.springtodo.aop.logger.LazyLogger;
import com.emobile.springtodo.mapper.TaskMapper;
import com.emobile.springtodo.model.dto.task.TaskInsertRequest;
import com.emobile.springtodo.model.dto.task.TaskListResponse;
import com.emobile.springtodo.model.dto.task.TaskResponse;
import com.emobile.springtodo.model.dto.task.TaskSaveRequest;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.service.TaskService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for working with entity user.
 *
 * @see TaskSaveRequest
 * @see TaskInsertRequest
 * @see TaskResponse
 * @see TaskListResponse
 */
@Tag(name = "TaskController",
        description = "Controller for working with tasks.")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/task")
public class TaskController {
    /**
     * Service for working with entity task.
     */
    private final TaskService taskService;
    /**
     * Mapper for working with entity task.
     */
    private final TaskMapper taskMapper;

    /**
     * Get all taskList.
     *
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link TaskListResponse}.
     */
    @Operation(
            summary = "Get all tasks.",
            description = "Only with admin access.",
            tags = {"task", "get"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = TaskListResponse.class))
    })
    @ApiResponse(responseCode = "401")
    @ApiResponse(responseCode = "403")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @LazyLogger
    public ResponseEntity<TaskListResponse> getAll(@Valid PageInfo pageInfo) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelListToModelListResponse(
                        taskService.findAll(pageInfo)
                ));
    }

    /**
     * Get a task object by specifying its {@code id}.
     * The response is task object with
     * id, username, password, email, content roles.
     *
     * @param id the {@code id} of the {@code user} to retrieve.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link TaskResponse} with searched {@code id}.
     */
    @Operation(
            summary = "Get task by id.",
            description = "Get a Task object by specifying its id. " +
                          "The response is Task object with " +
                          "id, name, description, status, createdAt, updatedAt, authorId.",
            tags = {"task", "get"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = TaskResponse.class))
    })
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(path = "/{id}")
    @LazyLogger
    public ResponseEntity<TaskResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponse(taskService.findById(id)));
    }

    /**
     * Create new task by {@link TaskSaveRequest}.
     *
     * @param modelRequest {@link TaskSaveRequest} to create new task.
     * @return {@link ResponseEntity} with {@link HttpStatus#CREATED}
     * and {@link TaskResponse} by saved task.
     */
    @Operation(
            summary = "Create new task.",
            description = "Only with admin access.",
            tags = {"task", "post"})
    @ApiResponse(responseCode = "201", content = {
            @Content(schema = @Schema(implementation = TaskResponse.class))
    })
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping()
    @LazyLogger
    public ResponseEntity<TaskResponse> create(
            @RequestBody @Valid TaskSaveRequest modelRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskMapper.modelToResponse(
                        taskService.save(
                                taskMapper.requestToModel(modelRequest)
                        )
                ));
    }

    /**
     * Update task with {@code id} by {@link TaskInsertRequest}.
     *
     * @param id           task {@code id} to update task.
     * @param modelRequest {@link TaskInsertRequest} to update task.
     * @return {@link ResponseEntity} with {@link HttpStatus#OK}
     * and {@link TaskResponse} by updated task.
     */
    @Operation(
            summary = "Update task by specifying its id.",
            tags = {"task", "put"})
    @ApiResponse(responseCode = "200", content = {
            @Content(schema = @Schema(implementation = TaskResponse.class))
    })
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping(path = "/{id}")
    @LazyLogger
    public ResponseEntity<TaskResponse> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid TaskInsertRequest modelRequest
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskMapper.modelToResponse(
                        taskService.update(
                                id,
                                taskMapper.requestToModel(modelRequest)
                        )
                ));
    }

    /**
     * Delete task by {@code id}.
     *
     * @param id task {@code id} to delete task.
     * @return {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT}.
     */
    @Operation(
            summary = "Delete task by specifying its id.",
            tags = {"task", "delete"})
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping(path = "/{id}")
    @LazyLogger
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        taskService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Delete all tasks.
     *
     * @return {@link ResponseEntity} with {@link HttpStatus#NO_CONTENT}.
     */
    @Operation(
            summary = "Delete all tasks.",
            tags = {"task", "delete"})
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "400")
    @ApiResponse(responseCode = "401")
    @ApiResponse(responseCode = "403")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(path = "")
    @LazyLogger
    public ResponseEntity<Void> deleteAll() {
        taskService.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
