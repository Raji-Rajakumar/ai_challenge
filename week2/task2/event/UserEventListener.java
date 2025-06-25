package com.taskmanager.event;

public interface UserEventListener {
    void onUserCreated(UserCreatedEvent event);
    void onUserUpdated(UserUpdatedEvent event);
    void onUserDeleted(UserDeletedEvent event);
} 