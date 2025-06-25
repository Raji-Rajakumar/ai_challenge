import React, { useEffect } from 'react';
import { 
    Routes, 
    Route, 
    Navigate, 
    useNavigate,
    createBrowserRouter,
    RouterProvider
} from 'react-router-dom';
import { Container } from 'react-bootstrap';
import Navigation from './components/Navigation';
import Login from './components/Login';
import Register from './components/Register';
import TaskList from './components/TaskList';
import LandingPage from './components/LandingPage';
import { AuthProvider, useAuth } from './context/AuthContext';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

// Protected Route component
function PrivateRoute({ children }) {
    const { user, loading } = useAuth();
    
    if (loading) {
        return <div>Loading...</div>;
    }
    
    return user ? children : <Navigate to="/login" />;
}

// New component to encapsulate content that needs AuthContext
function AppContent() {
    const { user, loading } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        // If not loading and user is null, and not already on the login page, navigate to login
        if (!loading && !user && window.location.pathname !== '/login' && window.location.pathname !== '/register') {
            navigate('/login', { replace: true });
        }
    }, [user, loading, navigate]);

    if (loading) {
        return <div>Loading authentication...</div>;
    }

    return (
        <>
            {user && <Navigation />}
            <Container fluid className="p-0">
                <Routes>
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route 
                        path="/tasks"
                        element={
                            <PrivateRoute>
                                <TaskList />
                            </PrivateRoute>
                        }
                    />
                    <Route path="*" element={<Navigate to={user ? "/tasks" : "/"} />} />
                </Routes>
            </Container>
        </>
    );
}

// Create router with future flags
const router = createBrowserRouter([
    {
        path: "*",
        element: <AppContent />
    }
], {
    future: {
        v7_startTransition: true,
        v7_relativeSplatPath: true
    }
});

function App() {
    return (
        <AuthProvider>
            <RouterProvider router={router} />
        </AuthProvider>
    );
}

export default App; 