package com.emobile.springtodo;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

class SpringToDoApplicationTests {
    @TestConfiguration
    static class CustomClockConfiguration {
        private static final LocalDateTime MILLENNIUM =
                LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);

        @Bean
        @Primary
        public Clock fixedClock() {
            return Clock.fixed(MILLENNIUM.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        }
    }

//    @TestConfiguration
//    static class CustomHibernateConfiguration {
//        private final String dataSourceDriverClassName = "org.postgresql.Driver";
//        private final String dataSourceUrl = "jdbc:postgresql://localhost:5432/spring_todo_db";
//        private final String dataSourceUsername = "postgres";
//        private final String dataSourcePassword = "1322";
////        @Bean
////        @Primary
////        public SessionUtil sessionUtil() {
////            return new SessionUtil(sessionFactory());
////        }
//
//        @Bean
//        @Primary
//        public SessionFactory sessionFactory() {
//            return new org.hibernate.cfg.Configuration()
//                    .setProperty("hibernate.connection.driver_class", dataSourceDriverClassName)
//                    .setProperty("hibernate.connection.url", dataSourceUrl)
//                    .setProperty("hibernate.connection.username", dataSourceUsername)
//                    .setProperty("hibernate.connection.password", dataSourcePassword)
//                    .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
//                    .setProperty("hibernate.show_sql", "true")
//                    .addAnnotatedClass(User.class)
//                    .addAnnotatedClass(Task.class)
//                    .buildSessionFactory();
//        }
//    }
}
