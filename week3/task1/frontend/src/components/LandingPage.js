import React from 'react';
import { Container, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const LandingPage = () => {
  const navigate = useNavigate();

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background: 'linear-gradient(45deg, #2196F3 30%, #21CBF3 90%)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <Container maxWidth="md">
        <Box
          sx={{
            textAlign: 'center',
            color: 'white',
            py: 8,
          }}
        >
          <Typography variant="h2" component="h1" gutterBottom>
            Welcome to Bus Booking System
          </Typography>
          <Typography variant="h5" component="h2" gutterBottom>
            Book your bus tickets with ease and convenience
          </Typography>
          <Box sx={{ mt: 4 }}>
            <Button
              variant="contained"
              color="secondary"
              size="large"
              onClick={() => navigate('/login')}
              sx={{ mr: 2 }}
            >
              Login
            </Button>
            <Button
              variant="outlined"
              color="inherit"
              size="large"
              onClick={() => navigate('/register')}
            >
              Register
            </Button>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default LandingPage; 