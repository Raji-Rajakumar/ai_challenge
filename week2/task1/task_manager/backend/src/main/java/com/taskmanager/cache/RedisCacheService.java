package com.taskmanager.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService implements CacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheConfig cacheConfig;
    private final ObjectMapper objectMapper;
    private final JedisPool jedisPool;
    
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, 
                           CacheConfig cacheConfig,
                           JedisPool jedisPool) {
        this.redisTemplate = redisTemplate;
        this.cacheConfig = cacheConfig;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.jedisPool = jedisPool;
    }
    
    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, type));
        } catch (Exception e) {
            logger.error("Error getting value from cache for key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, typeReference));
        } catch (Exception e) {
            logger.error("Error getting value from cache for key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public <T> void set(String key, T value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.setex(key, cacheConfig.getDefaultTtl(), jsonValue);
            }
        } catch (JsonProcessingException e) {
            logger.error("Error serializing value for key {}: {}", key, e.getMessage());
        } catch (Exception e) {
            logger.error("Error setting value in cache for key {}: {}", key, e.getMessage());
        }
    }
    
    @Override
    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            logger.error("Error deleting key {} from cache: {}", key, e.getMessage());
        }
    }
    
    @Override
    public boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error("Error checking existence of key {} in cache: {}", key, e.getMessage());
            return false;
        }
    }
    
    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushAll();
        } catch (Exception e) {
            logger.error("Error clearing cache: {}", e.getMessage());
        }
    }
} 