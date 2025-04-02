package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.config.property.cache.AppCacheProperties;
import com.emobile.springtodo.exception.EntityNotFoundException;
import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.security.AppUserDetails;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.TaskRepository;
import com.emobile.springtodo.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for working with entity {@link Task}.
 */
@RequiredArgsConstructor
@CacheConfig
@Service
public class TaskServiceImpl implements TaskService {
    /**
     * Self object for working with proxy.
     *
     * @see #setSelf(TaskService)
     */
    private TaskService self;
    /**
     * {@link Task} Repository.
     */
    private final TaskRepository taskRepository;
    /**
     * Time management object.
     */
    private final Clock clock;

    @Lazy
    @Autowired
    public void setSelf(TaskService self) {
        this.self = self;
    }

    /**
     * Find all {@link Task} objects
     * with {@code pageNumber} and {@code pageSize} from {@code pageInfo}.
     *
     * @return {@link Task} list.
     */
    @Override
    @Cacheable(AppCacheProperties.Types.TASKS)
    @Transactional(readOnly = true)
    @LazyLogger
    public List<Task> findAll(PageInfo pageInfo) {
        return taskRepository.findAll(pageInfo).content();
    }

    /**
     * Get a {@link Task} object by specifying its id.
     *
     * @param id id searched {@link Task}.
     * @return object of type {@link Task} with searched id.
     * @throws EntityNotFoundException if {@link Task} with id not found.
     */
    @Override
    @Cacheable(value = AppCacheProperties.Types.TASK_BY_ID, key = "#id")
    @Transactional(readOnly = true)
    @LazyLogger
    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "Task with id {0} not found!",
                                id
                        )
                )
        );
    }

    /**
     * Save object model of type {@link Task}.
     *
     * @param model object of type {@link Task} to save.
     * @return object of type {@link Task} that was saved.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.Types.TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_ID, key = "#result.authorId"),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_NAME, allEntries = true)
    })
    @Transactional
    @LazyLogger
    public Task save(Task model) {
        model = enrich(model);
        return taskRepository.save(model);
    }

    /**
     * Update object model of type {@link Task} with T.id equals id.
     *
     * @param id    id of the object to be updated.
     * @param model object of type {@link Task} to update.
     * @return object of type {@link Task} that was updated.
     */
    @Override
    @Caching(put = {
            @CachePut(value = AppCacheProperties.Types.TASK_BY_ID, key = "#id")
    }, evict = {
            @CacheEvict(value = AppCacheProperties.Types.TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_ID, key = "#result.authorId"),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_NAME, allEntries = true)
    })
    @Transactional
    @LazyLogger
    public Task update(Long id, Task model) {
        model = enrich(model, self.findById(id));
        return taskRepository.update(model);
    }

    /**
     * Enrich model to full version.
     *
     * @param model {@link Task} to enrich.
     * @return {@link Task} with all fields.
     */
    private Task enrich(Task model) {
        return Task.builder()
                .name(model.getName())
                .description(model.getDescription())
                .status(model.getStatus())
                .createdAt(LocalDateTime.now(clock))
                .updatedAt(LocalDateTime.now(clock))
                .authorId(getCurrentUserId())
                .build();
    }

    private Long getCurrentUserId() {
        AppUserDetails userDetails =
                (AppUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
        return userDetails.getUserId();
    }

    /**
     * Enrich {@code model} to full version.
     * If the {@code model} has no field values, then the values are taken
     * from a previously existing entity with the same id.
     *
     * @param model        {@link Task} with partially updated fields.
     * @param taskToUpdate source entity to update {@link Task}.
     * @return Updated {@link Task}.
     */
    private Task enrich(Task model, Task taskToUpdate) {
        return Task.builder()
                .id(taskToUpdate.getId())
                .name(model.getName() == null
                        ? taskToUpdate.getName()
                        : model.getName())
                .description(model.getDescription() == null
                        ? taskToUpdate.getDescription()
                        : model.getDescription())
                .status(model.getStatus() == null
                        ? taskToUpdate.getStatus()
                        : model.getStatus())
                .createdAt(taskToUpdate.getCreatedAt())
                .updatedAt(LocalDateTime.now(clock))
                .authorId(taskToUpdate.getAuthorId())
                .build();
    }

    /**
     * Delete object with User.id equals {@code id} from database.
     *
     * @param id id of the object to be deleted.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.Types.TASK_BY_ID, key = "#id"),
            @CacheEvict(value = AppCacheProperties.Types.TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_ID, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_NAME, allEntries = true)
    })
    @Transactional
    @LazyLogger
    public void deleteById(Long id) {
        self.findById(id);
        taskRepository.deleteById(id);
    }

    /**
     * Delete all {@link Task} objects.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.Types.TASK_BY_ID, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_ID, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_NAME, allEntries = true)
    })
    @Transactional
    @LazyLogger
    public void deleteAll() {
        taskRepository.deleteAll();
    }

    /**
     * Delete all {@link Task} objects
     * for user with {@code userId}.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.Types.TASK_BY_ID, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_ID, key = "#userId"),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_NAME, allEntries = true)
    })
    @Transactional
    @LazyLogger
    public void deleteAllByUserId(Long userId) {
        taskRepository.deleteAllByUserId(userId);
    }
}
