package com.taskmanager.event;

import com.taskmanager.dto.UserDTO;
import java.time.LocalDateTime;

public abstract class UserEvent {
    private final UserDTO user;
    private final LocalDateTime timestamp;

    protected UserEvent(UserDTO user) {
        this.user = user;
        this.timestamp = LocalDateTime.now();
    }

    public UserDTO getUser() {
        return user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
} 