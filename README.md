# Bright Tracker

Bright Tracker is a full-stack school management application designed to support
day-to-day school operations. The application provides a tablet-friendly
interface for managing students, staff, classrooms, guardians, attendance,
meals, allergies, and other school operations.

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

### Staff Management

- Create staff members
- View all staff members
- View a staff member by ID
- Update staff information
- Delete staff members
- Support staff roles including teachers and teacher aides
- Request validation
- 404 handling for staff members that do not exist

### Classroom Management

- Create classrooms
- View all classrooms
- View a classroom by ID
- Update classroom information
- Delete classrooms
- Track grade level, room number, capacity, and status
- 404 handling for classrooms that do not exist

### Student Enrollment

- Enroll students in classrooms
- View students enrolled in a classroom
- Remove students from classrooms
- Prevent duplicate enrollments
- Validate student and classroom existence

### Classroom Staff Assignment

- Assign teachers and teacher aides to classrooms
- View staff assigned to a classroom
- Remove staff assignments
- Support multiple staff members per classroom
- Prevent duplicate staff assignments
- Restrict classroom assignments to teachers and teacher aides

### Guardian Management

- Create guardians
- View all guardians
- View a guardian by ID
- Update guardian contact information
- Delete guardians
- Store phone number and optional email
- 404 handling for guardians that do not exist

### Student Guardian Relationships

- Link guardians to students
- Support multiple guardians per student
- Support multiple students per guardian
- Track guardian relationship type
- Designate primary contacts
- Designate emergency contacts
- View guardians for a student
- View students associated with a guardian
- Update guardian relationships
- Remove guardian relationships
- Prevent duplicate student-guardian relationships

### Student Meal Tracking

- Create daily meal records for students
- Track breakfast, lunch, and dinner participation
- View all meal records for a student
- View a student's meal record by date
- Update daily meal participation
- Delete meal records
- Prevent duplicate meal records for the same student and date
- Automatically track created and updated timestamps

### Student Allergy Management

- Record known student allergies
- Track allergy severity as mild, moderate, or severe
- Store allergy-specific notes and safety instructions
- Support multiple allergies per student
- View all allergies for a student
- View an individual allergy by ID
- Update allergy information
- Delete allergy records
- Prevent duplicate allergens for the same student
- Treat allergen names as case-insensitive for duplicate detection

## Testing

The backend includes integration tests using:

* JUnit 5
* Spring Boot Test
* MockMvc
* Testcontainers
* PostgreSQL 16

Current integration test coverage includes:

**Student API**

* Create a student
* Retrieve all students
* Retrieve a student by ID
* Update a student
* Delete a student
* Validate invalid requests
* Handle missing students with `404 Not Found`

**Staff API**

* Create a staff member
* Retrieve all staff members
* Retrieve a staff member by ID
* Update a staff member
* Delete a staff member
* Validate invalid requests
* Handle missing staff members with `404 Not Found`

**Student Enrollment API**

* Enroll a student in a classroom
* Retrieve classroom enrollments
* Remove a student from a classroom
* Reject duplicate enrollments with `409 Conflict`
* Handle missing students with `404 Not Found`
* Handle missing classrooms with `404 Not Found`
* Handle missing enrollments with `404 Not Found`

Integration tests use Testcontainers to start an isolated PostgreSQL 16
database. Flyway migrations are automatically applied before the tests run.


## API Overview

### Students

| Method | Endpoint             | Description      |
| ------ | -------------------- | ---------------- |
| POST   | `/api/students`      | Create a student |
| GET    | `/api/students`      | List students    |
| GET    | `/api/students/{id}` | Get a student    |
| PUT    | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

### Staff

| Method | Endpoint          | Description           |
| ------ | ----------------- | --------------------- |
| POST   | `/api/staff`      | Create a staff member |
| GET    | `/api/staff`      | List staff            |
| GET    | `/api/staff/{id}` | Get a staff member    |
| PUT    | `/api/staff/{id}` | Update a staff member |
| DELETE | `/api/staff/{id}` | Delete a staff member |

### Classrooms

| Method | Endpoint               | Description        |
| ------ | ---------------------- | ------------------ |
| POST   | `/api/classrooms`      | Create a classroom |
| GET    | `/api/classrooms`      | List classrooms    |
| GET    | `/api/classrooms/{id}` | Get a classroom    |
| PUT    | `/api/classrooms/{id}` | Update a classroom |
| DELETE | `/api/classrooms/{id}` | Delete a classroom |

### Classroom Relationships

