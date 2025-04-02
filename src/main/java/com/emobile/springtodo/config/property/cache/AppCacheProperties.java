package com.emobile.springtodo.config.property.cache;

import lombok.Data;
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
     * Actually global variables.
     * May not match values from config.
     */
    public static final class Types {
        public static final String USERS = "users";
        public static final String USER_BY_ID = "userById";
        public static final String USER_BY_NAME = "userByName";
        public static final String TASKS = "tasks";
        public static final String TASK_BY_ID = "taskById";

        private Types() {}
    }
}
