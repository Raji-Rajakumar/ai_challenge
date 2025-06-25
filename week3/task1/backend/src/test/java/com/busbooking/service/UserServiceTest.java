package com.busbooking.service;

import com.busbooking.dto.UserProfileDTO;
import com.busbooking.entity.User;
import com.busbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserProfileDTO userProfileDTO;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");

        // Setup user profile DTO
        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setFullName("Updated User");
        userProfileDTO.setPhoneNumber("9876543210");
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Act
        UserProfileDTO response = userService.getUserProfile(1L);

        // Assert
        assertNotNull(response);
        assertEquals(user.getFullName(), response.getFullName());
        assertEquals(user.getPhoneNumber(), response.getPhoneNumber());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserProfile_NotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.getUserProfile(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserProfileDTO response = userService.updateUserProfile(1L, userProfileDTO);

        // Assert
        assertNotNull(response);
        assertEquals(userProfileDTO.getFullName(), response.getFullName());
        assertEquals(userProfileDTO.getPhoneNumber(), response.getPhoneNumber());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserProfile_NotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUserProfile(1L, userProfileDTO));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
} 