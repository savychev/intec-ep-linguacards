# 🃏 LinguaCards

<p>
  <a href="https://github.com/savychev/intec-ep-linguacards/actions/workflows/ci.yml"><img src="https://github.com/savychev/intec-ep-linguacards/actions/workflows/ci.yml/badge.svg" alt="CI"></a>
  <img src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.3-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 4.0.3">
  <img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?logo=springsecurity&logoColor=white" alt="Spring Security JWT">
  <img src="https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Angular-DD0031?logo=angular&logoColor=white" alt="Angular">
</p>

A **monolingual flashcard** web application for vocabulary learning — final project
(*eindproject*) for the Intec **Java Developer EE** programme. Definitions and examples stay
in the **same language** as the deck (no translations), to train direct thinking in the
target language.

> 🇳🇱 Een **eentalige flashcard-webapp** om woordenschat te leren — eindproject van de
> Intec-opleiding **Java Ontwikkelaar EE**. Definities en voorbeelden blijven in **dezelfde
> taal** als de deck (geen vertalingen), om rechtstreeks denken in de doeltaal te trainen.

---

## ✅ Features (backend MVP)

- 🔐 **Auth** — register & login with **JWT** (Spring Security, stateless)
- 🗂️ **Decks & cards** — full CRUD, owner isolation (users only see their own data)
- 🎯 **Training flow** — get next card, rate it (`AGAIN / HARD / GOOD / EASY`),
  review history (`ReviewLog`), card status updates
- 📊 **Deck statistics** — totals, cards by status, reviews today
- ⚠️ **Consistent API errors** — global exception handling with typed exceptions
- 🩺 Health endpoint; DTOs for every request/response

**Frontend:** Angular SPA with working authentication, protected routing and personal deck CRUD.
Card, training and statistics screens are in development.

## 🧱 Tech stack

Java 17 · Spring Boot 4.0.3 · Spring Web · Spring Security (JWT) · Spring Data JPA ·
MySQL · Flyway · Bean Validation · JUnit · Maven | Angular · TypeScript

## 📂 Structure

```
backend/    Spring Boot REST API
  src/main/java/be/intecbrussel/linguacards/
    controller/   Auth · Deck · Card · Training · DeckStats · Me · Health
    service/      business logic (Deck, Card, Training, Stats, User)
    repository/   Spring Data JPA repositories
    entity/       User · Deck · Card · ReviewLog · ReviewRating
    security/     JWT token service
    config/       SecurityConfig · JwtProperties · PasswordConfig
    exception/    GlobalExceptionHandler + typed exceptions
frontend/   Angular SPA (features: auth · decks · train)
docs/       PRD · scope · architecture · ERD · UML · user stories · code reviews
```

## ▶️ Getting started

**Backend** — requires Java 17+, MySQL 8+ and a JWT signing secret:

```sql
CREATE DATABASE linguacards;
```

```bash
cd backend
export DB_USERNAME=root
export DB_PASSWORD=your-mysql-password
export JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
export CORS_ALLOWED_ORIGINS=http://localhost:4200
./mvnw spring-boot:run
```

PowerShell:

```powershell
cd backend
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-mysql-password"
$env:JWT_SECRET = "replace-with-a-random-secret-of-at-least-32-characters"
$env:CORS_ALLOWED_ORIGINS = "http://localhost:4200"
.\mvnw.cmd spring-boot:run
```

The default database URL is `jdbc:mysql://localhost:3306/linguacards`. Override it with
`DB_URL` when needed. Flyway creates or upgrades the schema at startup, and Hibernate validates
that it still matches the JPA entities. `JWT_SECRET` intentionally has no default and must not
be committed. `CORS_ALLOWED_ORIGINS` is a comma-separated allowlist for browser clients; it
defaults only to the Angular development origin.

For a local database previously created by Hibernate, back it up and recreate it before the
first Flyway-managed startup. If the existing schema and `V1` have been manually verified as
equivalent, Flyway can instead be baselined explicitly; do not enable baselining blindly because
it skips applying the initial migration.

**Frontend** — requires Node.js:

```bash
cd frontend
npm install
npm start
```

> ⚠️ Configuration values in `application.yml` are development defaults —
> use environment variables for anything beyond local development.

## 🧪 Verification

```bash
cd backend
./mvnw verify

cd ../frontend
npm ci
npm test -- --watch=false
npm run build
```

Backend tests apply the H2 Flyway migration, validate it against the JPA entities and use a
test-only JWT key. GitHub Actions runs the backend and frontend verification jobs for every pull
request and every push to `main`.

## 📚 Documentation

Design & planning docs in [`docs/`](docs): PRD, scope, architecture, domain model, ERD,
user stories, UX notes, UML diagrams (PlantUML) and backend code reviews under
[`docs/analysis/`](docs/analysis). The current delivery plan is tracked in
[`docs/PLAN.md`](docs/PLAN.md), with actionable work in [`docs/TASKS.md`](docs/TASKS.md).

## 📜 History

This repository consolidates the project's full development history: the original scaffold,
the iterative backend development repo and the v2 rebuild (backend + Angular frontend).
