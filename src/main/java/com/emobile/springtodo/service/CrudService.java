package com.emobile.springtodo.service;

import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;

import java.util.List;

/**
 * Default CRUD interface service for working with entity {@link T}.
 *
 * @param <T> entity for work
 */
public interface CrudService<T> {
    /**
     * Find all {@link T} objects from db
     * with {@code pageNumber} and {@code pageSize} from {@code pageInfo}.
     *
     * @return {@link Page} with {@link T} list.
     */
    List<T> findAll(PageInfo pageInfo);

    /**
     * Find object of type {@link T}
     * where {@code T.id} equals {@code id}.
     *
     * @param id id searched {@link T} object
     * @return object of type {@link T} with searched {@code id}
     */
    T findById(Long id);

    /**
     * Save object model of type {@link T}.
     *
     * @param model object of type {@link T} to save
     * @return object of type {@link T} that was saved
     */
    T save(T model);

    /**
     * Update object model of type {@link T}
     * with {@code T.id} equals {@code id}.
     *
     * @param id    id of the object to be updated
     * @param model object of type {@link T} to update
     * @return object of type {@link T} that was updated
     */
    T update(Long id, T model);

    /**
     * Delete object with {@code T.id}
     * equals {@code id} from database.
     *
     * @param id id of the object to be deleted
     */
    void deleteById(Long id);
}
