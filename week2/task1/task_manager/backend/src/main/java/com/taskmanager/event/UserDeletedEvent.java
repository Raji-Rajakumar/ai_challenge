package com.taskmanager.event;

import com.taskmanager.dto.UserDTO;

public class UserDeletedEvent extends UserEvent {
    public UserDeletedEvent(UserDTO user) {
        super(user);
    }
} 