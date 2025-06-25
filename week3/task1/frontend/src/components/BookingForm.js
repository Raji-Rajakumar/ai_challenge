import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container,
    Paper,
    TextField,
    Button,
    Typography,
    Grid,
    Alert,
} from '@mui/material';
import axios from 'axios';

function BookingForm() {
    const { scheduleId } = useParams();
    const navigate = useNavigate();
    const [schedule, setSchedule] = useState(null);
    const [numberOfSeats, setNumberOfSeats] = useState(1);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadSchedule = async () => {
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    setError('Authentication token not found. Please log in.');
                    setLoading(false);
                    return;
                }
                const response = await axios.get(`http://localhost:8080/api/schedules/${scheduleId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setSchedule(response.data);
                setError('');
            } catch (err) {
                console.error('Error loading schedule:', err);
                setError('Failed to load schedule details. ' + (err.response?.data?.message || err.message));
            } finally {
                setLoading(false);
            }
        };

        loadSchedule();
    }, [scheduleId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        const user = JSON.parse(localStorage.getItem('user'));
        const token = localStorage.getItem('token');

        if (!user || !user.id || !token) {
            setError('User not logged in or user ID not found. Please log in again.');
            return;
        }

        if (numberOfSeats < 1 || numberOfSeats > schedule.availableSeats) {
            setError('Please enter a valid number of seats.');
            return;
        }

        try {
            await axios.post(
                `http://localhost:8080/api/bookings`,
                {},
                {
                    params: {
                        scheduleId: scheduleId,
                        userId: user.id,
                        numberOfSeats: numberOfSeats,
                    },
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'application/json',
                    },
                }
            );
            navigate('/dashboard/my-bookings');
        } catch (err) {
            console.error('Error creating booking:', err);
            setError('Failed to create booking. ' + (err.response?.data?.message || err.message));
        }
    };

    if (loading) {
        return (
            <Container>
                <Typography>Loading schedule details...</Typography>
            </Container>
        );
    }

    if (error && !schedule) {
        return (
            <Container>
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    if (!schedule) {
        return (
            <Container>
                <Typography>Schedule not found.</Typography>
            </Container>
        );
    }

    return (
        <Container>
            <Typography variant="h4" gutterBottom>
                Book Your Tickets
            </Typography>

            {error && (
                <Alert severity="error" style={{ marginBottom: '1rem' }}>
                    {error}
                </Alert>
            )}

            <Paper style={{ padding: '2rem' }}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Typography variant="h6">Journey Details</Typography>
                        <Typography>
                            Bus: {schedule.bus?.busName || 'N/A'} ({schedule.bus?.busNumber || 'N/A'})
                        </Typography>
                        <Typography>
                            From: {schedule.source} To: {schedule.destination}
                        </Typography>
                        <Typography>
                            Departure: {new Date(schedule.departureTime).toLocaleString()}
                        </Typography>
                        <Typography>
                            Arrival: {new Date(schedule.arrivalTime).toLocaleString()}
                        </Typography>
                        <Typography>
                            Available Seats: {schedule.availableSeats}
                        </Typography>
                        <Typography>
                            Fare per Seat: ${schedule.fare.toFixed(2)}
                        </Typography>
                    </Grid>

                    <Grid item xs={12}>
                        <Typography variant="h6">Booking Details</Typography>
                    </Grid>

                    <Grid item xs={12}>
                        <TextField
                            fullWidth
                            label="Number of Seats"
                            type="number"
                            value={numberOfSeats}
                            onChange={(e) => {
                                const value = parseInt(e.target.value);
                                setNumberOfSeats(isNaN(value) ? '' : value);
                            }}
                            inputProps={{ min: 1, max: schedule.availableSeats }}
                            required
                            error={numberOfSeats < 1 || numberOfSeats > schedule.availableSeats}
                            helperText={numberOfSeats < 1 || numberOfSeats > schedule.availableSeats ? `Must be between 1 and ${schedule.availableSeats}` : ''}
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <Typography variant="h6">
                            Total Amount: $
                            {(numberOfSeats * schedule.fare).toFixed(2)}
                        </Typography>
                    </Grid>

                    <Grid item xs={12}>
                        <Button
                            variant="contained"
                            color="primary"
                            onClick={handleSubmit}
                            disabled={
                                numberOfSeats < 1 ||
                                numberOfSeats > schedule.availableSeats ||
                                !schedule
                            }
                        >
                            Confirm Booking
                        </Button>
                    </Grid>
                </Grid>
            </Paper>
        </Container>
    );
}

export default BookingForm; 