package com.taskmanager.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionFactory {
    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public DatabaseConnection createConnection(DatabaseType type) {
        if (type == null) {
            throw new IllegalArgumentException("Database type cannot be null");
        }

        switch (type) {
            case MYSQL:
                return new MySQLConnection(url, username, password);
            default:
                throw new IllegalArgumentException("Unsupported database type: " + type);
        }
    }

    public enum DatabaseType {
        MYSQL
    }
} 