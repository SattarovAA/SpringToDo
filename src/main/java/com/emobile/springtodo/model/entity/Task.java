package com.emobile.springtodo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity Task.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@FieldNameConstants
@Builder
public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Long Task id.
     */
    private Long id;
    /**
     * Task name.
     */
    private String name;
    /**
     * Task description.
     */
    private String description;
    /**
     * Task {@link TaskStatus} status.
     */
    private TaskStatus status;
    /**
     * Task creation time without timezone.
     */
    private LocalDateTime createdAt;
    /**
     * Task update time without timezone.
     */
    private LocalDateTime updatedAt;
    /**
     * The User id who owns the Task.
     */
    private Long authorId;
}
