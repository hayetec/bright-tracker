# Bright Tracker

Bright Tracker is a full-stack school management application designed for a
school. The application provides a tablet-friendly interface for
managing students, staff, attendance, and other day-to-day school operations.

## Tech Stack

### Backend
- Kotlin
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Gradle

### Infrastructure
- Docker
- Docker Compose

## Current Features

### Student Management
- Create students
- View all students
- View a student by ID
- Update student information
- Delete students
- Request validation
- 404 handling for students that do not exist

## Project Structure

```text
bright-tracker/
├── backend/             # Spring Boot API
├── docker-compose.yml   # Local PostgreSQL
└── README.md