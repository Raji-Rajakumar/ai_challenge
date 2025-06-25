import React from 'react';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import NotificationPanel from './NotificationPanel';
import { Box } from '@mui/material';

function Navigation() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    // Don't show navigation on login or register pages
    if (location.pathname === '/login' || location.pathname === '/register') {
        return null;
    }

    return (
        <Navbar bg="light" expand="lg" className="shadow-sm">
            <Container>
                <Navbar.Brand as={Link} to="/" className="fw-bold">
                    Task Manager
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {user && (
                            <Nav.Link className="text-muted">
                                <small>Welcome, {user.email}</small>
                            </Nav.Link>
                        )}
                    </Nav>
                    <Nav className="d-flex align-items-center">
                        {user && (
                            <>
                                <Box sx={{ mr: 2 }}>
                                    <NotificationPanel />
                                </Box>
                                <Button 
                                    variant="outline-danger" 
                                    onClick={handleLogout}
                                    className="px-4"
                                >
                                    Logout
                                </Button>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Navigation; 