package com.emobile.springtodo.controller;

import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.AppUserDetails;
import com.emobile.springtodo.model.security.RoleType;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("TaskControllerTest tests")
class TaskControllerTest {
    private static final String URL_TEMPLATE = "/api/task";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisCacheManager cacheManager;
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);

    @Container
    public static final PostgreSQLContainer<?> POSTGRE_CONTAINER =
            new PostgreSQLContainer<>("postgres:latest")
                    .withReuse(true)
                    .withDatabaseName("spring_todo_db");
    @Container
    public static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:latest"))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRE_CONTAINER::getPassword);
        registry.add("spring.data.redis.host",
                REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port",
                () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    @AfterAll
    static void afterAll() {
        POSTGRE_CONTAINER.stop();
        REDIS_CONTAINER.stop();
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("TRUNCATE users CASCADE");
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("getById test: get task data by id from anonymous user.")
    void givenAnonymousUserWhenGetByIdUrlThenStatusUnauthorized()
            throws Exception {
        String getByIdUrl = URL_TEMPLATE + "/1";

        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get task data by not existed user id.")
    void givenNotExistedUserIdWhenGetByIdUrlThenStatusNotFound()
            throws Exception {
        String getByIdUrl = URL_TEMPLATE + "/1";

        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get task data by existed user id.")
    void givenExistedUserIdWhenGetByIdUrlThenUserResponse()
            throws Exception {
        long expectedId = 1L;
        String getByIdUrl = URL_TEMPLATE + "/" + expectedId;
        String expectedName = "name";
        String expectedDescription = "description";
        String expectedStatus = "DONE";
        long expectedAuthorId = 2L;
        String sql = """
                INSERT INTO tasks(id, name, description, status, created_at, updated_at, author_id)
                VALUES (?,?,?,?,?,?,?)
                """;

        setDefaultAuthorUser(expectedAuthorId);
        jdbcTemplate.update(sql,
                expectedId, expectedName, expectedDescription, expectedStatus,
                BEFORE_MILLENNIUM, MILLENNIUM, expectedAuthorId
        );

        mockMvc.perform(get(getByIdUrl))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.updatedAt").isString())
                .andExpect(jsonPath("$.authorId").isNumber())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(expectedName))
                .andExpect(jsonPath("$.description").value(expectedDescription))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.createdAt")
                        .value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(BEFORE_MILLENNIUM)))
                .andExpect(jsonPath("$.updatedAt")
                        .value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(MILLENNIUM)))
                .andExpect(jsonPath("$.authorId").value(expectedAuthorId))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("save test: save task data by id from anonymous user.")
    void givenAnonymousUserWhenSaveUrlThenStatusUnauthorized()
            throws Exception {
        mockMvc.perform(post(URL_TEMPLATE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("save test: save task data by user id from auth user.")
    void givenSaveJsonWhenSaveUrlThenStatusCreated()
            throws Exception {
        long expectedId = 1L;
        String expectedName = "name";
        String expectedDescription = "description";
        String expectedStatus = "DONE";
        long expectedAuthorId = 2L;
        String requestUserJson = """
                {
                  "name": "name",
                  "description": "description",
                  "status": "DONE"
                }""";
        User defaultUser = new User(
                expectedAuthorId,
                "username",
                "pass",
                "email@c.om",
                RoleType.ROLE_USER,
                Collections.emptyList()
        );
        AppUserDetails principal = new AppUserDetails(defaultUser);
        setDefaultAuthorUser(expectedAuthorId);

        mockMvc.perform(post(URL_TEMPLATE)
                        .with(user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUserJson)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.updatedAt").isString())
                .andExpect(jsonPath("$.authorId").isNumber())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(expectedName))
                .andExpect(jsonPath("$.description").value(expectedDescription))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.createdAt")
                        .value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(MILLENNIUM)))
                .andExpect(jsonPath("$.updatedAt")
                        .value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(MILLENNIUM)))
                .andExpect(jsonPath("$.authorId").value(expectedAuthorId))
                .andExpect(status().isCreated());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("update test: update task data by id from anonymous user.")
    void givenAnonymousUserWhenUpdateUrlThenStatusUnauthorized()
            throws Exception {
        String updateUrl = URL_TEMPLATE + "/1";

        mockMvc.perform(put(updateUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update task data by user id from auth user.")
    void givenInsertJsonWhenUpdateUrlThenTaskResponse()
            throws Exception {
        long expectedId = 1L;
        String updateUrl = URL_TEMPLATE + "/" + expectedId;
        String expectedName = "name";
        String expectedDescription = "description";
        String expectedStatus = "DONE";
        long expectedAuthorId = 2L;
        String requestUserJson = """
                {
                  "name": "name",
                  "description": "description",
                  "status": "DONE"
                }""";
        String sql = """
                INSERT INTO tasks(id, name, description, status, created_at, updated_at, author_id)
                VALUES (?,?,?,?,?,?,?)
                """;
        setDefaultAuthorUser(expectedAuthorId);

        jdbcTemplate.update(sql,
                expectedId, expectedName, expectedDescription, expectedStatus,
                BEFORE_MILLENNIUM, MILLENNIUM, expectedAuthorId
        );

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUserJson)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.createdAt").isString())
                .andExpect(jsonPath("$.updatedAt").isString())
                .andExpect(jsonPath("$.authorId").isNumber())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.name").value(expectedName))
                .andExpect(jsonPath("$.description").value(expectedDescription))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.createdAt")
                        .value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(BEFORE_MILLENNIUM)))
                .andExpect(jsonPath("$.updatedAt")
                        .value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(MILLENNIUM)))
                .andExpect(jsonPath("$.authorId").value(expectedAuthorId))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("delete test: delete user data by id from anonymous user.")
    void givenAnonymousUserWhenDeleteUrlThenStatusUnauthorized()
            throws Exception {
        String deleteUrl = URL_TEMPLATE + "/1";

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by not existed id.")
    void givenNotExistedUserIdWhenDeleteUrlThenStatusNotFound()
            throws Exception {
        String deleteUrl = URL_TEMPLATE + "/1";

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by existed id from auth user.")
    void givenExistedUserIdWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        long expectedId = 1L;
        String deleteUrl = URL_TEMPLATE + "/" + expectedId;
        String expectedName = "name";
        String expectedDescription = "description";
        String expectedStatus = "DONE";
        long expectedAuthorId = 2L;
        String sql = """
                INSERT INTO tasks(id, name, description, status, created_at, updated_at, author_id)
                VALUES (?,?,?,?,?,?,?)
                """;

        setDefaultAuthorUser(expectedAuthorId);
        jdbcTemplate.update(sql,
                expectedId, expectedName, expectedDescription, expectedStatus,
                BEFORE_MILLENNIUM, MILLENNIUM, expectedAuthorId
        );
        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNoContent());
    }

    private void setDefaultAuthorUser(long expectedId) {
        String expectedUsername = "username";
        String expectedPass = "pass";
        String expectedEmail = "email@c.om";
        String expectedRole = "ROLE_USER";
        String sql = """
                INSERT INTO users(id, username, password, email, role)
                VALUES (?,?,?,?,?)
                """;
        jdbcTemplate.update(sql,
                expectedId, expectedUsername, expectedPass,
                expectedEmail, expectedRole
        );
    }
}
