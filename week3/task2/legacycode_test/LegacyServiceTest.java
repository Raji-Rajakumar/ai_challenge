package com.busbooking.service;

import com.busbooking.dto.RegisterRequest;
import com.busbooking.entity.User;
import com.busbooking.repository.UserRepository;
import com.busbooking.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LegacyServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("1234567890");
    }

    @Test
    void register_WithFieldInjection() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        var result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(registerRequest.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken(registerRequest.getEmail());
    }

    @Test
    void register_EmailExists_WithFieldInjection() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    void register_NullRequest_WithFieldInjection() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> authService.register(null));
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    void register_EmptyEmail_WithFieldInjection() {
        // Arrange
        registerRequest.setEmail("");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });
        when(jwtTokenProvider.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        var result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals("", result.getEmail());
        verify(userRepository).existsByEmail("");
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken("");
    }

    @Test
    void register_RepositoryError_WithFieldInjection() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }
} 