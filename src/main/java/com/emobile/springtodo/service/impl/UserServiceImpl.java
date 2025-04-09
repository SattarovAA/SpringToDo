package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.aop.logger.LazyLogger;
import com.emobile.springtodo.aop.session.LazySession;
import com.emobile.springtodo.model.util.SessionAction;
import com.emobile.springtodo.model.util.TransactionalType;
import com.emobile.springtodo.config.property.cache.AppCacheProperties;
import com.emobile.springtodo.exception.AlreadyExitsException;
import com.emobile.springtodo.exception.EntityNotFoundException;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.UserRepository;
import com.emobile.springtodo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * Service for working with entity {@link User}.
 */
@RequiredArgsConstructor
@CacheConfig
@Service
public class UserServiceImpl implements UserService {
    /**
     * Self object for working with proxy.
     *
     * @see #setSelf(UserService)
     */
    private UserService self;
    /**
     * {@link User} Repository.
     */
    private final UserRepository userRepository;
    /**
     * Default PasswordEncoder.
     * Needed to define and update the field password in {@link User}.
     *
     * @see #enrich(User)
     * @see #enrich(User, User)
     */
    private final PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    public void setSelf(UserService self) {
        this.self = self;
    }

    /**
     * Find all {@link User} objects
     * with {@code pageNumber} and {@code pageSize} from {@code pageInfo}.
     *
     * @return {@link User} list.
     */
    @Override
    @Cacheable(AppCacheProperties.Types.USERS)
    @LazySession
    @LazyLogger
    public List<User> findAll(PageInfo pageInfo) {
        return userRepository.findAll(pageInfo).content();
    }

    /**
     * Get a {@link User} object by specifying its {@code id}.
     *
     * @param id id searched {@link User}.
     * @return object of type {@link User} with searched id.
     * @throws EntityNotFoundException if {@link User} with id not found.
     */
    @Override
    @Cacheable(value = AppCacheProperties.Types.USER_BY_ID, key = "#id")
    @LazySession(action = SessionAction.CHECK)
    @LazyLogger
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "User with id {0} not found!",
                                id
                        )
                )
        );
    }

    /**
     * Find {@link User} with User.username equals {@code username}.
     *
     * @param username username searched User.
     * @return object of type {@link User} with searched id.
     * @throws EntityNotFoundException if {@link User} with username not found.
     */
    @Override
    @Cacheable(value = AppCacheProperties.Types.USER_BY_NAME, key = "#username")
    @LazySession(action = SessionAction.CHECK)
    @LazyLogger
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                EntityNotFoundException.create(
                        MessageFormat.format(
                                "User with username {0} not found!",
                                username
                        )
                )
        );
    }

    /**
     * Save object model of type {@link User}.
     *
     * @param model object of type {@link User} to save.
     * @return object of type {@link User} that was saved.
     */
    @Override
    @Cacheable(value = AppCacheProperties.Types.USER_BY_NAME, key = "#model.username")
    @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true)
    @LazySession(type = TransactionalType.AROUND)
    @LazyLogger
    public User save(User model) {
        model = enrich(model);
        self.checkDuplicateUsername(model.getUsername());
        self.checkDuplicateEmail(model.getEmail());
        return userRepository.save(model);
    }

    /**
     * Update object model of type {@link User} with T.id equals id.
     *
     * @param id    id of the object to be updated.
     * @param model object of type {@link User} to update.
     * @return object of type {@link User} that was updated.
     */
    @Override
    @Caching(put = {
            @CachePut(value = AppCacheProperties.Types.USER_BY_ID, key = "#id"),
            @CachePut(value = AppCacheProperties.Types.USER_BY_NAME, key = "#result.username")
    })
    @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true)
    @LazySession(type = TransactionalType.AROUND)
    @LazyLogger
    public User update(Long id, User model) {
        model = enrich(model, self.findById(id));
        self.checkDuplicateUsername(model.getUsername(), model.getId());
        self.checkDuplicateEmail(model.getEmail(), model.getId());
        return userRepository.update(model);
    }

    /**
     * Enrich model to full version.
     *
     * @param model {@link User} to enrich.
     * @return {@link User} with all fields.
     */
    private User enrich(User model) {
        return User.builder()
                .username(model.getUsername())
                .password(passwordEncoder.encode(model.getPassword()))
                .role(model.getRole())
                .email(model.getEmail())
                .build();
    }

    /**
     * Enrich {@code model} to full version.
     * If the {@code model} has no field values, then the values are taken
     * from a previously existing entity with the same id.
     *
     * @param model        {@link User} with partially updated fields.
     * @param userToUpdate source entity to update {@link User}.
     * @return Updated {@link User}.
     */
    private User enrich(User model, User userToUpdate) {
        return User.builder()
                .id(userToUpdate.getId())
                .username(model.getUsername() == null
                        ? userToUpdate.getUsername()
                        : model.getUsername())
                .password(model.getPassword() == null
                        ? passwordEncoder.encode(userToUpdate.getPassword())
                        : passwordEncoder.encode(model.getPassword()))
                .role(model.getRole() == null
                        ? userToUpdate.getRole()
                        : model.getRole())
                .email(model.getEmail() == null
                        ? userToUpdate.getEmail()
                        : model.getEmail())
                .taskList(model.getTaskList() == null
                        ? userToUpdate.getTaskList()
                        : model.getTaskList())
                .build();
    }

    /**
     * Check duplicate username.
     * For save new {@link User}.
     *
     * @param username username to check.
     * @throws AlreadyExitsException if username already exist.
     */
    @LazySession(action = SessionAction.CHECK)
    @LazyLogger
    public void checkDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Username ({0}) already exist!",
                            username
                    )
            );
        }
    }

    /**
     * Check duplicate username.
     * For update {@link User}.
     *
     * @param username      username to check.
     * @param currentUserId current user id.
     * @throws AlreadyExitsException if username already exist
     *                               excluding {@link User} with currentUserId.
     */
    @LazySession(action = SessionAction.CHECK)
    @LazyLogger
    public void checkDuplicateUsername(String username, Long currentUserId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentUserId)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Username ({0}) already exist!",
                            username
                    )
            );
        }
    }

    /**
     * Check duplicate email.
     * For save new {@link User}.
     *
     * @param email email to check.
     * @throws AlreadyExitsException if email already exist.
     */
    @LazySession(action = SessionAction.CHECK)
    @LazyLogger
    public void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Email ({0}) already exist!",
                            email
                    )
            );
        }
    }

    /**
     * Check duplicate email.
     * For update {@link User}.
     *
     * @param email         email to check.
     * @param currentUserId current user id.
     * @throws AlreadyExitsException if email already exist
     *                               excluding {@link User} with currentUserId.
     */
    @LazySession(action = SessionAction.CHECK)
    @LazyLogger
    public void checkDuplicateEmail(String email, Long currentUserId) {
        if (userRepository.existsByEmailAndIdNot(email, currentUserId)) {
            throw new AlreadyExitsException(
                    MessageFormat.format(
                            "Email ({0}) already exist!",
                            email
                    )
            );
        }
    }

    /**
     * Delete object with User.id equals id from database.
     *
     * @param id id of the object to be deleted.
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_NAME, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.USER_BY_ID, key = "#id"),
            @CacheEvict(value = AppCacheProperties.Types.USERS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.TASKS, allEntries = true),
            @CacheEvict(value = AppCacheProperties.Types.TASK_BY_ID, allEntries = true)
    })
    @LazySession(type = TransactionalType.AROUND)
    @LazyLogger
    public void deleteById(Long id) {
        User user = self.findById(id);
        userRepository.deleteById(user);
    }
}
