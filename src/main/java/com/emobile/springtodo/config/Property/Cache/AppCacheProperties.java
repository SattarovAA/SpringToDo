package com.emobile.springtodo.config.Property.Cache;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {
    private final List<String> cacheNames = new ArrayList<>();
    private final Map<String, CacheProperties> properties = new HashMap<>();

    @Data
    public static class CacheProperties {
        private Duration expiry = Duration.ZERO;
    }

    /**
     * Фактически глобальные переменные.
     * Могут не соответствовать значениям из config-а.
     */
    public interface Type {
        String USERS = "users";
        String USER_BY_ID = "userById";
        String USER_BY_NAME = "userByName";
        String TASKS = "tasks";
        String TASK_BY_ID = "taskById";
    }
}
