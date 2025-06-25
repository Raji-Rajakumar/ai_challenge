import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add a request interceptor to include JWT token if available
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const busService = {
    getAllBuses: () => api.get('/buses'),
    getBusById: (id) => api.get(`/buses/${id}`),
    createBus: (bus) => api.post('/buses', bus),
    updateBus: (id, bus) => api.put(`/buses/${id}`, bus),
    deleteBus: (id) => api.delete(`/buses/${id}`),
};

export const scheduleService = {
    getAllSchedules: () => api.get('/schedules'),
    getScheduleById: (id) => api.get(`/schedules/${id}`),
    searchSchedules: (source, destination) => 
        api.get(`/schedules/search?source=${source}&destination=${destination}`),
    createSchedule: (schedule) => api.post('/schedules', schedule),
    updateSchedule: (id, schedule) => api.put(`/schedules/${id}`, schedule),
    deleteSchedule: (id) => api.delete(`/schedules/${id}`),
};

export const bookingService = {
    getAllBookings: () => api.get('/bookings'),
    getBookingById: (id) => api.get(`/bookings/${id}`),
    getBookingsByEmail: (email) => api.get(`/bookings/email/${email}`),
    createBooking: (booking) => api.post('/bookings', booking),
    cancelBooking: (id) => api.post(`/bookings/${id}/cancel`),
}; 