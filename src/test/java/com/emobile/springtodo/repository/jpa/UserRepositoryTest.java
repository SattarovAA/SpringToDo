package com.emobile.springtodo.repository.jpa;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@DisplayName("UserRepository Tests")
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);
    @Container
    static PostgreSQLContainer<?> postgreContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreContainer::getUsername);
        registry.add("spring.datasource.password", postgreContainer::getPassword);
    }

    @AfterAll
    static void afterAll() {
        postgreContainer.stop();
    }

    @Test
    @DisplayName("findAll test: get all user data.")
    void givenPageInfoWhenGetAllThenListUser() {
        Pageable pageInfo = PageRequest.of(0, 5);
        User test1 = new User(2L, "test1", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        User test2 = new User(3L, "test2", "pass2",
                "email2@co.m", RoleType.ROLE_ADMIN, List.of());
        Page<User> expected = new PageImpl<>(List.of(test1, test2), pageInfo, 2);
        addToDb(test1);
        addToDb(test2);

        Page<User> actual = repository.findAll(pageInfo);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findById test: get user data by id.")
    void givenExistingIdWhenGetByIdThenUser() {
        Long userId = 2L;
        User test1 = new User(userId, "test1", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        Task testTask = new Task(2L, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, test1);
        User expected = new User(userId, "test1", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of(testTask));
        addToDb(test1);
        addToDb(testTask);

        Optional<User> actual = repository.findById(userId);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("findById test: try to get user data by not existing id.")
    void givenNotExistingIdWhenGetByIdThenThrow() {
        Long userId = 1L;

        Optional<User> actual = repository.findById(userId);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("findByUsername test: get user data by name.")
    void givenExistingNameWhenGetByIdThenUser() {
        String userUsername = "user";
        User test1 = new User(2L, userUsername, "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        Task testTask = new Task(2L, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, test1);
        User expected = new User(2L, userUsername, "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of(testTask));
        addToDb(test1);
        addToDb(testTask);

        Optional<User> actual = repository.findByUsername(userUsername);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("findByUsername test: get user data by name.")
    void givenNotExistingNameWhenGetByIdThenUser() {
        String notExistName = "notExistName";

        Optional<User> actual = repository.findByUsername(notExistName);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("save test: send user data to repository.")
    void givenUserWhenSendUserToDbThenSavedUser() {
        User userToSave = new User(null, "user", "pass",
                "email2@co.m", RoleType.ROLE_USER, List.of());

        repository.save(userToSave);
        entityManager.flush();

        assertTrue(existsInDb(userToSave));
    }

    @Test
    @DisplayName("update test: send user data to repository.")
    void givenUserWhenUpdateThenUpdatedUser() {
        User test1 = new User(2L, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        User expected = new User(2L, "user", "pass",
                "email2@co.m", RoleType.ROLE_USER, List.of());
        addToDb(test1);

        repository.save(expected);
        entityManager.flush();

        assertTrue(existsInDb(expected));
    }

    @Test
    @DisplayName("delete test: delete user data message to repository.")
    void givenUserIdWhenDeleteThenVoid() {
        Long userId = 1L;
        User test1 = new User(userId, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        addToDb(test1);

        repository.deleteById(userId);
        entityManager.flush();

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

    private boolean existsInDb(User user) {
        String sql = """
                SELECT EXISTS (SELECT *
                        FROM users
                        WHERE username = ?
                        AND password = ?
                        AND email = ?
                        AND role = ?)
                        AS result
                """;
        return jdbcTemplate.queryForObject(sql, getExistsMapper(),
                user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole().name()
        );
    }

    private RowMapper<Boolean> getExistsMapper() {
        return (resultSet, rowNum) ->
                resultSet.getString("result")
                        .equals("t");
    }
}
