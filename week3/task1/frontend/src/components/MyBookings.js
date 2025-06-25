import React, { useState, useEffect } from 'react';
import {
    Container,
    Paper,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    Alert,
} from '@mui/material';
import axios from 'axios'; // Use axios directly instead of bookingService if bookingService is not defined

function MyBookings() {
    const [bookings, setBookings] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUserBookings = async () => {
            const user = JSON.parse(localStorage.getItem('user'));
            const token = localStorage.getItem('token');

            if (!user || !user.id || !token) {
                setError('User not logged in or user ID not found.');
                setLoading(false);
                return;
            }

            try {
                const response = await axios.get(`http://localhost:8080/api/bookings/user/${user.id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });
                setBookings(response.data);
                setError('');
            } catch (err) {
                console.error('Error fetching user bookings:', err);
                setError('Failed to fetch bookings. Please try again.');
            } finally {
                setLoading(false);
            }
        };

        fetchUserBookings();
    }, []);

    const handleCancel = async (bookingId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.post(`http://localhost:8080/api/bookings/${bookingId}/cancel`, {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            // Re-fetch bookings after cancellation
            const user = JSON.parse(localStorage.getItem('user'));
            const tokenAfterCancel = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/bookings/user/${user.id}`, {
                headers: {
                    Authorization: `Bearer ${tokenAfterCancel}`,
                },
            });
            setBookings(response.data);
            setError('');
        } catch (error) {
            console.error('Error cancelling booking:', error);
            setError('Failed to cancel booking. Please try again.');
        }
    };

    if (loading) {
        return <Container><Typography variant="h6" align="center">Loading bookings...</Typography></Container>;
    }

    return (
        <Container>
            <Typography variant="h4" gutterBottom>
                My Bookings
            </Typography>

            {error && (
                <Alert severity="error" style={{ marginBottom: '1rem' }}>
                    {error}
                </Alert>
            )}

            {bookings.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Booking ID</TableCell>
                                <TableCell>Email</TableCell>
                                <TableCell>Full Name</TableCell>
                                <TableCell>Source</TableCell>
                                <TableCell>Destination</TableCell>
                                <TableCell>Departure</TableCell>
                                <TableCell>Seats</TableCell>
                                <TableCell>Total Amount</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Action</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {bookings.map((booking) => (
                                <TableRow key={booking.id}>
                                    <TableCell>{booking.id}</TableCell>
                                    <TableCell>{booking.user?.email || 'N/A'}</TableCell>
                                    <TableCell>{booking.user?.fullName || 'N/A'}</TableCell>
                                    <TableCell>{booking.schedule?.source || 'N/A'}</TableCell>
                                    <TableCell>{booking.schedule?.destination || 'N/A'}</TableCell>
                                    <TableCell>
                                        {booking.schedule?.departureTime ? new Date(
                                            booking.schedule.departureTime
                                        ).toLocaleString() : 'N/A'}
                                    </TableCell>
                                    <TableCell>{booking.numberOfSeats}</TableCell>
                                    <TableCell>${booking.totalAmount}</TableCell>
                                    <TableCell>{booking.status}</TableCell>
                                    <TableCell>
                                        {booking.status === 'CONFIRMED' && (
                                            <Button
                                                variant="contained"
                                                color="error"
                                                onClick={() => handleCancel(booking.id)}
                                            >
                                                Cancel
                                            </Button>
                                        )}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : ( (!error && !loading) &&
                <Typography variant="h6" align="center">No bookings found for your account.</Typography>
            )}
        </Container>
    );
}

export default MyBookings; 