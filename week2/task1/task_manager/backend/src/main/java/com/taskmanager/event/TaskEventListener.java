package com.taskmanager.event;

public interface TaskEventListener {
    void onTaskCreated(TaskCreatedEvent event);
    void onTaskUpdated(TaskUpdatedEvent event);
    void onTaskDeleted(TaskDeletedEvent event);
} 