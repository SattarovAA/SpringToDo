package com.emobile.springtodo.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("UserControllerTest tests")
public class UserControllerTest {
    private final static String urlTemplate = "/api/user";
    @Autowired
    JdbcTemplate jdbcTemplate;
    @MockitoBean
    PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisCacheManager cacheManager;

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
    @DisplayName("getById test: get user data by id from anonymous user.")
    void givenAnonymousUserWhenGetByIdUrlThenStatusUnauthorized()
            throws Exception {
        String getByIdUrl = urlTemplate + "/1";

        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get user data by not existed user id.")
    void givenNotExistedUserIdWhenGetByIdUrlThenStatusNotFound()
            throws Exception {
        String getByIdUrl = urlTemplate + "/1";

        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get user data by existed user id.")
    void givenExistedUserIdWhenGetByIdUrlThenUserResponse()
            throws Exception {
        long expectedId = 1L;
        String getByIdUrl = urlTemplate + "/" + expectedId;
        String expectedUsername = "username";
        String expectedPass = "encodedPass";
        String expectedEmail = "email@c.om";
        String expectedRole = "ROLE_ADMIN";
        String sql = """
                INSERT INTO users(id, username, password, email, role)
                VALUES (?,?,?,?,?)
                """;
        jdbcTemplate.update(sql,
                expectedId, expectedUsername, expectedPass,
                expectedEmail, expectedRole
        );

        mockMvc.perform(get(getByIdUrl))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.role").isString())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.username").value(expectedUsername))
                .andExpect(jsonPath("$.password").value(expectedPass))
                .andExpect(jsonPath("$.email").value(expectedEmail))
                .andExpect(jsonPath("$.role").value(expectedRole))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("update test: update user data by id from anonymous user.")
    void givenAnonymousUserWhenUpdateUrlThenStatusUnauthorized()
            throws Exception {
        String updateUrl = urlTemplate + "/1";

        mockMvc.perform(put(updateUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update user data by user id from auth user.")
    void givenInsertJsonWhenUpdateUrlThenUserResponse()
            throws Exception {
        long expectedId = 1L;
        String updateUrl = urlTemplate + "/" + expectedId;
        String oldUsername = "username";
        String oldPass = "password";
        String oldEmail = "email@c.om";
        String oldRole = "ROLE_ADMIN";
        String expectedUsername = "user";
        String expectedPass = "encodedPass";
        String requestUserJson = """
                {
                   "username": "user",
                   "password": "pass",
                   "email" : "email@c.om",
                   "roles": "ROLE_ADMIN"
                }""";
        String sql = """
                INSERT INTO users(id, username, password, email, role)
                VALUES (?,?,?,?,?)
                """;//
        jdbcTemplate.update(sql,
                expectedId, oldUsername, oldPass,
                oldEmail, oldRole
        );

        when(passwordEncoder.encode("pass"))
                .thenReturn(expectedPass);

        mockMvc.perform(put(updateUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUserJson)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.role").isString())
                .andExpect(jsonPath("$.id").value(expectedId))
                .andExpect(jsonPath("$.username").value(expectedUsername))
                .andExpect(jsonPath("$.password").value(expectedPass))
                .andExpect(jsonPath("$.email").value(oldEmail))
                .andExpect(jsonPath("$.role").value(oldRole))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("delete test: delete user data by id from anonymous user.")
    void givenAnonymousUserWhenDeleteUrlThenStatusUnauthorized()
            throws Exception {
        String deleteUrl = urlTemplate + "/1";

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by not existed id.")
    void givenNotExistedUserIdWhenDeleteUrlThenStatusNotFound()
            throws Exception {
        String deleteUrl = urlTemplate + "/1";

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by existed id from auth user.")
    void givenExistedUserIdWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        long expectedId = 1L;
        String deleteUrl = urlTemplate + "/" + expectedId;
        String expectedUsername = "username";
        String expectedPass = "encodedPass";
        String expectedEmail = "email@c.om";
        String expectedRole = "ROLE_ADMIN";
        String sql = """
                INSERT INTO users(id, username, password, email, role)
                VALUES (?,?,?,?,?)
                """;
        jdbcTemplate.update(sql,
                expectedId, expectedUsername, expectedPass,
                expectedEmail, expectedRole
        );
        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNoContent());
    }
}

