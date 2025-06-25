package com.taskmanager.service;

import com.taskmanager.database.DatabaseConnection;
import com.taskmanager.database.DatabaseConnectionFactory;
import com.taskmanager.dto.UserDTO;
import com.taskmanager.event.UserEventPublisher;
import com.taskmanager.exception.UserException;
import com.taskmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private DatabaseConnectionFactory connectionFactory;

    @Mock
    private DatabaseConnection databaseConnection;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() throws SQLException {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setName("Test User");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setPassword("password");

        // Setup common mocks
        when(connectionFactory.createConnection(any())).thenReturn(databaseConnection);
        when(databaseConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void createUser_Success() throws SQLException {
        // Setup mocks for successful user creation
        when(resultSet.next()).thenReturn(false); // Email check returns no results
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true); // For generated keys
        when(resultSet.getLong(1)).thenReturn(1L);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Create a new ResultSet for the email check
        ResultSet emailCheckResultSet = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT id FROM users WHERE email = ?")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(emailCheckResultSet);
        when(emailCheckResultSet.next()).thenReturn(false);

        UserDTO result = userService.createUser(testUserDTO);

        assertNotNull(result);
        assertEquals(testUserDTO.getEmail(), result.getEmail());
        verify(eventPublisher).publishUserCreated(any());
    }

    @Test
    void createUser_DuplicateEmail() throws SQLException {
        // Setup mocks for duplicate email check
        ResultSet emailCheckResultSet = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT id FROM users WHERE email = ?")).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(emailCheckResultSet);
        when(emailCheckResultSet.next()).thenReturn(true);

        assertThrows(UserException.class, () -> userService.createUser(testUserDTO));
        verify(eventPublisher, never()).publishUserCreated(any());
    }

    @Test
    void getUserById_Success() throws SQLException {
        // Setup mocks for successful user retrieval
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("name")).thenReturn("Test User");
        when(resultSet.getString("email")).thenReturn("test@example.com");

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_NotFound() throws SQLException {
        // Setup mocks for user not found
        when(resultSet.next()).thenReturn(false);

        assertThrows(UserException.class, () -> userService.getUserById(1L));
    }
} 