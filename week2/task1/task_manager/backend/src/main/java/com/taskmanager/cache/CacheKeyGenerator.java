package com.taskmanager.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating consistent cache keys.
 * This ensures that cache keys are properly formatted and namespaced.
 */
@Component
public class CacheKeyGenerator {
    
    private final CacheConfig cacheConfig;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CacheKeyGenerator(CacheConfig cacheConfig, RedisTemplate<String, Object> redisTemplate) {
        this.cacheConfig = cacheConfig;
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Generates a cache key for a task.
     *
     * @param taskId the ID of the task
     * @return the formatted cache key
     */
    public String taskKey(Long taskId) {
        return formatKey("task", taskId.toString());
    }
    
    /**
     * Generates a cache key for a user's tasks.
     *
     * @param userId the ID of the user
     * @return the formatted cache key
     */
    public String userTasksKey(Long userId) {
        return formatKey("user", userId.toString(), "tasks");
    }
    
    /**
     * Formats a cache key with the configured prefix and given segments.
     *
     * @param segments the key segments to join
     * @return the formatted cache key
     */
    private String formatKey(String... segments) {
        return cacheConfig.getPrefix() + String.join(":", segments);
    }
} 