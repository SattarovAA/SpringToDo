package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;

import java.util.Optional;

/**
 * Default CRUD interface repository for working with entity {@link T}.
 *
 * @param <T> entity for work
 */
public interface CrudRepository<T> {
    /**
     * Find all {@link T} objects from db
     * with {@code pageNumber} and {@code pageSize} from {@code pageInfo}.
     *
     * @return {@link Page} with {@link T} list.
     */
    Page<T> findAll(PageInfo pageinfo);

    /**
     * Search object {@link T} in db.
     *
     * @param id id searched {@link T} object
     * @return {@link Optional} if exist, {@link Optional#empty()} if not
     */
    Optional<T> findById(Long id);

    /**
     * Save object model of type {@link T}.
     *
     * @param model object of type {@link T} to save
     * @return object of type {@link T} that was saved
     */
    T save(T model);

    /**
     * Update object model of type {@link T}
     * by {@code T.id} value.
     *
     * @param model object of type {@link T} to update
     * @return object of type {@link T} that was updated
     */
    T update(T model);

    /**
     * Delete object with {@code T.id}
     * equals {@code id} from database.
     *
     * @param id id of the object to be deleted
     * @return {@code true} if success
     */
    boolean deleteById(Long id);
}
