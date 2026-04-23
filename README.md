# ClientHub API

Production-style REST API for **ClientHub**, a full-stack CRM application that enables authenticated users to manage clients, track relationship activity, and generate searchable reports in a secure, user-scoped system.

This project was built to demonstrate backend engineering practices beyond basic CRUD, including layered architecture, DTO separation, transactional service boundaries, JWT authentication, ownership enforcement, structured validation, and extensible reporting.

## Project Status

ClientHub is a deployed portfolio project and remains under active improvement as features, validation, usability, and documentation continue to evolve.

## Related Repositories

- **Frontend:** [ClientHub Frontend](https://github.com/mdubelbeis/clienthub-frontend)
- **Backend:** [ClientHub API](https://github.com/mdubelbeis/clienthub-api)

## Live Demo
- **Live Demo:** https://clienthub-frontend-sigma.vercel.app/login
- **Swagger / OpenAPI Docs:** https://clienthub-api.onrender.com/swagger-ui/index.html

---

## Overview

ClientHub models a simple but realistic CRM workflow where authenticated users manage their own client relationships and maintain a timeline of activities tied to each client.

The system supports:

- user authentication with JWT
- client creation, update, deletion, and retrieval
- activity creation, editing, and completion tracking
- searchable client and activity reporting
- user-scoped access control to protect tenant data

This API was designed to reflect production-minded backend structure rather than a single-layer demo application.

---

## Core Features

- **JWT authentication** for stateless API security
- **User-scoped data ownership** so users can only access their own clients and activities
- **Client management** with create, update, delete, and detail retrieval
- **Activity tracking** with status updates and client-linked history
- **Searchable reporting module** for client and activity reports
- **Structured validation** for request data
- **Centralized exception handling** for consistent JSON error responses
- **Paginated endpoints** for scalable retrieval patterns

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Hibernate
- PostgreSQL
- JWT
- Lombok
- Docker
- OpenAPI / Swagger

---

## What This Project Demonstrates

This project showcases:

- layered backend architecture
- relational domain modeling
- DTO-based API contract design
- transactional business logic
- stateless authentication with JWT
- secure ownership enforcement
- centralized error handling
- extensible service design for future growth
- containerized local development

---

## Architecture

ClientHub API follows a layered backend architecture designed for maintainability, security, and clear separation of concerns.

### Controller Layer
Handles HTTP request/response concerns and delegates business operations to services.

### Service Layer
Contains business logic, validation, ownership checks, reporting logic, and transaction boundaries.

### Repository Layer
Provides persistence access through Spring Data JPA repositories.

### Domain Layer
Defines the core entities and their relationships: users, clients, and activities.

### DTO Layer
Separates API contracts from persistence models and prevents direct entity exposure.

### Exception Layer
Centralizes error handling and standardizes JSON error responses.

### Security Layer
Implements JWT authentication, request filtering, and authenticated user resolution.

---

## Architectural Highlights

- JWT-based stateless authentication
- service-layer transaction management with `@Transactional`
- user ownership enforcement between `User -> Client -> Activity`
- DTO separation for stable request and response contracts
- paginated resource retrieval
- global exception handling
- reporting module built with inheritance and polymorphism
- Dockerized PostgreSQL for reproducible local setup

---

## Reporting Module

One of the key architectural additions in ClientHub is the reporting module.

The reporting system supports:
- **Client Summary Report**
- **Activity Report**

Each report returns:
- a report title
- a generated timestamp
- multiple columns
- multiple rows
- optional search filtering

The reporting module was intentionally designed using object-oriented principles:

- **Inheritance** through a shared abstract report generator
- **Polymorphism** through multiple report generator implementations selected through a common contract
- **Encapsulation** through service-layer orchestration and DTO-based responses

This allows additional report types to be added later without changing controller logic.

---

## Data Model

```mermaid
erDiagram
    USERS {
        uuid id PK
        varchar email
        varchar password
        timestamp created_at
    }

    CLIENTS {
        uuid id PK
        uuid user_id FK
        varchar name
        varchar email
        varchar phone
        timestamp created_at
        timestamp updated_at
    }

    ACTIVITIES {
        uuid id PK
        uuid client_id FK
        varchar type
        varchar status
        text notes
        timestamp created_at
        timestamp updated_at
        timestamp completed_at
    }

    USERS ||--o{ CLIENTS : owns
    CLIENTS ||--o{ ACTIVITIES : contains

	flowchart TD
    Client[API Consumer]
    Security[JWT Authentication Filter]
    Controller[Controller Layer]
    Service[Service Layer]
    Repository[Repository Layer]
    Database[(PostgreSQL)]

    Client --> Security
    Security --> Controller
    Controller --> Service
    Service --> Repository
    Repository --> Database
```
---

## Authentication

Authentication is implemented using JWT tokens.

### Login

```http request
POST /auth/login
```

Example request:
```http request
{
  "email": "user@example.com",
  "password": "password"
}
```

Example response:

```json
{
  "token": "JWT_TOKEN"
}
```
The authenticated user is resolved from the token and used to scope all client and activity access.

## Example Endpoints

### Auth

```http request
POST /auth/login
POST /auth/register
```

### Clients
```http request
GET    /api/clients
GET    /api/clients/{id}
POST   /api/clients
PUT    /api/clients/{id}
DELETE /api/clients/{id}
```

### Activities
```http request
GET    /api/clients/{clientId}/activities
POST   /api/clients/{clientId}/activities
PUT    /api/activities/{id}
PATCH  /api/activities/{id}/status
```

### Reports
```http request
POST /reports
```
Example report request:

```json
{
  "reportType": "activities",
  "searchTerm": "open"
}
```

---

## Example Client Response
```json
{
  "id": "6b0b0c75-3f6d-4f3c-8b39-33f5d40b4f21",
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "512-905-1530",
  "createdAt": "2026-03-15T22:55:21Z",
  "updatedAt": "2026-03-15T22:55:21Z"
}
```

---

## Validation and Error Handling

ClientHub uses structured request validation and centralized exception handling to produce consistent API responses.

Examples include:
* required field validation
* duplicate client email prevention per authenticated user
* phone formatting/validation
* unsupported report type handling
* protected ownership checks for user data

Example error response:

```json
{
  "timestamp": "2026-03-15T22:55:21Z",
  "status": 404,
  "error": "Not Found",
  "message": "Client not found",
  "path": "/api/clients/123"
}
```

---

## Pagination

Collection endpoints use paginated responses for scalability.

```http request
GET /api/clients?page=0&size=20
```

Example response:
```json
{
  "content": [],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

## Local Development

1. Start PostgreSQL

```bash
docker compose up -d
```

2. Run the API
```bash
./mvnw spring-boot:run
```

3. Access the application

* API: http://localhost:8080
* Swagger UI: http://localhost:8080/swagger-ui.html

--- 

## Design Decisions

### DTO Separation
Entities are not exposed directly through the API. Request and response DTOs define stable API contracts and prevent persistence concerns from leaking into the API layer.

### Service-Layer Transactions

Business logic lives inside service classes and uses transaction boundaries for consistency and atomicity.

### Ownership Enforcement

All client and activity operations are scoped to the authenticated user to prevent cross-user data access.

### Stateless Authentication

JWT-based authentication avoids server-side sessions and supports scalable API design.

### Extensible Reporting Design

The reports module uses a shared abstraction so that new report types can be added without rewriting endpoint orchestration.

---

## Future Enhancements

* unit and integration test expansion
* advanced report filters
* CSV export for reports
* richer activity analytics
* role-based authorization
* CI/CD pipeline automation
* observability and metrics
* improved frontend/admin UX integrations

---

## Why This Project Matters

ClientHub was built to show the kind of structure expected in a real backend application:

* not just CRUD endpoints
* but authentication
* ownership enforcement
* validation
* layered design
* extensibility
* maintainability
* and production-minded API contracts

It represents the kind of backend work involved in building secure, scalable full-stack business applications.