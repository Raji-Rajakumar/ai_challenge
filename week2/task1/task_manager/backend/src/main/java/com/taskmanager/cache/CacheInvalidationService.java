package com.taskmanager.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheInvalidationService {
    private static final Logger logger = LoggerFactory.getLogger(CacheInvalidationService.class);

    private final Set<String> invalidatedKeys;
    private final CacheConfig cacheConfig;
    private final CacheKeyGenerator keyGenerator;

    public CacheInvalidationService(CacheConfig cacheConfig, CacheKeyGenerator keyGenerator) {
        this.invalidatedKeys = ConcurrentHashMap.newKeySet();
        this.cacheConfig = cacheConfig;
        this.keyGenerator = keyGenerator;
    }

    public void invalidateTask(Long taskId) {
        String key = keyGenerator.taskKey(taskId);
        invalidateKey(key);
    }

    public void invalidateUserTasks(Long userId) {
        String key = keyGenerator.userTasksKey(userId);
        invalidateKey(key);
    }

    public void invalidateKey(String key) {
        invalidatedKeys.add(key);
        logger.debug("Invalidated cache key: {}", key);
    }

    public void invalidatePattern(String pattern) {
        invalidatedKeys.add(pattern);
        logger.debug("Invalidated cache pattern: {}", pattern);
    }

    public boolean isInvalidated(String key) {
        return invalidatedKeys.contains(key);
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void clearInvalidatedKeys() {
        invalidatedKeys.clear();
        logger.debug("Cleared invalidated keys cache");
    }
} 