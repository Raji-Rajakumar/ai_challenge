package com.taskmanager.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Optional;

/**
 * Interface defining the contract for cache operations.
 * This interface provides a generic way to interact with any caching implementation.
 */
public interface CacheService {
    
    /**
     * Retrieves a value from the cache.
     *
     * @param key the key to look up
     * @param type the class type of the value to return
     * @param <T> the type of the value
     * @return an Optional containing the value if found, empty otherwise
     */
    <T> Optional<T> get(String key, Class<T> type);
    
    /**
     * Retrieves a value from the cache.
     *
     * @param key the key to look up
     * @param typeReference the TypeReference of the value to return
     * @param <T> the type of the value
     * @return an Optional containing the value if found, empty otherwise
     */
    <T> Optional<T> get(String key, TypeReference<T> typeReference);
    
    /**
     * Stores a value in the cache.
     *
     * @param key the key to store the value under
     * @param value the value to store
     * @param <T> the type of the value
     */
    <T> void set(String key, T value);
    
    /**
     * Removes a value from the cache.
     *
     * @param key the key to remove
     * @return true if the key was found and removed, false otherwise
     */
    void delete(String key);
    
    /**
     * Checks if a key exists in the cache.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    boolean exists(String key);
    
    /**
     * Clears all entries from the cache.
     */
    void clear();
} 