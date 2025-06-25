package com.taskmanager.dto;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import java.time.LocalDateTime;

public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime dueDate;
    private Long userId;
    private String userEmail;
    private UserDTO assignee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean completed;
    private Long assignedTo;

    // Default constructor
    public TaskDTO() {
    }

    // Constructor with all fields
    public TaskDTO(Long id, String title, String description, String status, 
                  LocalDateTime dueDate, Long userId, String userEmail,
                  UserDTO assignee, LocalDateTime createdAt, LocalDateTime updatedAt, boolean completed, Long assignedTo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.userId = userId;
        this.userEmail = userEmail;
        this.assignee = assignee;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completed = completed;
        this.assignedTo = assignedTo;
    }

    // New constructor to convert Task entity to TaskDTO
    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name(); // Convert enum to String
        this.dueDate = task.getDueDate() != null ? task.getDueDate().atStartOfDay() : null; // Convert LocalDate to LocalDateTime
        this.completed = task.isCompleted();
        this.userId = task.getUser() != null ? task.getUser().getId() : null;
        this.assignedTo = task.getUser() != null ? task.getUser().getId() : null;
        this.userEmail = task.getUser() != null ? task.getUser().getEmail() : null;
        // Assuming createdAt and updatedAt might be handled by entity listeners or not strictly needed for the event
        this.createdAt = LocalDateTime.now(); // Set to current time or retrieve if available from Task
        this.updatedAt = LocalDateTime.now(); // Set to current time or retrieve if available from Task
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserDTO getAssignee() {
        return assignee;
    }

    public void setAssignee(UserDTO assignee) {
        this.assignee = assignee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }
} 