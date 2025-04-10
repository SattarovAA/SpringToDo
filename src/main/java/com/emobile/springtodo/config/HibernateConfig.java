package com.emobile.springtodo.config;

import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {
    @Value("${spring.datasource.driver-class-name}")
    private String dataSourceDriverClassName;
    @Value("${spring.datasource.url}")
    private String dataSourceUrl;
    @Value("${spring.datasource.username}")
    private String dataSourceUsername;
    @Value("${spring.datasource.password}")
    private String dataSourcePassword;

    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration()
                .setProperty("hibernate.connection.driver_class", dataSourceDriverClassName)
                .setProperty("hibernate.connection.url", dataSourceUrl)
                .setProperty("hibernate.connection.username", dataSourceUsername)
                .setProperty("hibernate.connection.password", dataSourcePassword)
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.show_sql", "true")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();
    }



//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
//        return transactionManager;
//    }

//    @Bean
//    public HibernateTransactionManager transactionManager() {
//        HibernateTransactionManager txManager = new HibernateTransactionManager();
//        txManager.setSessionFactory(sessionFactory().getObject());
//        return txManager;
//    }
}
