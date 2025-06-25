package com.taskmanager.event;

import com.taskmanager.dto.UserDTO;

public class UserCreatedEvent extends UserEvent {
    public UserCreatedEvent(UserDTO user) {
        super(user);
    }
} 