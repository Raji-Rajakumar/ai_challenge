package com.taskmanager.service;

import com.taskmanager.cache.CacheInvalidationService;
import com.taskmanager.cache.CacheKeyGenerator;
import com.taskmanager.cache.CacheService;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private CacheKeyGenerator keyGenerator;

    @Mock
    private CacheInvalidationService invalidationService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setUser(testUser);

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(testUser.getEmail());
    }

    @Test
    void getAllTasks_ShouldReturnTasksList() {
        // Arrange
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByUser(testUser)).thenReturn(expectedTasks);
        when(keyGenerator.userTasksKey(testUser.getId())).thenReturn("user:1:tasks");

        // Act
        List<Task> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(expectedTasks.size(), result.size());
        assertEquals(expectedTasks.get(0).getTitle(), result.get(0).getTitle());
        verify(cacheService).set(anyString(), any());
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(keyGenerator.taskKey(1L)).thenReturn("task:1");

        // Act
        Task result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(cacheService).set(anyString(), any());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        Task result = taskService.createTask(testTask);

        // Assert
        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
        verify(invalidationService).invalidateUserTasks(testUser.getId());
    }

    @Test
    void updateTask_ShouldUpdateAndReturnTask() {
        // Arrange
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        Task result = taskService.updateTask(1L, updatedTask);

        // Assert
        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        verify(taskRepository).save(any(Task.class));
        verify(invalidationService).invalidateTask(1L);
        verify(invalidationService).invalidateUserTasks(testUser.getId());
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository).delete(testTask);
        verify(invalidationService).invalidateTask(1L);
        verify(invalidationService).invalidateUserTasks(testUser.getId());
    }

    @Test
    void toggleTaskComplete_ShouldToggleCompletionStatus() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        taskService.toggleTaskComplete(1L);

        // Assert
        verify(taskRepository).save(any(Task.class));
        verify(invalidationService).invalidateTask(1L);
        verify(invalidationService).invalidateUserTasks(testUser.getId());
    }
} 