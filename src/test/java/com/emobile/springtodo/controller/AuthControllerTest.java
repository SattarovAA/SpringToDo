package com.emobile.springtodo.controller;

import com.redis.testcontainers.RedisContainer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("AuthControllerTest tests")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

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

    @Test
    @WithAnonymousUser
    @DisplayName("registerUser test: new admin user from anonymous user.")
    void givenUserRequestWhenRegisterUrlThenMessage() throws Exception {
        String url = "/api/auth/register";
        String requestJson = """
                {
                "username": "user",
                "password": "pass",
                "email": "email@c.om",
                "role": "ROLE_ADMIN"
                }""";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                )
                .andExpect(jsonPath("$.message").isString())
                .andExpect(status().isCreated());
    }
}
