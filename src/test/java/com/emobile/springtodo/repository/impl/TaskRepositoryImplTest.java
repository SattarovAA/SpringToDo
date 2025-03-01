package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DisplayName("TaskRepositoryImplTest Tests")
public class TaskRepositoryImplTest {
    private TaskRepositoryImpl repository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);

    @Container
    static PostgreSQLContainer<?> POSTGRE_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_CONTAINER::getPassword);
    }

    @AfterAll
    static void afterAll() {
        POSTGRE_CONTAINER.stop();
    }

    @BeforeEach
    void setUp() {
        repository = new TaskRepositoryImpl(jdbcTemplate);
        jdbcTemplate.update("TRUNCATE tasks CASCADE");
        jdbcTemplate.update("TRUNCATE users CASCADE");
    }

    @Test
    @DisplayName("findAll test: get all user data.")
    void givenPageInfoWhenGetAllThenListUser() {
        PageInfo pageInfo = new PageInfo(5, 1);
        Task test1 = new Task(2L, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, 3L);
        Task test2 = new Task(4L, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, 2L);
        Page<Task> expected = new Page<>(List.of(test1, test2));
        User testAuthor = new User(3L, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        User testAuthor2 = new User(2L, "user2", "pass1",
                "email2@co.m", RoleType.ROLE_USER, List.of());
        addToDb(testAuthor);
        addToDb(testAuthor2);
        addToDb(test1);
        addToDb(test2);

        Page<Task> actual = repository.findAll(pageInfo);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findById test: get user data by id.")
    void givenExistingIdWhenGetByIdThenUser() {
        Long taskId = 2L;
        Task test1 = new Task(taskId, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, 4L);
        User testAuthor = new User(4L, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        addToDb(testAuthor);
        addToDb(test1);

        Optional<Task> actual = repository.findById(taskId);

        assertTrue(actual.isPresent());
        assertEquals(test1, actual.get());
    }

    @Test
    @DisplayName("findById test: try to get task data by not existing id.")
    void givenNotExistingIdWhenGetByIdThenThrow() {
        Long taskId = 1L;

        Optional<Task> actual = repository.findById(taskId);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("update test: send task data to repository.")
    void givenTaskWhenUpdateThenUpdatedTask() {
        Task test1 = new Task(2L, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, BEFORE_MILLENNIUM, 3L);
        Task expected = new Task(2L, "name2", "des2",
                TaskStatus.DONE, BEFORE_MILLENNIUM, MILLENNIUM, 3L);
        User testAuthor = new User(3L, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        addToDb(testAuthor);
        addToDb(test1);

        repository.update(expected);

        assertTrue(existsInDb(expected));
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenTaskIdWhenDeleteThenVoid() {
        Long taskId = 2L;
        Task test1 = new Task(taskId, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, BEFORE_MILLENNIUM, 3L);
        User testAuthor = new User(3L, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        addToDb(testAuthor);
        addToDb(test1);

        repository.deleteById(taskId);
        assertFalse(existsInDb(test1));
    }

    private void addToDb(User user) {
        String sql = """
                INSERT INTO users(id, username, password, email, role)
                VALUES (?,?,?,?,?)
                """;
        jdbcTemplate.update(sql,
                user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole().name()
        );
    }

    private void addToDb(Task task) {
        String sql = """
                INSERT INTO tasks(id, name, description, status, created_at, updated_at, author_id)
                VALUES (?,?,?,?,?,?,?)
                """;
        jdbcTemplate.update(sql,
                task.getId(), task.getName(), task.getDescription(),
                task.getStatus().name(), task.getCreatedAt(),
                task.getUpdatedAt(), task.getAuthorId()
        );
    }

    private boolean existsInDb(Task task) {
        String sql = """
                SELECT EXISTS (SELECT *
                        FROM tasks
                        WHERE name = ?
                        AND description = ?
                        AND status = ?
                        AND created_at = ?
                        AND updated_at = ?
                        AND author_id = ?)
                        AS result
                """;
        return jdbcTemplate.queryForObject(sql, getExistsMapper(),
                task.getName(), task.getDescription(),
                task.getStatus().name(), task.getCreatedAt(),
                task.getUpdatedAt(), task.getAuthorId()
        );
    }

    private RowMapper<Boolean> getExistsMapper() {
        return (resultSet, rowNum) ->
                resultSet.getString("result")
                        .equals("t");
    }
}
