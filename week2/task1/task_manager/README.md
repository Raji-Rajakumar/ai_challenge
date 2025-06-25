# Task Manager Application

A full-stack task management application built with React, Spring Boot, and MySQL.

## Prerequisites

- Java 11 or higher
- Node.js 14 or higher
- MySQL 8.0 or higher
- Maven

## Database Setup

1. Create a MySQL database named `taskmanager`:
```sql
CREATE DATABASE taskmanager;
```

2. The application will automatically create the required tables on startup.

## Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`.

## Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend application will start on `http://localhost:3000`.

## Features

- Create, read, update, and delete tasks
- Filter tasks by status (All, Pending, Completed)
- Responsive design with Bootstrap
- Real-time updates
- Form validation
- Error handling

## API Endpoints

- `GET /tasks` - Get all tasks
- `GET /tasks/{id}` - Get a specific task
- `POST /tasks` - Create a new task
- `PUT /tasks/{id}` - Update a task
- `DELETE /tasks/{id}` - Delete a task

## Technologies Used

- Frontend:
  - React
  - React Bootstrap
  - Axios
  - CSS3

- Backend:
  - Spring Boot
  - Spring Data JPA
  - MySQL
  - Lombok

## Project Structure

```
task_manager/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── taskmanager/
│   │   │   │           ├── controller/
│   │   │   │           ├── model/
│   │   │   │           ├── repository/
│   │   │   │           └── TaskManagerApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
└── frontend/
    ├── src/
    │   ├── components/
    │   │   ├── TaskList.js
    │   │   ├── TaskForm.js
    │   │   └── TaskItem.js
    │   ├── App.js
    │   └── App.css
    └── package.json
``` 