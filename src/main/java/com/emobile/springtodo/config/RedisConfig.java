package com.emobile.springtodo.config;

import com.emobile.springtodo.config.Property.Cache.AppCacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
public class RedisConfig {
    /**
     * Redis cache configuration.
     *
     * @return configuration with updated cache expires
     * @see AppCacheProperties
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            AppCacheProperties appCacheProperties) {
        Map<String, RedisCacheConfiguration> redisConfiguration = new HashMap<>();
        appCacheProperties.getCacheNames().forEach(cacheName ->
                redisConfiguration.put(cacheName,
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(
                                        appCacheProperties.getProperties()
                                                .get(cacheName)
                                                .getExpiry()
                                )
                )
        );
        return (builder) -> builder
                .withInitialCacheConfigurations(redisConfiguration);
    }
}
