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
@DisplayName("TaskServiceImplTest Tests")
class TaskServiceImplTest {
    private TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserServiceImpl userService;
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);
    private final Clock clock = Clock.fixed(MILLENNIUM.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);


    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, userService, clock);
        taskService.setSelf(taskService);
    }

    @Test
    @DisplayName("findAll test: get all user data.")
    void givenWhenGetAllThenListTask() {
        List<Task> taskList = new ArrayList<>(List.of(
                new Task(),
                new Task()
        ));
        PageInfo pageInfo = new PageInfo(0, 10);

        when(taskRepository.findAll(pageInfo))
                .thenReturn(new Page<>(taskList));

        List<Task> actual = taskService.findAll(pageInfo);

        assertEquals(taskList.size(), actual.size());
        verify(taskRepository, times(1))
                .findAll(pageInfo);
    }

    @Test
    @DisplayName("findById test: get task data by id.")
    void givenExistingIdWhenGetByIdThenTask() {
        Long taskId = 1L;
        User defaultUser = new User(
                2L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        Task defaultTask = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                LocalDateTime.of(2, 2, 2, 2, 2),
                LocalDateTime.of(2, 2, 2, 2, 2),
                defaultUser
        );

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(defaultTask));

        Task actual = taskService.findById(taskId);

        assertEquals(defaultTask, actual);
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("findById test: try to get task data by not existing id.")
    void givenNotExistingIdWhenGetByIdThenThrow() {
        Long taskId = 1L;

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.findById(taskId),
                " index is incorrect."
        );
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("save test: send task data to repository.")
    void givenTaskWhenSendTaskToDbThenSavedTask() {
        Long userPrincipalId = 10L;
        User defaultUser = new User(
                userPrincipalId,
                "username",
                "password",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
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
                defaultUser
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
        when(userService.findById(userPrincipalId))
                .thenReturn(defaultUser);

        Task actual = taskService.save(taskToSave);

        assertEquals(expected, actual);
        verify(taskRepository, times(1))
                .save(any());
    }

    @Test
    @DisplayName("update test: with partially filled Task data.")
    void givenPartiallyFilledTaskUpdateThenUpdatedTask() {
        Long taskId = 1L;
        User defaultUser = new User(
                2L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        Task partiallyFilledTask = new Task(
                null,
                "name",
                null,
                TaskStatus.TODO,
                null,
                null,
                defaultUser
        );
        Task taskToUpdate = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                BEFORE_MILLENNIUM,
                defaultUser
        );
        Task expected = new Task(
                1L,
                "name",
                "description",
                TaskStatus.TODO,
                BEFORE_MILLENNIUM,
                MILLENNIUM,
                defaultUser
        );


        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(taskToUpdate));
        when(taskRepository.update(expected))
                .thenReturn(expected);
        when(userService.findById(2L))
                .thenReturn(defaultUser);

        Task actual = taskService.update(taskId, partiallyFilledTask);

        assertEquals(expected, actual);
        verify(taskRepository, times(1))
                .update(expected);
        verify(taskRepository, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("update test: with filled Task data.")
    void givenFilledTaskAndTaskIdWhenUpdateThenUpdatedTask() {
        Long taskId = 1L;
        User defaultUser = new User(
                2L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        Task taskToUpdate = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                BEFORE_MILLENNIUM,
                defaultUser
        );
        Task expected = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                MILLENNIUM,
                defaultUser
        );

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(expected));
        when(taskRepository.update(expected))
                .thenReturn(expected);
        when(userService.findById(2L))
                .thenReturn(defaultUser);

        Task actual = taskService.update(taskId, taskToUpdate);

        assertEquals(expected, actual);
        verify(taskRepository, times(1))
                .update(expected);
        verify(taskRepository, times(1))
                .findById(1L);
    }

    @Test
    @DisplayName("update test: try update with not existed task id.")
    void givenTaskAndNotExistedTaskIdWhenUpdateThenUpdatedTask() {
        Long notExistedTaskId = 1L;
        LocalDateTime creationTime = LocalDateTime.of(1999, Month.DECEMBER, 10, 0, 0, 0);
        User defaultUser = new User(
                2L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        Task taskToUpdate = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                creationTime,
                creationTime,
                defaultUser
        );

        when(taskRepository.findById(notExistedTaskId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.update(notExistedTaskId, taskToUpdate),
                "TaskId is incorrect."
        );
        verify(taskRepository, times(0))
                .update(any());
        verify(taskRepository, times(1))
                .findById(any());
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenExistedTaskIdWhenDeleteThenVoid() {
        Long existedTaskId = 1L;
        User defaultUser = new User(
                2L,
                "user",
                "pass",
                "email",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        Task expected = new Task(
                1L,
                "name",
                "description",
                TaskStatus.DONE,
                BEFORE_MILLENNIUM,
                MILLENNIUM,
                defaultUser
        );
        when(taskRepository.findById(existedTaskId))
                .thenReturn(Optional.of(expected));
        taskService.deleteById(existedTaskId);

        verify(taskRepository, times(1))
                .findById(existedTaskId);
        verify(taskRepository, times(1))
                .deleteById(expected);
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenNotExistedTaskIdWhenDeleteThenVoid() {
        Long notExistedTaskId = 1L;

        assertThrows(EntityNotFoundException.class,
                () -> taskService.deleteById(notExistedTaskId),
                "TaskId is incorrect."
        );
        verify(taskRepository, times(1))
                .findById(notExistedTaskId);
        verify(taskRepository, times(0))
                .deleteById(any());
    }
}