package com.taskmanager.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnection extends AutoCloseable {
    Connection getConnection() throws SQLException;
    void close() throws SQLException;
    boolean isConnected();
    String getDatabaseType();
} 