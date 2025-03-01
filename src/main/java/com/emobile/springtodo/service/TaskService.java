package com.emobile.springtodo.service;

import com.emobile.springtodo.model.entity.Task;

/**
 * Default interface service for working with entity {@link Task}.
 */
public interface TaskService extends CrudService<Task> {
    /**
     * Delete all {@link Task} objects.
     */
    void deleteAll();

    /**
     * Delete all {@link Task} objects
     * for user with {@code userId}.
     */
    void deleteAllByUserId(Long id);
}
