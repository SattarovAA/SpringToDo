package com.emobile.springtodo.service.impl.security;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.service.UserService;
import com.emobile.springtodo.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for work with new {@link User}.
 */
@RequiredArgsConstructor
@Service
public class SecurityServiceImpl implements SecurityService {
    /**
     * Service for work with {@link User} entity.
     */
    private final UserService userService;

    /**
     * Registration new {@link User}.
     *
     * @param user {@link User} for registration.
     * @return registered {@link User}.
     */
    @Override
    @LazyLogger
    public User registerNewUser(User user) {
        return userService.save(user);
    }
}
