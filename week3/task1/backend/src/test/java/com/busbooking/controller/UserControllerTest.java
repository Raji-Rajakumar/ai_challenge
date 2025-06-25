package com.busbooking.controller;

import com.busbooking.dto.UserProfileDTO;
import com.busbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserProfileDTO userProfileDTO;

    @BeforeEach
    void setUp() {
        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(1L);
        userProfileDTO.setEmail("test@example.com");
        userProfileDTO.setFullName("Test User");
        userProfileDTO.setPhoneNumber("1234567890");
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userService.getUserProfile(anyLong())).thenReturn(userProfileDTO);

        // Act
        ResponseEntity<UserProfileDTO> response = userController.getUserProfile(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(userProfileDTO.getId(), response.getBody().getId());
        assertEquals(userProfileDTO.getEmail(), response.getBody().getEmail());
        assertEquals(userProfileDTO.getFullName(), response.getBody().getFullName());
        assertEquals(userProfileDTO.getPhoneNumber(), response.getBody().getPhoneNumber());

        verify(userService).getUserProfile(1L);
    }

    @Test
    void getUserProfile_NotFound() {
        // Arrange
        when(userService.getUserProfile(anyLong()))
            .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.getUserProfile(1L));
        verify(userService).getUserProfile(1L);
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        UserProfileDTO updatedProfile = new UserProfileDTO();
        updatedProfile.setId(1L);
        updatedProfile.setEmail("test@example.com");
        updatedProfile.setFullName("Updated User");
        updatedProfile.setPhoneNumber("9876543210");

        when(userService.updateUserProfile(anyLong(), any(UserProfileDTO.class))).thenReturn(updatedProfile);

        // Act
        ResponseEntity<UserProfileDTO> response = userController.updateUserProfile(1L, updatedProfile);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(updatedProfile.getId(), response.getBody().getId());
        assertEquals(updatedProfile.getEmail(), response.getBody().getEmail());
        assertEquals(updatedProfile.getFullName(), response.getBody().getFullName());
        assertEquals(updatedProfile.getPhoneNumber(), response.getBody().getPhoneNumber());

        verify(userService).updateUserProfile(1L, updatedProfile);
    }

    @Test
    void updateUserProfile_NotFound() {
        // Arrange
        when(userService.updateUserProfile(anyLong(), any(UserProfileDTO.class)))
            .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.updateUserProfile(1L, userProfileDTO));
        verify(userService).updateUserProfile(1L, userProfileDTO);
    }
} 