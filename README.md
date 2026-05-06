# ClientHub API

Production-style REST API for **ClientHub**, a full-stack CRM application that allows authenticated users to manage clients, track relationship activity, view dashboard metrics, and generate searchable reports in a secure, user-scoped system.

The goal of this project is to demonstrate backend engineering practices beyond basic CRUD, including layered architecture, DTO separation, JWT authentication, ownership enforcement, transactional service logic, validation, centralized exception handling, reporting, dashboard aggregation, and production deployment.

---

## Live Demo

- Frontend: https://clienthub-frontend-sigma.vercel.app/login
- Backend API: https://clienthub-api.onrender.com
- Swagger / OpenAPI: https://clienthub-api.onrender.com/swagger-ui/index.html

### Demo Account

```text
Email: demo@clienthub.com
Password: DemoPassword123!
```
---

## Tech Stack

* Java 21
* Spring Boot
* Spring Web MVC
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* JWT
* Docker
* OpenAPI / Swagger
* Render

---

## Core Features

* JWT-based registration and login
* User-scoped client data
* Client CRUD operations
* Client activity tracking
* Activity completion workflow
* Dashboard summary metrics
* Recent activity display
* Searchable client and activity reports
* Request validation
* Centralized JSON error handling
* Paginated client retrieval
* Demo data seeding through a Spring seed profile

---

Architecture

ClientHub follows a layered backend architecture:

```text
Controller Layer
   ↓
Service Layer
   ↓
Repository Layer
   ↓
PostgreSQL
```

### Key Design Points

* Controllers handle HTTP concerns.
* Services contain business logic, ownership checks, validation, and transaction boundaries.
* Repositories use Spring Data JPA for persistence.
* DTOs separate API contracts from JPA entities.
* JWT authentication resolves the current user and scopes access to that user’s data.
* Reports are generated through an extensible report generator design.
* Dashboard metrics are computed server-side from authenticated user-owned data.

---

## Data Model

```text
User
 └── Client
      └── Activity
```

* A user can own many clients.
* A client belongs to exactly one user.
* A client can have many activities.
* Activities track type, status, notes, timestamps, and completion time.
* A unique constraint prevents duplicate client emails per user.

---

## Main Endpoints

### Auth

```text
POST /auth/register
POST /auth/login
```

### Dashboard

```text
GET /api/dashboard/summary
```

### Clients

```text
GET    /api/clients
GET    /api/clients/{id}
POST   /api/clients
PUT    /api/clients/{id}
DELETE /api/clients/{id}
```

### Activities

```text
GET    /api/clients/{clientId}/activities
POST   /api/clients/{clientId}/activities
PUT    /api/activities/{id}
PATCH  /api/activities/{id}/status
```

### Reports

```text
POST /api/reports
```

---

## Dashboard Summary

The dashboard endpoint returns real user-scoped metrics:

* total clients
* total activities
* open activities
* completed activities
* recent activities

Example response:

```json
{
  "totalClients": 8,
  "totalActivities": 16,
  "openActivities": 8,
  "completedActivities": 8,
  "recentActivities": [
    {
      "id": "7f0c6b35-8f8d-4a3c-bc41-569c9d1b430a",
      "clientId": "92a15a86-3be4-4e8c-a9ff-20d69e1f08e7",
      "clientName": "Austin Roofing Co.",
      "type": "EMAIL",
      "status": "OPEN",
      "notes": "Send follow-up summary and pricing information.",
      "createdAt": "2026-05-06T19:39:00Z"
    }
  ]
}
```

---

## Local Development

Start PostgreSQL:

```bash
docker compose up -d
```

Run the API:

```bash
./mvnw spring-boot:run
```

Run with demo seed data:
```text
SPRING_PROFILES_ACTIVE=dev,seed ./mvnw spring-boot:run
```

Local URLs:
```text
API: http://localhost:8080
Swagger: http://localhost:8080/swagger-ui/index.html
```

---

## Spring Profiles

ClientHub uses Spring profiles for environment-specific behavior.

```text
dev   - local development
prod  - production deployment
seed  - optional demo data seeding
```

Production normally runs with:

```text
SPRING_PROFILES_ACTIVE=prod
```

Demo seeding can be run temporarily with:

```text
SPRING_PROFILES_ACTIVE=prod,seed
```

After seeding, the app should be returned to:

```text
SPRING_PROFILES_ACTIVE=prod
```

---

## Production Environment Variables

```text
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
JWT_SECRET=<production-secret>
JWT_EXPIRATION=86400000
CORS_ALLOWED_ORIGINS=https://clienthub-frontend-sigma.vercel.app
```

---

## Design Decisions

#### DTO Separation

Entities are not exposed directly through the API. Request and response DTOs define stable API contracts and prevent persistence concerns from leaking into the API layer.

#### Service-Layer Business Logic

Business logic lives inside service classes rather than controllers. This keeps controllers thin and centralizes validation, ownership checks, report generation, dashboard aggregation, and transaction boundaries.

#### Ownership Enforcement

Client and activity operations are scoped to the authenticated user so one user cannot access or modify another user’s CRM data.

#### Stateless Authentication

JWT-based authentication avoids server-side sessions and supports a stateless API design.

#### Dashboard Aggregation

Dashboard metrics are calculated server-side from authenticated user-owned data instead of being hardcoded on the frontend.

#### Profile-Based Demo Seeding

Demo data is seeded through a dedicated Spring seed profile instead of running automatically during normal production startup.

---

## Future Enhancements

* Expanded unit and integration testing
* Testcontainers for database-backed tests
* CSV export for reports
* Activity due dates and follow-up reminders
* Advanced dashboard analytics
* Flyway or Liquibase database migrations
* CI/CD pipeline automation

---

## Project Purpose

ClientHub was built to demonstrate the backend structure used in real business applications: authentication, authorization, user-owned data, validation, reporting, dashboard aggregation, deployment configuration, and maintainable API contracts.

