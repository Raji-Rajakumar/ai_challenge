import React, { useState } from 'react';
import { Form, Button, Card, Alert } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

function Register() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { register } = useAuth();
    const navigate = useNavigate();

    const validatePassword = (password) => {
        const regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/;
        return regex.test(password);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (!name.trim()) {
            return setError('Name is required');
        }

        if (password !== confirmPassword) {
            return setError('Passwords do not match');
        }

        if (!validatePassword(password)) {
            return setError(
                'Password must be at least 8 characters long and contain at least one digit, ' +
                'one uppercase letter, one lowercase letter, and one special character'
            );
        }

        setLoading(true);
        const result = await register(name, email, password);
        if (result.success) {
            navigate('/tasks');
        } else {
            setError(result.error);
        }
        setLoading(false);
    };

    return (
        <Card className="w-100" style={{ maxWidth: '400px', margin: '0 auto' }}>
            <Card.Body>
                <h2 className="text-center mb-4">Register</h2>
                {error && <Alert variant="danger">{error}</Alert>}
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Name</Form.Label>
                        <Form.Control
                            type="text"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <Form.Text className="text-muted">
                            Password must be at least 8 characters long and contain at least one digit,
                            one uppercase letter, one lowercase letter, and one special character.
                        </Form.Text>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Confirm Password</Form.Label>
                        <Form.Control
                            type="password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Button
                        disabled={loading}
                        className="w-100"
                        type="submit"
                    >
                        {loading ? 'Registering...' : 'Register'}
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    );
}

export default Register; 