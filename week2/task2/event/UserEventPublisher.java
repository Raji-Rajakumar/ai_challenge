package com.taskmanager.event;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserEventPublisher {
    private final List<UserEventListener> listeners = new ArrayList<>();

    public void addEventListener(UserEventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(UserEventListener listener) {
        listeners.remove(listener);
    }

    public void publishUserCreated(UserCreatedEvent event) {
        listeners.forEach(listener -> listener.onUserCreated(event));
    }

    public void publishUserUpdated(UserUpdatedEvent event) {
        listeners.forEach(listener -> listener.onUserUpdated(event));
    }

    public void publishUserDeleted(UserDeletedEvent event) {
        listeners.forEach(listener -> listener.onUserDeleted(event));
    }
} 