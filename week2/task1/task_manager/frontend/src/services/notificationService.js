import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const notificationService = {
    getNotifications: async (userId) => {
        try {
            const response = await axios.get(`${API_URL}/notifications/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Error fetching notifications:', error);
            throw error;
        }
    },

    markAsRead: async (notificationId) => {
        try {
            await axios.put(`${API_URL}/notifications/${notificationId}/read`);
        } catch (error) {
            console.error('Error marking notification as read:', error);
            throw error;
        }
    },

    markAllAsRead: async (userId) => {
        try {
            await axios.put(`${API_URL}/notifications/user/${userId}/read-all`);
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
            throw error;
        }
    }
}; 