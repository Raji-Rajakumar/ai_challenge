package com.taskmanager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingUserEventListener implements UserEventListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingUserEventListener.class);

    @Override
    public void onUserCreated(UserCreatedEvent event) {
        logger.info("User created: {} at {}", 
            event.getUser().getEmail(), 
            event.getTimestamp());
    }

    @Override
    public void onUserUpdated(UserUpdatedEvent event) {
        logger.info("User updated: {} at {}", 
            event.getUser().getEmail(), 
            event.getTimestamp());
    }

    @Override
    public void onUserDeleted(UserDeletedEvent event) {
        logger.info("User deleted: {} at {}", 
            event.getUser().getEmail(), 
            event.getTimestamp());
    }
} 