package com.taskmanager.event;

import com.taskmanager.model.Notification;
import com.taskmanager.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationListener implements TaskEventListener {
    private final NotificationService notificationService;

    @Autowired
    public TaskNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void onTaskCreated(TaskCreatedEvent event) {
        Notification notification = new Notification(
            event.getTask().getAssignedTo(),
            "New task created: " + event.getTask().getTitle(),
            "TASK_CREATED"
        );
        notificationService.createNotification(notification);
    }

    @Override
    public void onTaskUpdated(TaskUpdatedEvent event) {
        Notification notification = new Notification(
            event.getTask().getAssignedTo(),
            "Task updated: " + event.getTask().getTitle(),
            "TASK_UPDATED"
        );
        notificationService.createNotification(notification);
    }

    @Override
    public void onTaskDeleted(TaskDeletedEvent event) {
        Notification notification = new Notification(
            event.getTask().getAssignedTo(),
            "Task deleted: " + event.getTask().getTitle(),
            "TASK_DELETED"
        );
        notificationService.createNotification(notification);
    }
} 