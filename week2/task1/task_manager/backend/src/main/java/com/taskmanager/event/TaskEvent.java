package com.taskmanager.event;

import com.taskmanager.dto.TaskDTO;
import java.time.LocalDateTime;

public abstract class TaskEvent {
    private final TaskDTO task;
    private final LocalDateTime timestamp;

    protected TaskEvent(TaskDTO task) {
        this.task = task;
        this.timestamp = LocalDateTime.now();
    }

    public TaskDTO getTask() {
        return task;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
} 