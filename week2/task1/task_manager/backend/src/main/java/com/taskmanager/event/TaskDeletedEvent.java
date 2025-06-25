package com.taskmanager.event;

import com.taskmanager.dto.TaskDTO;

public class TaskDeletedEvent extends TaskEvent {
    public TaskDeletedEvent(TaskDTO task) {
        super(task);
    }
} 