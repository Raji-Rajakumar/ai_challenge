package com.taskmanager.event;

import com.taskmanager.dto.TaskDTO;

public class TaskCreatedEvent extends TaskEvent {
    public TaskCreatedEvent(TaskDTO task) {
        super(task);
    }
} 