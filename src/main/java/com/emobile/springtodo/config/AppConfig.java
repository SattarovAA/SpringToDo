package com.emobile.springtodo.config;

import com.emobile.springtodo.config.property.cache.AppCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.Clock;

/**
 * Default application configuration.
 */
@Configuration
@ComponentScan("com.emobile")
@EnableWebMvc
@EnableCaching
@EnableConfigurationProperties(AppCacheProperties.class)
@PropertySource("classpath:config/application.yml")
public class AppConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
