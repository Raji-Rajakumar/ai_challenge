import React, { useState, useEffect } from 'react';
import { notificationService } from '../services/notificationService';
import { useAuth } from '../context/AuthContext';
import { format } from 'date-fns';
import {
    Box,
    List,
    ListItem,
    ListItemText,
    Typography,
    IconButton,
    Badge,
    Paper,
    Divider,
    Collapse,
    Button,
    Tooltip,
    Fade
} from '@mui/material';
import {
    Notifications as NotificationsIcon,
    Close as CloseIcon,
    CheckCircle as CheckCircleIcon,
    Add as AddIcon,
    Edit as EditIcon,
    Delete as DeleteIcon,
    DoneAll as DoneAllIcon
} from '@mui/icons-material';

const NotificationPanel = () => {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [isOpen, setIsOpen] = useState(false);
    const { user } = useAuth();

    const fetchNotifications = async () => {
        try {
            const data = await notificationService.getNotifications(user.id);
            setNotifications(data);
            setUnreadCount(data.filter(n => !n.read).length);
        } catch (error) {
            console.error('Error fetching notifications:', error);
        }
    };

    useEffect(() => {
        if (user) {
            fetchNotifications();
            // Poll for new notifications every 30 seconds
            const interval = setInterval(fetchNotifications, 30000);
            return () => clearInterval(interval);
        }
    }, [user]);

    const handleMarkAsRead = async (notificationId) => {
        try {
            await notificationService.markAsRead(notificationId);
            setNotifications(notifications.map(notification =>
                notification.id === notificationId
                    ? { ...notification, read: true }
                    : notification
            ));
            setUnreadCount(prev => Math.max(0, prev - 1));
        } catch (error) {
            console.error('Error marking notification as read:', error);
        }
    };

    const handleMarkAllAsRead = async () => {
        try {
            const unreadNotifications = notifications.filter(n => !n.read);
            await Promise.all(unreadNotifications.map(n => notificationService.markAsRead(n.id)));
            setNotifications(notifications.map(n => ({ ...n, read: true })));
            setUnreadCount(0);
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
        }
    };

    const getNotificationIcon = (type) => {
        switch (type) {
            case 'TASK_CREATED':
                return <AddIcon color="success" />;
            case 'TASK_UPDATED':
                return <EditIcon color="info" />;
            case 'TASK_DELETED':
                return <DeleteIcon color="error" />;
            default:
                return <NotificationsIcon />;
        }
    };

    const getNotificationColor = (type) => {
        switch (type) {
            case 'TASK_CREATED':
                return 'success.main';
            case 'TASK_UPDATED':
                return 'info.main';
            case 'TASK_DELETED':
                return 'error.main';
            default:
                return 'text.primary';
        }
    };

    return (
        <>
            <Tooltip title="Notifications">
                <IconButton
                    color="inherit"
                    onClick={() => setIsOpen(!isOpen)}
                    sx={{ position: 'relative' }}
                >
                    <Badge
                        badgeContent={unreadCount}
                        color="error"
                        sx={{
                            '& .MuiBadge-badge': {
                                right: -3,
                                top: 3,
                            }
                        }}
                    >
                        <NotificationsIcon />
                    </Badge>
                </IconButton>
            </Tooltip>

            <Fade in={isOpen}>
                <Paper
                    elevation={3}
                    sx={{
                        width: 350,
                        maxHeight: 500,
                        overflow: 'auto',
                        position: 'fixed',
                        right: 20,
                        top: 100,
                        zIndex: 1000,
                        borderRadius: 2,
                        boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
                    }}
                >
                    <Box sx={{ p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                        <Typography variant="h6" component="div" sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            Notifications
                            {unreadCount > 0 && (
                                <Badge
                                    badgeContent={unreadCount}
                                    color="error"
                                    sx={{ ml: 1 }}
                                >
                                    <NotificationsIcon />
                                </Badge>
                            )}
                        </Typography>
                        <Box>
                            {unreadCount > 0 && (
                                <Tooltip title="Mark all as read">
                                    <IconButton
                                        size="small"
                                        onClick={handleMarkAllAsRead}
                                        sx={{ mr: 1 }}
                                    >
                                        <DoneAllIcon />
                                    </IconButton>
                                </Tooltip>
                            )}
                            <IconButton
                                size="small"
                                onClick={() => setIsOpen(false)}
                            >
                                <CloseIcon />
                            </IconButton>
                        </Box>
                    </Box>
                    <Divider />
                    <List sx={{ p: 0 }}>
                        {notifications.length === 0 ? (
                            <ListItem>
                                <ListItemText
                                    primary="No notifications"
                                    sx={{ textAlign: 'center', color: 'text.secondary' }}
                                />
                            </ListItem>
                        ) : (
                            notifications.map((notification) => (
                                <ListItem
                                    key={notification.id}
                                    sx={{
                                        bgcolor: notification.read ? 'transparent' : 'action.hover',
                                        borderLeft: 4,
                                        borderColor: getNotificationColor(notification.type),
                                        transition: 'background-color 0.2s',
                                        '&:hover': {
                                            bgcolor: 'action.selected'
                                        }
                                    }}
                                    secondaryAction={
                                        !notification.read && (
                                            <Tooltip title="Mark as read">
                                                <IconButton
                                                    edge="end"
                                                    onClick={() => handleMarkAsRead(notification.id)}
                                                    size="small"
                                                >
                                                    <CheckCircleIcon fontSize="small" />
                                                </IconButton>
                                            </Tooltip>
                                        )
                                    }
                                >
                                    <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 1, width: '100%' }}>
                                        <Box sx={{ mt: 0.5 }}>
                                            {getNotificationIcon(notification.type)}
                                        </Box>
                                        <ListItemText
                                            primary={notification.message}
                                            secondary={format(new Date(notification.createdAt), 'MMM d, yyyy HH:mm')}
                                            primaryTypographyProps={{
                                                sx: {
                                                    fontWeight: notification.read ? 'normal' : 'bold',
                                                    color: notification.read ? 'text.primary' : 'text.primary'
                                                }
                                            }}
                                            secondaryTypographyProps={{
                                                sx: { fontSize: '0.75rem' }
                                            }}
                                        />
                                    </Box>
                                </ListItem>
                            ))
                        )}
                    </List>
                </Paper>
            </Fade>
        </>
    );
};

export default NotificationPanel; 