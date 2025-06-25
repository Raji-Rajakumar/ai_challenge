package com.taskmanager.service;

import com.taskmanager.cache.CacheInvalidationService;
import com.taskmanager.cache.CacheKeyGenerator;
import com.taskmanager.cache.CacheService;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taskmanager.event.TaskEventPublisher;
import com.taskmanager.event.TaskCreatedEvent;
import com.taskmanager.dto.TaskDTO;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;
    private final CacheKeyGenerator keyGenerator;
    private final CacheInvalidationService invalidationService;
    private final TaskEventPublisher taskEventPublisher;

    public TaskServiceImpl(TaskRepository taskRepository,
                          UserRepository userRepository,
                          CacheService cacheService,
                          CacheKeyGenerator keyGenerator,
                          CacheInvalidationService invalidationService,
                          TaskEventPublisher taskEventPublisher) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
        this.keyGenerator = keyGenerator;
        this.invalidationService = invalidationService;
        this.taskEventPublisher = taskEventPublisher;
    }

    @Override
    public List<Task> getAllTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser(auth.getName());
        
        String cacheKey = keyGenerator.userTasksKey(user.getId());
        return cacheService.get(cacheKey, new TypeReference<List<Task>>() {})
                .orElseGet(() -> {
                    List<Task> tasks = taskRepository.findByUser(user);
                    cacheService.set(cacheKey, tasks);
                    return tasks;
                });
    }

    @Override
    public Task getTaskById(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser(auth.getName());
        
        String cacheKey = keyGenerator.taskKey(id);
        return cacheService.get(cacheKey, Task.class)
                .orElseGet(() -> {
                    Task task = taskRepository.findByIdAndUser(id, user)
                            .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
                    cacheService.set(cacheKey, task);
                    return task;
                });
    }

    @Override
    public Task createTask(Task task) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser(auth.getName());
        
        task.setUser(user);
        task.setStatus(Task.TaskStatus.Pending);
        task.setCompleted(false);
        
        Task savedTask = taskRepository.save(task);
        
        // Invalidate user's task list cache
        invalidationService.invalidateUserTasks(user.getId());

        // Publish TaskCreatedEvent
        TaskDTO taskDTO = new TaskDTO(savedTask);
        taskEventPublisher.publishTaskCreated(new TaskCreatedEvent(taskDTO));
        
        return savedTask;
    }

    @Override
    public Task updateTask(Long id, Task taskDetails) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser(auth.getName());
        
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setCompleted(taskDetails.isCompleted());
        task.setStatus(taskDetails.getStatus());

        Task updatedTask = taskRepository.save(task);
        
        // Invalidate both task and user's task list cache
        invalidationService.invalidateTask(id);
        invalidationService.invalidateUserTasks(user.getId());
        
        return updatedTask;
    }

    @Override
    public void deleteTask(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser(auth.getName());
        
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        taskRepository.delete(task);
        
        // Invalidate both task and user's task list cache
        invalidationService.invalidateTask(id);
        invalidationService.invalidateUserTasks(user.getId());
    }

    @Override
    public void toggleTaskComplete(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = getCurrentUser(auth.getName());
        
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        task.setCompleted(!task.isCompleted());
        if (task.isCompleted()) {
            task.setStatus(Task.TaskStatus.Completed);
        } else {
            task.setStatus(Task.TaskStatus.Pending);
        }

        taskRepository.save(task);
        
        // Invalidate both task and user's task list cache
        invalidationService.invalidateTask(id);
        invalidationService.invalidateUserTasks(user.getId());
    }

    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
} 