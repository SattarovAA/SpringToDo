package com.emobile.springtodo.service;

import com.emobile.springtodo.model.entity.User;

/**
 * Default interface service for working with entity {@link User}.
 */
public interface UserService extends CrudService<User> {
    /**
     * Find {@link User}
     * with {@code User.username} equals {@code username}.
     *
     * @param username username searched {@link User}.
     * @return {@link User} with searched username.
     */
    User findByUsername(String username);

    /**
     * Check duplicate {@code username}.
     * For save {@link User}.
     *
     * @param username username to check
     */
    void checkDuplicateUsername(String username);

    /**
     * Check duplicate {@code username}.
     * For update {@link User}.
     *
     * @param username      username to check
     * @param currentUserId current user id
     */
    void checkDuplicateUsername(String username, Long currentUserId);

    /**
     * Check duplicate {@code email}.
     * For save {@link User}.
     *
     * @param email username searched {@link User}
     */
    void checkDuplicateEmail(String email);

    /**
     * Check duplicate {@code email}.
     * For update {@link User}.
     *
     * @param email username searched {@link User}
     */
    void checkDuplicateEmail(String email, Long currentUserId);
}
