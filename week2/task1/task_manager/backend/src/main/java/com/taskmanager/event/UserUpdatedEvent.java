package com.taskmanager.event;

import com.taskmanager.dto.UserDTO;

public class UserUpdatedEvent extends UserEvent {
    public UserUpdatedEvent(UserDTO user) {
        super(user);
    }
} 