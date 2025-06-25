import React, { useState, useEffect } from 'react';
import {
    Container,
    Paper,
    TextField,
    Button,
    Typography,
    Grid,
    Alert,
    Box,
    Divider,
} from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function UserProfile() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editMode, setEditMode] = useState(false);
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        phoneNumber: '',
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
    });
    const navigate = useNavigate();

    useEffect(() => {
        fetchUserProfile();
    }, []);

    const fetchUserProfile = async () => {
        try {
            const token = localStorage.getItem('token');
            const storedUser = JSON.parse(localStorage.getItem('user'));
            
            if (!token || !storedUser) {
                setError('User not logged in');
                setLoading(false);
                navigate('/login');
                return;
            }

            const response = await axios.get(`http://localhost:8080/api/users/${storedUser.id}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.data) {
                setUser(response.data);
                setFormData({
                    fullName: response.data.fullName || '',
                    email: response.data.email || '',
                    phoneNumber: response.data.phoneNumber || '',
                    currentPassword: '',
                    newPassword: '',
                    confirmPassword: '',
                });
                setError('');
            } else {
                setError('Failed to load user data');
            }
        } catch (err) {
            console.error('Error fetching user profile:', err);
            setError(err.response?.data?.message || 'Failed to load user profile');
            if (err.response?.status === 401) {
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setError('');

        if (!user || !user.id) {
            setError('User data not available. Please try refreshing the page.');
            return;
        }

        // Validate passwords if changing password
        if (formData.newPassword) {
            if (formData.newPassword !== formData.confirmPassword) {
                setError('New passwords do not match');
                return;
            }
            if (!formData.currentPassword) {
                setError('Current password is required to set new password');
                return;
            }
        }

        try {
            const token = localStorage.getItem('token');
            if (!token) {
                setError('Authentication token not found. Please log in again.');
                navigate('/login');
                return;
            }

            const response = await axios.put(
                `http://localhost:8080/api/users/${user.id}`,
                {
                    fullName: formData.fullName,
                    email: formData.email,
                    phoneNumber: formData.phoneNumber,
                    currentPassword: formData.currentPassword,
                    newPassword: formData.newPassword,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (response.data) {
                setUser(response.data);
                setEditMode(false);
                setFormData(prev => ({
                    ...prev,
                    currentPassword: '',
                    newPassword: '',
                    confirmPassword: '',
                }));
                setError('');
                // Update the user in localStorage
                localStorage.setItem('user', JSON.stringify(response.data));
            } else {
                setError('Failed to update profile');
            }
        } catch (err) {
            console.error('Error updating profile:', err);
            setError(err.response?.data?.message || 'Failed to update profile');
            if (err.response?.status === 401) {
                navigate('/login');
            }
        }
    };

    if (loading) {
        return (
            <Container>
                <Typography>Loading profile...</Typography>
            </Container>
        );
    }

    if (!user) {
        return (
            <Container>
                <Alert severity="error">
                    {error || 'Failed to load user profile. Please try again.'}
                </Alert>
            </Container>
        );
    }

    return (
        <Container maxWidth="md">
            <Typography variant="h4" gutterBottom>
                My Profile
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Paper sx={{ p: 3 }}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Box display="flex" justifyContent="space-between" alignItems="center">
                            <Typography variant="h6">Personal Information</Typography>
                            <Button
                                variant={editMode ? "outlined" : "contained"}
                                onClick={() => setEditMode(!editMode)}
                            >
                                {editMode ? "Cancel" : "Edit Profile"}
                            </Button>
                        </Box>
                        <Divider sx={{ my: 2 }} />
                    </Grid>

                    <Grid item xs={12}>
                        <form onSubmit={handleUpdateProfile}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Full Name"
                                        name="fullName"
                                        value={formData.fullName}
                                        onChange={handleInputChange}
                                        disabled={!editMode}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Email"
                                        name="email"
                                        type="email"
                                        value={formData.email}
                                        onChange={handleInputChange}
                                        disabled={!editMode}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        label="Phone Number"
                                        name="phoneNumber"
                                        value={formData.phoneNumber}
                                        onChange={handleInputChange}
                                        disabled={!editMode}
                                    />
                                </Grid>

                                {editMode && (
                                    <>
                                        <Grid item xs={12}>
                                            <Typography variant="h6" sx={{ mt: 2 }}>
                                                Change Password
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Leave blank if you don't want to change your password
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                label="Current Password"
                                                name="currentPassword"
                                                type="password"
                                                value={formData.currentPassword}
                                                onChange={handleInputChange}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                label="New Password"
                                                name="newPassword"
                                                type="password"
                                                value={formData.newPassword}
                                                onChange={handleInputChange}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                label="Confirm New Password"
                                                name="confirmPassword"
                                                type="password"
                                                value={formData.confirmPassword}
                                                onChange={handleInputChange}
                                            />
                                        </Grid>
                                    </>
                                )}

                                {editMode && (
                                    <Grid item xs={12}>
                                        <Button
                                            type="submit"
                                            variant="contained"
                                            color="primary"
                                            fullWidth
                                        >
                                            Save Changes
                                        </Button>
                                    </Grid>
                                )}
                            </Grid>
                        </form>
                    </Grid>
                </Grid>
            </Paper>
        </Container>
    );
}

export default UserProfile; 