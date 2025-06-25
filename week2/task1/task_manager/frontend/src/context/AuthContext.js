import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const userData = localStorage.getItem('user');
        if (token && userData) {
            try {
                const parsedUser = JSON.parse(userData);
                setUser({ ...parsedUser, token });
            } catch (error) {
                console.error('Error parsing user data:', error);
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
        setLoading(false);
    }, []);

    const login = (userData) => {
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
    };

    const register = async (name, email, password) => {
        try {
            console.log('Attempting registration with:', { name, email });
            const response = await api.post('/auth/register', { name, email, password });
            console.log('Registration response:', response.data);
            
            if (response.data && response.data.token) {
                const { token, user: userData } = response.data;
                localStorage.setItem('token', token);
                localStorage.setItem('user', JSON.stringify(userData));
                setUser({ ...userData, token });
                return { success: true };
            } else {
                console.error('Invalid response format:', response.data);
                return { 
                    success: false, 
                    error: 'Invalid response from server' 
                };
            }
        } catch (error) {
            console.error('Registration error:', error);
            if (error.response) {
                // The request was made and the server responded with a status code
                // that falls out of the range of 2xx
                console.error('Error response:', error.response.data);
                return { 
                    success: false, 
                    error: error.response.data.message || 'Registration failed' 
                };
            } else if (error.request) {
                // The request was made but no response was received
                console.error('No response received:', error.request);
                return { 
                    success: false, 
                    error: 'No response from server' 
                };
            } else {
                // Something happened in setting up the request that triggered an Error
                console.error('Error setting up request:', error.message);
                return { 
                    success: false, 
                    error: 'An error occurred during registration' 
                };
            }
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    };

    const value = {
        user,
        login,
        register,
        logout,
        loading
    };

    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}; 