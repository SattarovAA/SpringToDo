package com.emobile.springtodo.service.impl.security;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.AppUserDetails;
import com.emobile.springtodo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service for working with {@link AppUserDetails}.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * Service for working with entity {@link User}.
     */
    private final UserService userService;

    /**
     * Load {@link User} by username from {@link UserService}.
     *
     * @param username username searched {@link User}.
     * @return {@link AppUserDetails} with th found user.
     */
    @Override
    @LazyLogger
    public UserDetails loadUserByUsername(String username) {
        return new AppUserDetails(userService.findByUsername(username));
    }
}
