package com.busbooking.controller;

import com.busbooking.dto.AuthResponse;
import com.busbooking.dto.LoginRequest;
import com.busbooking.dto.RegisterRequest;
import com.busbooking.service.AuthService;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private Validator validator;

    @BeforeEach
    void setUp() {
        // Setup validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhoneNumber("1234567890");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Setup auth response
        authResponse = new AuthResponse("jwt-token", "test@example.com", "Test User", 1L);
    }

    @Test
    void register_Success() {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(authResponse.getToken(), response.getBody().getToken());
        assertEquals(authResponse.getEmail(), response.getBody().getEmail());
        assertEquals(authResponse.getFullName(), response.getBody().getFullName());
        assertEquals(authResponse.getUserId(), response.getBody().getUserId());

        verify(authService).register(registerRequest);
    }

    @Test
    void register_EmailAlreadyExists() {
        // Arrange
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Email already registered"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authController.register(registerRequest));
        verify(authService).register(registerRequest);
    }

    @Test
    void register_InvalidEmail() {
        // Arrange
        registerRequest.setEmail("invalid-email");

        // Act & Assert
        var violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void register_InvalidPhoneNumber() {
        // Arrange
        registerRequest.setPhoneNumber("123"); // Invalid phone number format

        // Act & Assert
        var violations = validator.validate(registerRequest);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void login_Success() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(authResponse.getToken(), response.getBody().getToken());
        assertEquals(authResponse.getEmail(), response.getBody().getEmail());
        assertEquals(authResponse.getFullName(), response.getBody().getFullName());
        assertEquals(authResponse.getUserId(), response.getBody().getUserId());

        verify(authService).login(loginRequest);
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authController.login(loginRequest));
        verify(authService).login(loginRequest);
    }
} 