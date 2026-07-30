# 🃏 LinguaCards

<p>
  <img src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3.4">
  <img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?logo=springsecurity&logoColor=white" alt="Spring Security JWT">
  <img src="https://img.shields.io/badge/Flyway-CC0200?logo=flyway&logoColor=white" alt="Flyway">
  <img src="https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white" alt="MySQL">
</p>

A **monolingual flashcard** web application for vocabulary learning — final project for the
Intec *Java Developer EE* programme. Definitions and examples stay in the **same language** as
the deck (no translations), to train direct thinking in the target language.

This repository contains the **Spring Boot backend** (REST + JWT auth). An Angular frontend is
planned. Package root: `be.intec.linguacards`.

> 🇳🇱 Een **eentalige flashcard-webapp** om woordenschat te leren (Intec-eindproject). Definities
> en voorbeelden blijven in **dezelfde taal** als de deck — geen vertalingen. Deze repo bevat de
> **Spring Boot backend** (REST + JWT). Een Angular-frontend is gepland.

---

## ✅ Status

Backend foundation — **implemented**:

- 🔐 **Authentication** — register & login with **JWT** (`/api/auth/register`, `/api/auth/login`)
- 🛡️ **Spring Security** — stateless JWT filter, `CustomUserDetailsService`, protected endpoints
  (e.g. `GET /api/health/secure`)
- 🗄️ **Flyway** migration (`V1__init.sql`) on **MySQL**; JPA runs in `validate` mode
- ⚠️ **Global exception handling** — consistent JSON error responses (`GlobalExceptionHandler`)
- 🧪 **Integration test** (`AuthIntegrationTest`) covering register → login → protected endpoint, on H2

Planned (documented in [`docs/`](docs)): decks & cards CRUD, training flow
(rate `AGAIN/HARD/GOOD/EASY`, review history, card status), deck statistics, Angular frontend.

## 🧱 Tech stack

Java 17 · Spring Boot 3.4 · Spring Web · Spring Security · Spring Data JPA · Bean Validation ·
Flyway · MySQL · Lombok · JJWT · JUnit 5 (H2 for tests) · Maven

## 📂 Structure

```
backend/
  src/main/java/be/intec/linguacards/
    auth/        register/login, JWT issuing (controller · service · dto · repository)
    security/    JWT filter, SecurityConfig, JwtService, CustomUserDetailsService
    config/      security beans
    exception/   ApiException, ErrorResponse, GlobalExceptionHandler
    health/      protected health endpoint
  src/main/resources/
    application.yml
    db/migration/V1__init.sql
docs/            PRD, SCOPE, ARCHITECTURE, DOMAIN_MODEL, ERD, USER_STORIES, UML (PlantUML)
frontend/        (planned: Angular)
```

## ▶️ Getting started

**Requirements:** Java 17+, Maven, a running MySQL.

1. Create the database and adjust credentials in
   [`backend/src/main/resources/application.yml`](backend/src/main/resources/application.yml):

   ```sql
   CREATE DATABASE linguacards;
   ```

2. Build & run:

   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. Register and log in:

   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"me@example.com","password":"secret123"}'
   ```

   Use the returned JWT as `Authorization: Bearer <token>` to call `GET /api/health/secure`.

4. Run tests:

   ```bash
   cd backend && mvn test
   ```

> ⚠️ The credentials and JWT secret in `application.yml` are **development defaults only**.
> Use environment variables / externalised config for any real deployment.

## 📚 Documentation

Design and planning docs live in [`docs/`](docs): product requirements (`PRD.md`), scope
(`SCOPE.md`), architecture (`ARCHITECTURE.md`), domain model & ERD, user stories, and UML
diagrams under `docs/uml/`.
