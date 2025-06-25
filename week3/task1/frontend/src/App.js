import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material';
import LandingPage from './components/LandingPage';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import ScheduleSearch from './components/ScheduleSearch';
import BookingForm from './components/BookingForm';
import MyBookings from './components/MyBookings';
import UserProfile from './components/UserProfile';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2196F3',
    },
    secondary: {
      main: '#f50057',
    },
  },
});

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" />;
};

function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route
            path="/dashboard"
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            }
          >
            <Route index element={<ScheduleSearch />} />
            <Route path="buses" element={<ScheduleSearch />} />
            <Route path="book/:scheduleId" element={<BookingForm />} />
            <Route path="my-bookings" element={<MyBookings />} />
            <Route path="profile" element={<UserProfile />} />
          </Route>
        </Routes>
      </Router>
    </ThemeProvider>
  );
}

export default App; 