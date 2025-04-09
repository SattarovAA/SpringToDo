package com.emobile.springtodo.controller;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.repository.impl.UserRepositoryImpl;
import com.emobile.springtodo.util.SessionUtil;
import com.redis.testcontainers.RedisContainer;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.MediaType;
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

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("UserControllerTest tests")
class UserControllerTest {
    private static final String URL_TEMPLATE = "/api/user";
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);
    @Autowired
    private SessionFactory sessionFactory;
    @MockitoBean
    PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisCacheManager cacheManager;
    private Long existedId;


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

    @BeforeAll
    static void beforeAll() {
        POSTGRE_CONTAINER.start();
        Flyway flyway = Flyway.configure()
                .dataSource(POSTGRE_CONTAINER.getJdbcUrl(), POSTGRE_CONTAINER.getUsername(), POSTGRE_CONTAINER.getPassword())
                .load();
        flyway.migrate();
    }

    @AfterAll
    static void afterAll() {
        POSTGRE_CONTAINER.stop();
        REDIS_CONTAINER.stop();
    }

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("DELETE FROM Task").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();

        Task test1 = new Task();
        Task test2 = new Task();
        User testAuthor = new User(null, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of(test1));
        User testAuthor2 = new User(null, "user2", "pass1",
                "email2@co.m", RoleType.ROLE_USER, List.of(test2));
        test1 = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, testAuthor);
        test2 = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, testAuthor2);

        session.persist(testAuthor);
        session.persist(testAuthor2);
        session.persist(test1);
        session.persist(test2);
        existedId = testAuthor.getId();

        transaction.commit();
        session.close();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("getById test: get user data by id from anonymous user.")
    void givenAnonymousUserWhenGetByIdUrlThenStatusUnauthorized()
            throws Exception {
        String getByIdUrl = URL_TEMPLATE + "/" + existedId;

        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get user data by not existed user id.")
    void givenNotExistedUserIdWhenGetByIdUrlThenStatusNotFound()
            throws Exception {
        String getByIdUrl = URL_TEMPLATE + "/" + (existedId + 2);

        mockMvc.perform(get(getByIdUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser()
    @DisplayName("getById test: get user data by existed user id.")
    void givenExistedUserIdWhenGetByIdUrlThenUserResponse()
            throws Exception {
        String getByIdUrl = URL_TEMPLATE + "/" + existedId;
        String expectedUsername = "user";
        String expectedPass = "pass1";
        String expectedEmail = "email1@co.m";
        String expectedRole = "ROLE_USER";

        mockMvc.perform(get(getByIdUrl))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").isString())
                .andExpect(jsonPath("$.password").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.role").isString())
                .andExpect(jsonPath("$.id").value(existedId))
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
        String updateUrl = URL_TEMPLATE + "/" + existedId;

        mockMvc.perform(put(updateUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("update test: update user data by user id from auth user.")
    void givenInsertJsonWhenUpdateUrlThenUserResponse()
            throws Exception {
        String updateUrl = URL_TEMPLATE + "/" + existedId;
        String oldEmail = "email@co.m";
        String oldRole = "ROLE_USER";
        String expectedUsername = "user";
        String expectedPass = "encodedPass";
        String requestUserJson = """
                {
                   "username": "user",
                   "password": "pass",
                   "email" : "email@co.m",
                   "roles": "ROLE_USER"
                }""";

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
                .andExpect(jsonPath("$.id").value(existedId))
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
        String deleteUrl = URL_TEMPLATE + "/" + existedId;

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by not existed id.")
    void givenNotExistedUserIdWhenDeleteUrlThenStatusNotFound()
            throws Exception {
        String deleteUrl = URL_TEMPLATE + "/" + (existedId + 2);

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser()
    @DisplayName("delete test: delete user data by existed id from auth user.")
    void givenExistedUserIdWhenDeleteUrlThenStatusNoContent()
            throws Exception {
        String deleteUrl = URL_TEMPLATE + "/" + existedId;

        mockMvc.perform(delete(deleteUrl))
                .andExpect(status().isNoContent());
    }
}

