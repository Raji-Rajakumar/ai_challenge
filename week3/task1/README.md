# Bus Ticket Booking System

A full-stack bus ticket booking system built with Spring Boot and React.

## Features

- View available buses and their details
- Search bus schedules by source and destination
- Book tickets with passenger details
- View and manage bookings
- Cancel bookings
- Real-time seat availability

## Prerequisites

- Java 11 or higher
- Node.js 14 or higher
- Maven
- npm

## Project Structure

```
task1/
├── backend/                 # Spring Boot backend
│   ├── src/
│   └── pom.xml
└── frontend/               # React frontend
    ├── src/
    └── package.json
```

## Setup and Running

### Backend (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd task1/backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend server will start on http://localhost:8080

### Frontend (React)

1. Navigate to the frontend directory:
   ```bash
   cd task1/frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend application will start on http://localhost:3000

## API Endpoints

### Buses
- GET /api/buses - Get all buses
- GET /api/buses/{id} - Get bus by ID
- POST /api/buses - Create new bus
- PUT /api/buses/{id} - Update bus
- DELETE /api/buses/{id} - Delete bus

### Schedules
- GET /api/schedules - Get all schedules
- GET /api/schedules/{id} - Get schedule by ID
- GET /api/schedules/search - Search schedules by source and destination
- POST /api/schedules - Create new schedule
- PUT /api/schedules/{id} - Update schedule
- DELETE /api/schedules/{id} - Delete schedule

### Bookings
- GET /api/bookings - Get all bookings
- GET /api/bookings/{id} - Get booking by ID
- GET /api/bookings/email/{email} - Get bookings by email
- POST /api/bookings - Create new booking
- POST /api/bookings/{id}/cancel - Cancel booking

## Database

The application uses H2 in-memory database for development. The database console is available at http://localhost:8080/h2-console with the following credentials:
- JDBC URL: jdbc:h2:mem:busdb
- Username: sa
- Password: (empty) 