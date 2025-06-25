package com.taskmanager.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Configuration class for cache-related properties.
 * These properties can be configured in application.properties/yml.
 */
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {
    
    private String prefix = "taskmanager:";
    private long defaultTtl = 3600; // 1 hour in seconds
    private boolean enabled = true;
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public long getDefaultTtl() {
        return defaultTtl;
    }
    
    public void setDefaultTtl(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        return poolConfig;
    }
} 