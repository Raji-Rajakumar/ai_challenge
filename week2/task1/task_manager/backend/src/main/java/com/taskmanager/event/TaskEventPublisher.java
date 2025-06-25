package com.taskmanager.event;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskEventPublisher {
    private final List<TaskEventListener> listeners = new ArrayList<>();

    public void addEventListener(TaskEventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(TaskEventListener listener) {
        listeners.remove(listener);
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        listeners.forEach(listener -> listener.onTaskCreated(event));
    }

    public void publishTaskUpdated(TaskUpdatedEvent event) {
        listeners.forEach(listener -> listener.onTaskUpdated(event));
    }

    public void publishTaskDeleted(TaskDeletedEvent event) {
        listeners.forEach(listener -> listener.onTaskDeleted(event));
    }
} 