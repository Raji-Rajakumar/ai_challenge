import React from 'react';
import { Card, Button, Badge } from 'react-bootstrap';

function TaskItem({ task, onEdit, onDelete }) {
  const formatDate = (dateString) => {
    if (!dateString) return 'No due date';
    return new Date(dateString).toLocaleDateString();
  };

  return (
    <Card className="h-100">
      <Card.Body>
        <div className="d-flex justify-content-between align-items-start mb-2">
          <Card.Title className="mb-0">{task.title}</Card.Title>
          <Badge bg={task.status === 'Completed' ? 'success' : 'warning'}>
            {task.status}
          </Badge>
        </div>
        
        <Card.Text className="text-muted mb-2">
          Due: {formatDate(task.dueDate)}
        </Card.Text>
        
        <Card.Text>
          {task.description || 'No description provided'}
        </Card.Text>
      </Card.Body>
      
      <Card.Footer className="bg-transparent">
        <div className="d-flex justify-content-end">
          <Button
            variant="outline-primary"
            size="sm"
            onClick={onEdit}
            className="me-2"
          >
            Edit
          </Button>
          <Button
            variant="outline-danger"
            size="sm"
            onClick={onDelete}
          >
            Delete
          </Button>
        </div>
      </Card.Footer>
    </Card>
  );
}

export default TaskItem; 