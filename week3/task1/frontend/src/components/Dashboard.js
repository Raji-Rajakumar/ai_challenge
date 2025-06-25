import React from 'react';
import { useNavigate, Outlet } from 'react-router-dom';
import {
  Container,
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
} from '@mui/material';

const Dashboard = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user'));

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" style={{ flexGrow: 1 }}>
            Bus Booking System
          </Typography>
          <Typography variant="body1" style={{ marginRight: '1rem' }}>
            Welcome, {user?.fullName}
          </Typography>
          <Button color="inherit" onClick={() => navigate('/dashboard/profile')}>
            Profile
          </Button>
          <Button color="inherit" onClick={handleLogout}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>

      <Container style={{ marginTop: '2rem' }}>
        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
          <Button variant="contained" onClick={() => navigate('/dashboard/buses')}>
            View Buses
          </Button>
          <Button variant="contained" onClick={() => navigate('/dashboard/my-bookings')}>
            My Bookings
          </Button>
        </Box>

        <Outlet />
      </Container>
    </div>
  );
};

export default Dashboard; 