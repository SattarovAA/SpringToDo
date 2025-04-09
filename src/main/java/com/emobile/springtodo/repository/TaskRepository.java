package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.entity.Task;

/**
 * Default interface repository for working with entity {@link Task}.
 */
public interface TaskRepository extends CrudRepository<Task> {
    /**
     * Delete all {@link Task} objects in database.
     */
    void deleteAll();

    /**
     * Delete all {@link Task} objects in database
     * for user with {@code userId}.
     *
     * @return {@code true} if success
     */
    boolean deleteAllByUserId(Long authorId);
}
