import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Modal, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';

const TaskList = () => {
    const [tasks, setTasks] = useState([]);
    const [newTask, setNewTask] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingTask, setEditingTask] = useState(null);
    const [showEditModal, setShowEditModal] = useState(false);
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    useEffect(() => {
        if (!user) {
            navigate('/login');
            return;
        }
        fetchTasks();
    }, [user, navigate]);

    const handleLogout = () => {
        logout();
        navigate('/login', { replace: true });
    };

    const fetchTasks = async () => {
        try {
            setLoading(true);
            console.log('Fetching tasks...');
            const response = await api.get('/api/tasks');
            console.log('Tasks response:', response.data);
            setTasks(response.data);
            setError(null);
        } catch (err) {
            console.error('Error fetching tasks:', err);
            if (err.response?.status === 401 || err.response?.status === 403) {
                logout();
                navigate('/login');
            } else {
                setError(err.response?.data?.message || 'Failed to fetch tasks. Please try again.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!newTask.trim()) return;

        try {
            const taskData = {
                title: newTask,
                description: '',
                completed: false,
                status: 'Pending'
            };
            
            console.log('Creating task:', taskData);
            const response = await api.post('/api/tasks', taskData);
            console.log('Create task response:', response.data);
            setTasks([...tasks, response.data]);
            setNewTask('');
            setError(null);
        } catch (err) {
            console.error('Error creating task:', err);
            setError(err.response?.data?.message || 'Failed to create task. Please try again.');
        }
    };

    const handleDelete = async (taskId) => {
        try {
            console.log('Deleting task:', taskId);
            await api.delete(`/api/tasks/${taskId}`);
            setTasks(tasks.filter(task => task.id !== taskId));
            setError(null);
        } catch (err) {
            console.error('Error deleting task:', err);
            setError(err.response?.data?.message || 'Failed to delete task. Please try again.');
        }
    };

    const handleToggleComplete = async (taskId, completed) => {
        try {
            const task = tasks.find(t => t.id === taskId);
            const updatedTask = {
                ...task,
                completed: !completed,
                status: !completed ? 'Completed' : 'Pending'
            };
            
            console.log('Updating task:', updatedTask);
            const response = await api.put(`/api/tasks/${taskId}`, updatedTask);
            console.log('Update task response:', response.data);
            setTasks(tasks.map(task => 
                task.id === taskId ? response.data : task
            ));
            setError(null);
        } catch (err) {
            console.error('Error updating task:', err);
            setError(err.response?.data?.message || 'Failed to update task. Please try again.');
        }
    };

    const handleEdit = (task) => {
        setEditingTask({
            ...task,
            due_date: task.dueDate ? new Date(task.dueDate).toISOString().split('T')[0] : ''
        });
        setShowEditModal(true);
    };

    const handleUpdateTask = async (e) => {
        e.preventDefault();
        try {
            const taskToUpdate = {
                ...editingTask,
                dueDate: editingTask.due_date || null
            };
            delete taskToUpdate.due_date;

            console.log('Updating task:', taskToUpdate);
            const response = await api.put(`/api/tasks/${editingTask.id}`, taskToUpdate);
            console.log('Update task response:', response.data);
            setTasks(tasks.map(task => 
                task.id === editingTask.id ? response.data : task
            ));
            setShowEditModal(false);
            setEditingTask(null);
            setError(null);
        } catch (err) {
            console.error('Error updating task:', err);
            setError(err.response?.data?.message || 'Failed to update task. Please try again.');
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString();
    };

    if (loading) {
        return <div className="text-center">Loading...</div>;
    }

    return (
        <Container className="mt-4">
            <Row className="mb-4">
                <Col>
                    <h2>Task List</h2>
                </Col>
                {/* <Col xs="auto">
                    <Button variant="outline-danger" onClick={handleLogout}>
                        Logout
                    </Button>
                </Col> */}
            </Row>

            {error && (
                <Alert variant="danger" className="mb-4">
                    {error}
                </Alert>
            )}

            <Card className="mb-4">
                <Card.Body>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group>
                            <Form.Control
                                type="text"
                                value={newTask}
                                onChange={(e) => setNewTask(e.target.value)}
                                placeholder="Add a new task..."
                                required
                            />
                        </Form.Group>
                        <Button type="submit" className="mt-2">
                            Add Task
                        </Button>
                    </Form>
                </Card.Body>
            </Card>

            {tasks.map(task => (
                <Card key={task.id} className="mb-3">
                    <Card.Body>
                        <Row>
                            <Col>
                                <h5 className={task.completed ? 'text-muted text-decoration-line-through' : ''}>
                                    {task.title}
                                </h5>
                                <p className="text-muted">{task.description}</p>
                            </Col>
                            <Col xs="auto">
                                <Button
                                    variant={task.completed ? 'success' : 'outline-success'}
                                    className="me-2"
                                    onClick={() => handleToggleComplete(task.id, task.completed)}
                                >
                                    {task.completed ? 'Completed' : 'Mark Complete'}
                                </Button>
                                <Button
                                    variant="outline-primary"
                                    className="me-2"
                                    onClick={() => handleEdit(task)}
                                >
                                    Edit
                                </Button>
                                <Button
                                    variant="outline-danger"
                                    onClick={() => handleDelete(task.id)}
                                >
                                    Delete
                                </Button>
                            </Col>
                        </Row>
                    </Card.Body>
                </Card>
            ))}

            <Modal show={showEditModal} onHide={() => setShowEditModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit Task</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleUpdateTask}>
                        <Form.Group className="mb-3">
                            <Form.Label>Title</Form.Label>
                            <Form.Control
                                type="text"
                                value={editingTask?.title || ''}
                                onChange={(e) => setEditingTask({...editingTask, title: e.target.value})}
                                required
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                value={editingTask?.description || ''}
                                onChange={(e) => setEditingTask({...editingTask, description: e.target.value})}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Due Date</Form.Label>
                            <Form.Control
                                type="date"
                                value={editingTask?.due_date || ''}
                                onChange={(e) => setEditingTask({...editingTask, due_date: e.target.value})}
                            />
                        </Form.Group>
                        <Button type="submit" className="me-2">
                            Save Changes
                        </Button>
                        <Button variant="secondary" onClick={() => setShowEditModal(false)}>
                            Cancel
                        </Button>
                    </Form>
                </Modal.Body>
            </Modal>
        </Container>
    );
};

export default TaskList; 