package com.taskmanager.service;

import com.taskmanager.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import java.util.List;

@Service
@Validated
public interface TaskService {
    List<Task> getAllTasks();
    Task getTaskById(Long id);
    Task createTask(@Valid Task task);
    Task updateTask(Long id, @Valid Task task);
    void deleteTask(Long id);
    void toggleTaskComplete(Long id);
} 