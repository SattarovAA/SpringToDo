package com.emobile.springtodo.controller;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("AppControllerTest tests")
public class AppControllerTest {
    private final static String urlTemplate = "/api/test";
    private final static String publicResponseMessage = "Public response data.";
    private final static String adminResponseMessage = "Admin response data.";
    private final static String userResponseMessage = "User response data.";
    private final static String anyResponseMessage = "Authenticated response data.";
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

    @AfterAll
    static void afterAll() {
        POSTGRE_CONTAINER.stop();
        REDIS_CONTAINER.stop();
    }

    @Test
    @WithMockUser(username = "testAdmin", roles = {"ADMIN"})
    @DisplayName("allAccess test with admin user.")
    void givenAdminUser_whenAllAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/all"))
                .andExpect(content().string(publicResponseMessage));
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("allAccess test with simple user.")
    void givenSimpleUser_whenAllAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/all"))
                .andExpect(content().string(publicResponseMessage));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("allAccess test with anonymous user.")
    void givenAnonymousUser_whenAllAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/all"))
                .andExpect(content().string(publicResponseMessage));
    }

    @Test
    @WithMockUser(username = "testAdmin", roles = {"ADMIN"})
    @DisplayName("adminAccess test with admin user.")
    void givenAdminUser_whenAdminAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/admin"))
                .andExpect(content().string(adminResponseMessage));
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("adminAccess test with simple user.")
    void givenSimpleUser_whenAdminAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("adminAccess test with anonymous user.")
    void givenAnonymousUser_whenAdminAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testAdmin", roles = {"ADMIN"})
    @DisplayName("userAccess test with admin user.")
    void givenAdminUser_whenUserAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("anyAccess test with simple user.")
    void givenSimpleUser_whenUserAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/user"))
                .andExpect(content().string(userResponseMessage));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("userAccess test with anonymous user.")
    void givenAnonymousUser_whenUserAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testAdmin", roles = {"ADMIN"})
    @DisplayName("anyAccess test with admin user.")
    void givenAdminUser_whenAnyAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/any"))
                .andExpect(content().string(anyResponseMessage));
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("anyAccess test with simple user.")
    void givenSimpleUser_whenAnyAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/any"))
                .andExpect(content().string(anyResponseMessage));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("anyAccess test with anonymous user.")
    void givenAnonymousUser_whenAnyAccessUrl_thenMessage() throws Exception {
        mockMvc.perform(get(urlTemplate + "/any"))
                .andExpect(status().isForbidden());
    }
}
