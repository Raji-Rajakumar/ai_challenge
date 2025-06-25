import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    validateStatus: function (status) {
        return status >= 200 && status < 500; // Accept all status codes less than 500
    }
});

// Add a request interceptor
instance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log('Adding token to request:', config.url);
        } else {
            console.log('No token found for request:', config.url);
        }
        return config;
    },
    (error) => {
        console.error('Request error:', error);
        return Promise.reject(error);
    }
);

// Add a response interceptor
instance.interceptors.response.use(
    (response) => {
        console.log('Response received:', response.config.url, response.status);
        
        // Check if the response is JSON
        const contentType = response.headers['content-type'];
        if (contentType && contentType.includes('application/json')) {
            return response;
        } else {
            console.error('Received non-JSON response:', contentType);
            return Promise.reject(new Error('Invalid response format'));
        }
    },
    (error) => {
        console.error('Response error:', error);
        
        if (error.response) {
            // The request was made and the server responded with a status code
            console.error('Error response:', error.response.data);
            console.error('Error status:', error.response.status);
            console.error('Error headers:', error.response.headers);
            
            // Check if the error response is HTML
            const contentType = error.response.headers['content-type'];
            if (contentType && contentType.includes('text/html')) {
                console.error('Received HTML error response');
                return Promise.reject(new Error('Server error occurred'));
            }
            
            if (error.response.status === 401) {
                console.log('Unauthorized access, clearing token and redirecting to login');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            } else if (error.response.status === 403) {
                console.log('Forbidden access, token might be invalid or expired');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = '/login';
            }
        } else if (error.request) {
            // The request was made but no response was received
            console.error('Error request:', error.request);
            return Promise.reject(new Error('No response from server'));
        } else {
            // Something happened in setting up the request that triggered an Error
            console.error('Error message:', error.message);
        }
        return Promise.reject(error);
    }
);

export default instance; 