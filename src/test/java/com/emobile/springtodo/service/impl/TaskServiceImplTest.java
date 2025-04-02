package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.exception.EntityNotFoundException;
import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.AppUserDetails;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImplTest Tests")
class TaskServiceImplTest {
    private TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);
    private final Clock clock = Clock.fixed(MILLENNIUM.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);


    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, clock);
        taskService.setSelf(taskService);
    }

    @Test
    @DisplayName("findAll test: get all user data.")
    void givenWhenGetAllThenListUser() {
        List<Task> userList = new ArrayList<>(List.of(
                new Task(),
                new Task()
        ));
        PageInfo pageInfo = new PageInfo(0, 10);

        when(taskRepository.findAll(pageInfo))
                .thenReturn(new Page<>(userList));

        List<Task> actual = taskService.findAll(pageInfo);

        assertEquals(userList.size(), actual.size());
        verify(taskRepository, times(1))
                .findAll(pageInfo);
    }

    @Test
    @DisplayName("findById test: get user data by id.")
    void givenExistingIdWhenGetByIdThenUser() {
        Long userId = 1L;
        Task defaultTask = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                LocalDateTime.of(2, 2, 2, 2, 2),
                LocalDateTime.of(2, 2, 2, 2, 2),
                2L
        );

        when(taskRepository.findById(userId))
                .thenReturn(Optional.of(defaultTask));

        Task actual = taskService.findById(userId);

        assertEquals(defaultTask, actual);
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("findById test: try to get user data by not existing id.")
    void givenNotExistingIdWhenGetByIdThenThrow() {
        Long userId = 1L;

        when(taskRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.findById(userId),
                " index is incorrect."
        );
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("save test: send user data to repository.")
    void givenUserWhenSendUserToDbThenSavedUser() {
        Long userPrincipalId = 10L;
        Task taskToSave = new Task(
                null,
                "name",
                "description",
                TaskStatus.DONE,
                null,
                null,
                null
        );
        Task expected = new Task(
                null,
                "name",
                "description",
                TaskStatus.DONE,
                MILLENNIUM,
                MILLENNIUM,
                userPrincipalId
        );
        User defaultUser = new User(
                userPrincipalId,
                "username",
                "password",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        AppUserDetails principal = new AppUserDetails(defaultUser);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        principal, "password", principal.getAuthorities()
                );
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(taskRepository.save(expected))
                .thenReturn(expected);

        Task actual = taskService.save(taskToSave);

        assertEquals(expected, actual);
        verify(taskRepository, times(1))
                .save(any());
    }

    @Test
    @DisplayName("update test: with partially filled User data.")
    void givenPartiallyFilledUserUpdateThenUpdatedUser() {
        Long userId = 1L;
        Task partiallyFilledTask = new Task(
                null,
                "name",
                null,
                TaskStatus.TODO,
                null,
                null,
                2L
        );
        Task taskToUpdate = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                BEFORE_MILLENNIUM,
                2L
        );
        Task expected = new Task(
                1L,
                "name",
                "description",
                TaskStatus.TODO,
                BEFORE_MILLENNIUM,
                MILLENNIUM,
                2L
        );


        when(taskRepository.findById(userId))
                .thenReturn(Optional.of(taskToUpdate));
        when(taskRepository.update(expected))
                .thenReturn(expected);

        Task actual = taskService.update(userId, partiallyFilledTask);

        assertEquals(expected, actual);
        verify(taskRepository, times(1))
                .update(expected);
        verify(taskRepository, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("update test: with filled User data.")
    void givenFilledUserAndUserIdWhenUpdateThenUpdatedUser() {
        Long userId = 1L;
        Task taskToUpdate = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                BEFORE_MILLENNIUM,
                2L
        );
        Task expected = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                MILLENNIUM,
                2L
        );

        when(taskRepository.findById(userId))
                .thenReturn(Optional.of(expected));
        when(taskRepository.update(expected))
                .thenReturn(expected);

        Task actual = taskService.update(userId, taskToUpdate);

        assertEquals(expected, actual);
        verify(taskRepository, times(1))
                .update(expected);
        verify(taskRepository, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("update test: try update with not existed user id.")
    void givenUserAndNotExistedUserIdWhenUpdateThenUpdatedUser() {
        Long notExistedUserId = 1L;
        LocalDateTime creationTime = LocalDateTime.of(1999, Month.DECEMBER, 10, 0, 0, 0);
        Task taskToUpdate = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                creationTime,
                creationTime,
                2L
        );

        when(taskRepository.findById(notExistedUserId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.update(notExistedUserId, taskToUpdate),
                "UserId is incorrect."
        );
        verify(taskRepository, times(0))
                .update(any());
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("delete test: delete user data message to repository.")
    void givenExistedUserIdWhenDeleteThenVoid() {
        Long existedTaskId = 1L;

        when(taskRepository.findById(existedTaskId))
                .thenReturn(Optional.of(new Task()));
        taskService.deleteById(existedTaskId);

        verify(taskRepository, times(1))
                .findById(existedTaskId);
        verify(taskRepository, times(1))
                .deleteById(existedTaskId);
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenNotExistedUserIdWhenDeleteThenVoid() {
        Long notExistedUserId = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> taskService.deleteById(notExistedUserId),
                "UserId is incorrect."
        );
        verify(taskRepository, times(1))
                .findById(notExistedUserId);
        verify(taskRepository, times(0))
                .deleteById(notExistedUserId);
    }
}