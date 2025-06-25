import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Paper,
    TextField,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
} from '@mui/material';
import { scheduleService } from '../services/api';

function ScheduleList() {
    const [schedules, setSchedules] = useState([]);
    const [searchParams, setSearchParams] = useState({
        source: '',
        destination: '',
    });
    const navigate = useNavigate();

    useEffect(() => {
        loadSchedules();
    }, []);

    const loadSchedules = async () => {
        try {
            const response = await scheduleService.getAllSchedules();
            setSchedules(response.data);
        } catch (error) {
            console.error('Error loading schedules:', error);
        }
    };

    const handleSearch = async () => {
        try {
            const response = await scheduleService.searchSchedules(
                searchParams.source,
                searchParams.destination
            );
            setSchedules(response.data);
        } catch (error) {
            console.error('Error searching schedules:', error);
        }
    };

    const handleBookNow = (scheduleId) => {
        navigate(`/book/${scheduleId}`);
    };

    return (
        <Container>
            <Typography variant="h4" gutterBottom>
                Bus Schedules
            </Typography>

            <Paper style={{ padding: '1rem', marginBottom: '2rem' }}>
                <div style={{ display: 'flex', gap: '1rem', marginBottom: '1rem' }}>
                    <TextField
                        label="From"
                        value={searchParams.source}
                        onChange={(e) =>
                            setSearchParams({ ...searchParams, source: e.target.value })
                        }
                    />
                    <TextField
                        label="To"
                        value={searchParams.destination}
                        onChange={(e) =>
                            setSearchParams({ ...searchParams, destination: e.target.value })
                        }
                    />
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSearch}
                    >
                        Search
                    </Button>
                </div>
            </Paper>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Bus Name</TableCell>
                            <TableCell>Bus Type</TableCell>
                            <TableCell>From</TableCell>
                            <TableCell>To</TableCell>
                            <TableCell>Departure</TableCell>
                            <TableCell>Arrival</TableCell>
                            <TableCell>Available Seats</TableCell>
                            <TableCell>Fare</TableCell>
                            <TableCell>Action</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {schedules.map((schedule) => (
                            <TableRow key={schedule.id}>
                                <TableCell>{schedule.bus.busName}</TableCell>
                                <TableCell>{schedule.bus.busType}</TableCell>
                                <TableCell>{schedule.source}</TableCell>
                                <TableCell>{schedule.destination}</TableCell>
                                <TableCell>
                                    {new Date(schedule.departureTime).toLocaleString()}
                                </TableCell>
                                <TableCell>
                                    {new Date(schedule.arrivalTime).toLocaleString()}
                                </TableCell>
                                <TableCell>{schedule.availableSeats}</TableCell>
                                <TableCell>${schedule.bus.farePerSeat}</TableCell>
                                <TableCell>
                                    <Button
                                        variant="contained"
                                        color="primary"
                                        onClick={() => handleBookNow(schedule.id)}
                                        disabled={schedule.availableSeats === 0}
                                    >
                                        Book Now
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Container>
    );
}

export default ScheduleList; 