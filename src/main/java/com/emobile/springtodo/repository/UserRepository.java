package com.emobile.springtodo.repository;

import com.emobile.springtodo.model.entity.User;

import java.util.Optional;

/**
 * Default interface repository for working with entity {@link User}.
 */
public interface UserRepository extends CrudRepository<User> {
    /**
     * Find {@link User}
     * with {@code User.username} equals {@code username}.
     *
     * @param username username searched {@link User}
     * @return {@link Optional} if exist, {@link Optional#empty()} if not
     */
    Optional<User> findByUsername(String username);

    /**
     * Check duplicate {@code username}.
     * For save {@link User}.
     *
     * @param username username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     */
    boolean existsByUsername(String username);

    /**
     * Check duplicate {@code username}.
     * For update {@link User}.
     *
     * @param username username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     */
    boolean existsByUsernameAndIdNot(String username, Long currentUserId);

    /**
     * Check duplicate {@code email}.
     * For save {@link User}.
     *
     * @param email username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     */
    boolean existsByEmail(String email);

    /**
     * Check duplicate {@code email}.
     * For update {@link User}.
     *
     * @param email username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     */
    boolean existsByEmailAndIdNot(String email, Long currentUserId);
}
