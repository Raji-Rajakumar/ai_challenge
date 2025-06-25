import React, { useState, useEffect } from 'react';
import { Container, Typography, TextField, Button, Box, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const ScheduleSearch = () => {
  const [schedules, setSchedules] = useState([]);
  const [source, setSource] = useState('');
  const [destination, setDestination] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const fetchAllSchedules = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8080/api/schedules', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setSchedules(response.data);
    } catch (err) {
      setError('Error fetching schedules: ' + (err.response?.data?.message || err.message));
    }
  };

  useEffect(() => {
    fetchAllSchedules();
  }, []);

  const handleSearch = async () => {
    setError('');
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8080/api/schedules/search', {
        params: { source, destination },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setSchedules(response.data);
    } catch (err) {
      setError('Error searching schedules: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleBookClick = (scheduleId) => {
    navigate(`/dashboard/book/${scheduleId}`);
  };

  return (
    <Container>
      <Typography variant="h4" component="h1" gutterBottom>Search Bus</Typography>
      <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
        <TextField
          label="From"
          variant="outlined"
          value={source}
          onChange={(e) => setSource(e.target.value)}
        />
        <TextField
          label="To"
          variant="outlined"
          value={destination}
          onChange={(e) => setDestination(e.target.value)}
        />
        <Button variant="contained" onClick={handleSearch}>Search</Button>
        <Button variant="outlined" onClick={() => { setSource(''); setDestination(''); fetchAllSchedules(); }}>Clear Search</Button>
      </Box>
      {error && <Typography color="error">{error}</Typography>}
      
      {schedules.length > 0 ? (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Bus Name</TableCell>
                <TableCell>Bus Number</TableCell>
                <TableCell>Source</TableCell>
                <TableCell>Destination</TableCell>
                <TableCell>Departure Time</TableCell>
                <TableCell>Arrival Time</TableCell>
                <TableCell>Fare</TableCell>
                <TableCell>Available Seats</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {schedules.map((schedule) => (
                <TableRow key={schedule.id}>
                  <TableCell>{schedule.bus?.busName || 'N/A'}</TableCell>
                  <TableCell>{schedule.bus?.busNumber || 'N/A'}</TableCell>
                  <TableCell>{schedule.source}</TableCell>
                  <TableCell>{schedule.destination}</TableCell>
                  <TableCell>{new Date(schedule.departureTime).toLocaleString()}</TableCell>
                  <TableCell>{new Date(schedule.arrivalTime).toLocaleString()}</TableCell>
                  <TableCell>${schedule.fare}</TableCell>
                  <TableCell>{schedule.availableSeats}</TableCell>
                  <TableCell>
                    <Button variant="contained" color="primary" onClick={() => handleBookClick(schedule.id)}>
                      Book
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      ) : (
        <Typography variant="h6" align="center">No bus found.</Typography>
      )}
    </Container>
  );
};

export default ScheduleSearch; 