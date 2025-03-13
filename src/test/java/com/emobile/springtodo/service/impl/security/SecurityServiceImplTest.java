package com.emobile.springtodo.service.impl.security;

import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityServiceImplTest Tests")
class SecurityServiceImplTest {
    private SecurityServiceImpl securityService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityServiceImpl(userService);
    }

    @Test
    @DisplayName("registerNewUser test: send to new user data to UserService.")
    void givenUserWhenRegisterNewUserThenUser() {
        User user = new User(
                1L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );

        when(userService.save(user)).thenReturn(user);
        User actual = securityService.registerNewUser(user);

        assertEquals(user, actual);
        verify(userService, times(1))
                .save(any());
    }
}
