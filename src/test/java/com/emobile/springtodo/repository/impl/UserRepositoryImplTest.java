package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.util.SessionUtil;
import jakarta.persistence.EntityManager;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.flywaydb.core.internal.jdbc.RowMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
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
//@SpringBootTest
@Testcontainers
@DisplayName("UserRepositoryImpl Tests")
class UserRepositoryImplTest {
    //    @Autowired
    private UserRepositoryImpl repository;
    //    @Autowired
    private SessionUtil sessionUtil;
    private static SessionFactory sessionFactory;

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
        repository = new UserRepositoryImpl(sessionUtil);

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        session.createQuery("DELETE FROM Task").executeUpdate();
        session.createQuery("DELETE FROM User").executeUpdate();

        transaction.commit();
        session.close();
    }

    @AfterEach
    void after() {
        sessionUtil.closeSession();
    }

    @Test
    @DisplayName("findAll test: get all user data.")
    void givenPageInfoWhenGetAllThenListUser() {
        PageInfo pageInfo = new PageInfo(5, 0);
        User test1 = new User(null, "test1", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        User test2 = new User(null, "test2", "pass2",
                "email2@co.m", RoleType.ROLE_ADMIN, List.of());
        Page<User> expected = new Page<>(List.of(test1, test2));

        Session session = sessionUtil.getSession();
        Transaction transaction = session.beginTransaction();
        sessionUtil.getSession().persist(test1);
        sessionUtil.getSession().persist(test2);
        transaction.commit();

        Page<User> actual = repository.findAll(pageInfo);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findById test: get user data by id.")
    void givenExistingIdWhenGetByIdThenUser() {
        Session session = sessionUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Task testTask = new Task();
        User test1 = new User(null, "test1", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of(testTask));
        testTask = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, test1);

        sessionUtil.getSession().persist(test1);
        sessionUtil.getSession().persist(testTask);
        Long userId = test1.getId();
        transaction.commit();

        Optional<User> actual = repository.findById(userId);

        assertTrue(actual.isPresent());
        assertEquals(test1, actual.get());
        assertEquals(1, actual.get().getTaskList().size());
    }

    @Test
    @DisplayName("findById test: try to get user data by not existing id.")
    void givenNotExistingIdWhenGetByIdThenThrow() {
        Long userId = 1L;

        Optional<User> actual = repository.findById(userId);
        actual.ifPresent(System.out::println);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("findByUsername test: get user data by name.")
    void givenExistingNameWhenGetByIdThenUser() {
        String userUsername = "user";
        Task testTask = new Task();
        User test1 = new User(null, userUsername, "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of(testTask));
        testTask = new Task(null, "name", "des",
                TaskStatus.TODO, BEFORE_MILLENNIUM, MILLENNIUM, test1);

        Session session = sessionUtil.getSession();
        Transaction transaction = session.beginTransaction();
        sessionUtil.getSession().persist(test1);
        sessionUtil.getSession().persist(testTask);
        transaction.commit();

        Optional<User> actual = repository.findByUsername(userUsername);

        assertTrue(actual.isPresent());
        assertEquals(test1, actual.get());
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
        Session session = sessionUtil.getSession();
        User userToSave = new User(null, "user", "pass",
                "email2@co.m", RoleType.ROLE_USER, List.of());

        session.beginTransaction();
        User actual = repository.save(userToSave);
        session.getTransaction().commit();

        User inDb = session.find(User.class, actual.getId());

        assertNotNull(actual);
        assertNotNull(inDb);
        assertEquals(inDb, actual);
        assertSame(inDb, actual);
    }

    @Test
    @DisplayName("update test: send user data to repository.")
    void givenUserWhenUpdateThenUpdatedUser() {
        Session session = sessionUtil.getSession();
        User test1 = new User(null, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());

        session.beginTransaction();
        addToDb(test1);
        Long userId = test1.getId();
        session.getTransaction().commit();

        User expected = new User(userId, "user", "pass",
                "email2@co.m", RoleType.ROLE_USER, List.of());

        User actual = repository.update(expected);

        session.find(User.class, userId);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("delete test: delete user data message to repository.")
    void givenUserIdWhenDeleteThenVoid() {
        Session session = sessionUtil.getSession();
        User test1 = new User(null, "user", "pass1",
                "email1@co.m", RoleType.ROLE_USER, List.of());
        session.beginTransaction();
        session.persist(test1);
        session.getTransaction().commit();

        repository.deleteById(test1);

        User actual = session.find(User.class, test1.getId());
        assertNull(actual);
    }

    private void addToDb(User user) {
        sessionUtil.getSession().persist(user);
    }

    private void addToDb(Task task) {
        sessionUtil.getSession().persist(task);
    }
}
