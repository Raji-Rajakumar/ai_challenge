package com.taskmanager.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseConnectionFactoryTest {

    @InjectMocks
    private DatabaseConnectionFactory factory;

    @Test
    void createMySQLConnection() {
        // Setup
        ReflectionTestUtils.setField(factory, "url", "jdbc:mysql://localhost:3306/test");
        ReflectionTestUtils.setField(factory, "username", "test");
        ReflectionTestUtils.setField(factory, "password", "test");

        // Execute
        DatabaseConnection connection = factory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL);

        // Verify
        assertNotNull(connection);
        assertEquals("MySQL", connection.getDatabaseType());
    }

    @Test
    void createConnectionWithInvalidType() {
        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> 
            factory.createConnection(null)
        );
    }
} 