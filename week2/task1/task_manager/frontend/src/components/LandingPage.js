import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Container } from 'react-bootstrap';
import './LandingPage.css';

const LandingPage = () => {
    const navigate = useNavigate();

    return (
        <div className="landing-page">
            <Container className="landing-content">
                <div className="app-logo">
                    <i className="fas fa-tasks"></i>
                </div>
                <h1 className="main-title">Task Manager</h1>
                <p className="subtitle">Organize your tasks efficiently and boost your productivity</p>
                <div className="features">
                    <div className="feature-item">
                        <i className="fas fa-check-circle"></i>
                        <span>Track your progress</span>
                    </div>
                    <div className="feature-item">
                        <i className="fas fa-clock"></i>
                        <span>Meet deadlines</span>
                    </div>
                    <div className="feature-item">
                        <i className="fas fa-chart-line"></i>
                        <span>Boost productivity</span>
                    </div>
                </div>
                <div className="button-container">
                    <Button 
                        variant="primary" 
                        size="lg" 
                        className="action-button login-button"
                        onClick={() => navigate('/login')}
                    >
                        <i className="fas fa-sign-in-alt"></i> Login
                    </Button>
                    <Button 
                        variant="outline-primary" 
                        size="lg" 
                        className="action-button register-button"
                        onClick={() => navigate('/register')}
                    >
                        <i className="fas fa-user-plus"></i> Register
                    </Button>
                </div>
            </Container>
        </div>
    );
};

export default LandingPage; 