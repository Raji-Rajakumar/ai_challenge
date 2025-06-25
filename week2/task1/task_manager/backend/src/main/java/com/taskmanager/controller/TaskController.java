package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            logger.info("Getting all tasks");
            return ResponseEntity.ok(taskService.getAllTasks());
        } catch (Exception e) {
            logger.error("Error getting tasks: ", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            logger.info("Getting task {}", id);
            return ResponseEntity.ok(taskService.getTaskById(id));
        } catch (Exception e) {
            logger.error("Error getting task {}: ", id, e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            logger.info("Creating new task");
            return ResponseEntity.ok(taskService.createTask(task));
        } catch (Exception e) {
            logger.error("Error creating task: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        try {
            logger.info("Updating task {}", id);
            return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
        } catch (Exception e) {
            logger.error("Error updating task {}: ", id, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            logger.info("Deleting task {}", id);
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting task {}: ", id, e);
            throw e;
        }
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleTaskComplete(@PathVariable Long id) {
        try {
            logger.info("Toggling completion status for task {}", id);
            taskService.toggleTaskComplete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error toggling task {}: ", id, e);
            throw e;
        }
    }
} 