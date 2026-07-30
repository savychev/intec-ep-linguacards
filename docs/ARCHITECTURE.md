# LinguaCards - Architecture (High-Level)

## 1. Architecture Style
Modular monolith with layered architecture.

- Monolith: single backend application (Spring Boot)
- Modular: clear packages/modules by feature
- Layered: controller -> service -> repository

Frontend is a separate SPA (Angular) consuming REST APIs.

## 2. System Context (C4 - Context)
Actors:
- User (authenticated)

Systems:
- LinguaCards Web App (Angular + Spring Boot)
- MySQL database

User interacts with Angular UI. Angular calls Spring Boot REST API. Spring Boot persists data to MySQL.

## 3. Containers (C4 - Container)
1) Frontend (Angular)
- UI pages: Auth, Decks, Cards, Training, Stats
- JWT stored client-side (localStorage or memory; MVP: localStorage)
- HttpInterceptor adds Authorization header

2) Backend (Spring Boot)
- REST controllers
- Security (JWT)
- Service layer (business rules)
- Repositories (JPA)
- Flyway migrations

3) Database (MySQL)
- users, decks, cards, review_logs tables

## 4. Backend Modules (packages)
Recommended feature-oriented structure inside base package be.intec.linguacards:

- auth
    - controller, dto, service, security helpers
- deck
    - controller, dto, service, repository
- card
    - controller, dto, service, repository
- training
    - controller, dto, service
- stats
    - controller, dto, service
- common
    - exception, config, util

(Exact packaging can be simplified, but keep separation clear.)

## 5. Layers and Responsibilities

### Controller Layer
- HTTP mapping
- DTO validation
- returns proper HTTP codes
- no business logic

### Service Layer
- business rules
- owner checks (Deck/Card/Training/Stats ownership validation)
- training logic (status mapping + deterministic next-card selection by createdAt)
- transactions

### Repository Layer
- DB access via Spring Data JPA
- query methods for stats and training selection

## 6. Security Model
- JWT authentication
- Endpoints:
    - public: /api/auth/**
    - protected: everything else
- CORS:
    - allow Angular dev server http://localhost:4200 (MVP)

## 7. Error Handling
- GlobalExceptionHandler
- Standard error response structure:
    - timestamp
    - status
    - error
    - message
    - path
    - fieldErrors (for validation)

## 8. Testing Strategy (MVP)
- Integration tests:
    - register/login flow
    - access protected endpoint
    - owner isolation checks (later)
- Unit tests:
    - service logic for status update
    - training selection rules (later)

## 9. Deployment (MVP)
Local development:
- MySQL can run via Docker or local install
- backend runs with Maven
- frontend runs with npm

Optional:
- docker-compose (mysql + backend + frontend)