| Method | Endpoint                                             | Description             |
| ------ | ---------------------------------------------------- | ----------------------- |
| POST   | `/api/classrooms/{classroomId}/students/{studentId}` | Enroll a student        |
| GET    | `/api/classrooms/{classroomId}/students`             | List classroom students |
| DELETE | `/api/classrooms/{classroomId}/students/{studentId}` | Remove a student        |
| POST   | `/api/classrooms/{classroomId}/staff/{staffId}`      | Assign staff            |
| GET    | `/api/classrooms/{classroomId}/staff`                | List classroom staff    |
| DELETE | `/api/classrooms/{classroomId}/staff/{staffId}`      | Remove staff            |

### Guardians

| Method | Endpoint              | Description       |
| ------ | --------------------- | ----------------- |
| POST   | `/api/guardians`      | Create a guardian |
| GET    | `/api/guardians`      | List guardians    |
| GET    | `/api/guardians/{id}` | Get a guardian    |
| PUT    | `/api/guardians/{id}` | Update a guardian |
| DELETE | `/api/guardians/{id}` | Delete a guardian |

### Student Guardian Relationships

| Method | Endpoint                                           | Description                  |
| ------ | -------------------------------------------------- | ---------------------------- |
| POST   | `/api/students/{studentId}/guardians/{guardianId}` | Link a guardian to a student |
| GET    | `/api/students/{studentId}/guardians`              | List a student's guardians   |
| GET    | `/api/guardians/{guardianId}/students`             | List a guardian's students   |
| PUT    | `/api/students/{studentId}/guardians/{guardianId}` | Update the relationship      |
| DELETE | `/api/students/{studentId}/guardians/{guardianId}` | Remove the relationship      |

### Student Meals

| Method | Endpoint                                       | Description                   |
| ------ | ---------------------------------------------- | ----------------------------- |
| POST   | `/api/students/{studentId}/meals`              | Create a daily meal record    |
| GET    | `/api/students/{studentId}/meals`              | List a student's meal records |
| GET    | `/api/students/{studentId}/meals/{recordDate}` | Get a meal record by date     |
| PUT    | `/api/students/{studentId}/meals/{recordDate}` | Update a meal record          |
| DELETE | `/api/students/{studentId}/meals/{recordDate}` | Delete a meal record          |

### Student Allergies

| Method | Endpoint                                          | Description                |
| ------ | ------------------------------------------------- | -------------------------- |
| POST   | `/api/students/{studentId}/allergies`             | Add a student allergy      |
| GET    | `/api/students/{studentId}/allergies`             | List a student's allergies |
| GET    | `/api/students/{studentId}/allergies/{allergyId}` | Get an allergy             |
| PUT    | `/api/students/{studentId}/allergies/{allergyId}` | Update an allergy          |
| DELETE | `/api/students/{studentId}/allergies/{allergyId}` | Delete an allergy          |

## Database Relationships

```text
Classroom
├── Students
│   └── student_enrollments
└── Staff
    └── classroom_staff_assignments

Student
├── Guardians
│   └── student_guardians
│       ├── relationship
│       ├── is_primary_contact
│       └── is_emergency_contact
│
├── Meal Records
│   └── student_meal_records
│       ├── record_date
│       ├── breakfast_eaten
│       ├── lunch_eaten
│       └── dinner_eaten
│
└── Allergies
    └── student_allergies
        ├── allergen
        ├── severity
        └── notes

Guardian
└── Students
    └── student_guardians
```

Students and guardians have a many-to-many relationship. A student can have
multiple guardians, and a guardian can be associated with multiple students.

Each student can have multiple daily meal records and multiple allergy records.
A meal record belongs to a single student and date, while an allergy record
belongs to a single student.

## Project Structure

```text
bright-tracker/
├── backend/
│   └── src/main/
│       ├── kotlin/com/issenur/brighttracker/
│       │   ├── allergy/
│       │   ├── assignment/
│       │   ├── classroom/
│       │   ├── enrollment/
│       │   ├── guardian/
│       │   ├── meal/
│       │   ├── staff/
│       │   └── student/
│       └── resources/
│           └── db/migration/
├── docker-compose.yml
└── README.md
```

## Local Development

Start PostgreSQL:

```bash
docker compose up -d
```

Start the backend:

```bash
cd backend
./gradlew bootRun
```

The backend API runs locally on port `8080`.

## Testing & Coverage

Integration tests use JUnit 5, Spring Boot Test, MockMvc, Testcontainers,
and PostgreSQL 16. Testcontainers provides an isolated PostgreSQL database,
and Flyway applies the database migrations before the tests run.

Current integration tests cover the Student and Staff APIs, including CRUD
operations, request validation, and `404 Not Found` handling.

### Run Tests

From the `backend` directory:

```bash
./gradlew test
```

Test report:

```text
build/reports/tests/test/index.html
```

### Generate Code Coverage

JaCoCo is used to generate backend code coverage reports.

```bash
./gradlew clean test jacocoTestReport
```

JaCoCo coverage report:

```text
build/reports/jacoco/test/html/index.html
```

### Run Full Build

```bash
./gradlew clean build
```
