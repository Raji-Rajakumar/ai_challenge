package com.taskmanager.service;

import com.taskmanager.database.DatabaseConnection;
import com.taskmanager.database.DatabaseConnectionFactory;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.event.UserCreatedEvent;
import com.taskmanager.event.UserDeletedEvent;
import com.taskmanager.event.UserEventPublisher;
import com.taskmanager.event.UserUpdatedEvent;
import com.taskmanager.exception.UserException;
import com.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class UserService {
    private final DatabaseConnectionFactory connectionFactory;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;

    @Autowired
    public UserService(DatabaseConnectionFactory connectionFactory, 
                      PasswordEncoder passwordEncoder,
                      UserEventPublisher eventPublisher) {
        this.connectionFactory = connectionFactory;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users")) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new UserException("Error fetching users", e);
        }
        return users;
    }

    public UserDTO getUserById(Long id) {
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    UserDTO user = new UserDTO();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new UserException("Error fetching user with id: " + id, e);
        }
        throw new UserException("User not found with id: " + id);
    }

    public UserDTO createUser(@Valid UserDTO userDTO) {
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            
            // Check if email exists
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
                checkStmt.setString(1, userDTO.getEmail());
                if (checkStmt.executeQuery().next()) {
                    throw new UserException("Email already exists: " + userDTO.getEmail());
                }
            }

            // Insert new user
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users (name, email, password) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, userDTO.getName());
                insertStmt.setString(2, userDTO.getEmail());
                insertStmt.setString(3, passwordEncoder.encode(userDTO.getPassword()));
                
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new UserException("Failed to create user");
                }

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userDTO.setId(generatedKeys.getLong(1));
                        eventPublisher.publishUserCreated(new UserCreatedEvent(userDTO));
                        return userDTO;
                    } else {
                        throw new UserException("Failed to get generated user id");
                    }
                }
            }
        } catch (SQLException e) {
            throw new UserException("Error creating user", e);
        }
    }

    public UserDTO updateUser(Long id, @Valid UserDTO userDTO) {
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            
            // Check if user exists
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE id = ?")) {
                checkStmt.setLong(1, id);
                if (!checkStmt.executeQuery().next()) {
                    throw new UserException("User not found with id: " + id);
                }
            }

            // Check if new email is already taken
            try (PreparedStatement emailCheckStmt = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ? AND id != ?")) {
                emailCheckStmt.setString(1, userDTO.getEmail());
                emailCheckStmt.setLong(2, id);
                if (emailCheckStmt.executeQuery().next()) {
                    throw new UserException("Email already exists: " + userDTO.getEmail());
                }
            }

            // Update user
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE users SET name = ?, email = ? WHERE id = ?")) {
                updateStmt.setString(1, userDTO.getName());
                updateStmt.setString(2, userDTO.getEmail());
                updateStmt.setLong(3, id);
                
                int affectedRows = updateStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new UserException("Failed to update user");
                }
                
                userDTO.setId(id);
                eventPublisher.publishUserUpdated(new UserUpdatedEvent(userDTO));
                return userDTO;
            }
        } catch (SQLException e) {
            throw new UserException("Error updating user", e);
        }
    }

    public void deleteUser(Long id) {
        UserDTO userToDelete = getUserById(id);
        try (DatabaseConnection dbConnection = connectionFactory.createConnection(DatabaseConnectionFactory.DatabaseType.MYSQL)) {
            Connection conn = dbConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                stmt.setLong(1, id);
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new UserException("User not found with id: " + id);
                }
                eventPublisher.publishUserDeleted(new UserDeletedEvent(userToDelete));
            }
        } catch (SQLException e) {
            throw new UserException("Error deleting user", e);
        }
    }
} 