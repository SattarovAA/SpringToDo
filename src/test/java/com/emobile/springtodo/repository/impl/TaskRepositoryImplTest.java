package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.util.SessionUtil;
import org.flywaydb.core.Flyway;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DisplayName("TaskRepositoryImplTest Tests")
class TaskRepositoryImplTest {
    private TaskRepositoryImpl repository;
    //    @Autowired
    private SessionUtil sessionUtil;
    private static SessionFactory sessionFactory;
    private static final LocalDateTime MILLENNIUM = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);
    private static final LocalDateTime BEFORE_MILLENNIUM = MILLENNIUM.minusDays(5);

    @Container
    static PostgreSQLContainer<?> postgreContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withReuse(true);

    @BeforeAll
    static void beforeAll() {
        postgreContainer.start();
        Flyway flyway = Flyway.configure()
                .dataSource(postgreContainer.getJdbcUrl(), postgreContainer.getUsername(), postgreContainer.getPassword())
                .load();
        flyway.migrate();

        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.url", postgreContainer.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgreContainer.getUsername())
                .setProperty("hibernate.connection.password", postgreContainer.getPassword())
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Task.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        postgreContainer.stop();
    }

    @BeforeEach
    void setUp() {
        sessionUtil = new SessionUtil(sessionFactory);
        repository = new TaskRepositoryImpl(sessionUtil);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("DELETE FROM Task").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();

        transaction.commit();
        session.close();
    }

    @Test
    @DisplayName("findAll test: get all user data.")
    void givenPageInfoWhenGetAllThenListUser() {
        Session session = sessionUtil.getSession();
        PageInfo pageInfo = new PageInfo(5, 0);
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
        Page<Task> expected = new Page<>(List.of(test1, test2));

        session.beginTransaction();
        session.persist(testAuthor);
        session.persist(testAuthor2);
        session.persist(test1);
        session.persist(test2);
        session.getTransaction().commit();

        Page<Task> actual = repository.findAll(pageInfo);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findById test: get user data by id.")
    void givenExistingIdWhenGetByIdThenUser() {
        Session session = sessionUtil.getSession();
        Task test1 = new Task();
        User testAuthor = new User(null, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of(test1));
        test1 = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, testAuthor);

        session.beginTransaction();
        session.persist(testAuthor);
        session.persist(test1);
        Long taskId = test1.getId();
        session.getTransaction().commit();

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
        Session session = sessionUtil.getSession();
        User testAuthor = new User(null, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        Task test1 = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, BEFORE_MILLENNIUM, testAuthor);

        session.beginTransaction();
        session.persist(testAuthor);
        session.persist(test1);
        Long taskId = test1.getId();
        session.getTransaction().commit();

        Task expected = new Task(taskId, "name2", "des2",
                TaskStatus.DONE, BEFORE_MILLENNIUM, MILLENNIUM, testAuthor);

        Task actual = repository.update(expected);

        session.find(Task.class, taskId);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("delete test: delete task data message to repository.")
    void givenTaskIdWhenDeleteThenVoid() {
        Session session = sessionUtil.getSession();
        User testAuthor = new User(null, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        Task test1 = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, BEFORE_MILLENNIUM, testAuthor);
        session.beginTransaction();
        session.persist(testAuthor);
        session.persist(test1);
        session.getTransaction().commit();

        repository.deleteById(test1);

        Task actual = session.find(Task.class, test1.getId());
        assertNull(actual);
    }
}
