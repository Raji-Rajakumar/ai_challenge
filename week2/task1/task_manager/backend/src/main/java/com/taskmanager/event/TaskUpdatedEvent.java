package com.taskmanager.event;

import com.taskmanager.dto.TaskDTO;

public class TaskUpdatedEvent extends TaskEvent {
    public TaskUpdatedEvent(TaskDTO task) {
        super(task);
    }
} 