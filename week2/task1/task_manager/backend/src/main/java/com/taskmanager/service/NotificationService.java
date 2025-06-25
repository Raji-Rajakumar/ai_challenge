package com.taskmanager.service;

import com.taskmanager.database.DatabaseConnection;
import com.taskmanager.database.DatabaseConnectionFactory;
import com.taskmanager.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    private final DatabaseConnectionFactory connectionFactory;

    @Autowired
    public NotificationService(DatabaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void createNotification(Notification notification) {
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO notifications (user_id, message, type, read, created_at) VALUES (?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setLong(1, notification.getUserId());
                stmt.setString(2, notification.getMessage());
                stmt.setString(3, notification.getType());
                stmt.setBoolean(4, notification.isRead());
                stmt.setTimestamp(5, Timestamp.valueOf(notification.getCreatedAt()));
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating notification failed, no rows affected.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        notification.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating notification failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating notification", e);
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        List<Notification> notifications = new ArrayList<>();
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC")) {
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getLong("id"));
                    notification.setUserId(rs.getLong("user_id"));
                    notification.setMessage(rs.getString("message"));
                    notification.setType(rs.getString("type"));
                    notification.setRead(rs.getBoolean("read"));
                    notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notifications", e);
        }
        return notifications;
    }

    public void markNotificationAsRead(Long notificationId) {
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE notifications SET read = true WHERE id = ?")) {
                stmt.setLong(1, notificationId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error marking notification as read", e);
        }
    }

    public void markAllNotificationsAsRead(Long userId) {
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE notifications SET read = true WHERE user_id = ? AND read = false")) {
                stmt.setLong(1, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error marking all notifications as read", e);
        }
    }
} 