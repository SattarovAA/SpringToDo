package com.emobile.springtodo.service.impl.security;

import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.AppUserDetails;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImplTest Tests")
public class UserDetailsServiceImplTest {
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userService);
    }

    @Test
    @DisplayName("loadUserByUsername test: try to load " +
                 "user by username with correct username.")
    void givenExistingUsernameWhenLoadUserByUsernameThenUserDetails() {
        String userUsername = "user";
        User existingUser = new User(
                1L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        UserDetails expected = new AppUserDetails(existingUser);

        when(userService.findByUsername(userUsername))
                .thenReturn(existingUser);

        UserDetails actual = userDetailsService.loadUserByUsername(userUsername);

        assertEquals(expected, actual);
        verify(userService, times(1))
                .findByUsername(any());
    }
}
