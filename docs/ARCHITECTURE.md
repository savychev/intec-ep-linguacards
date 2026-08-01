# LinguaCards architecture

This document describes the implemented system. Historical design reviews remain under
[`analysis/`](analysis/).

## System shape

LinguaCards is a modular monolith with three runtime containers:

1. An Angular single-page application served by unprivileged NGINX.
2. A Spring Boot REST API.
3. A MySQL database managed by Flyway migrations.

In Docker Compose, the browser talks only to `http://localhost:4200`. NGINX serves the SPA,
falls back to `index.html` for client routes and proxies `/api` to the backend. Direct backend
access on port `8080` remains available for API development.

## Backend

The Java code uses a conventional layered package structure under
`be.intecbrussel.linguacards`:

- `controller` maps HTTP requests, validates DTOs and reads the authenticated JWT principal;
- `service` owns transactions, scheduling rules and owner checks;
- `repository` provides Spring Data JPA access and ordering/count queries;
- `entity` contains the four persisted entities and `ReviewRating` enum;
- `dto` isolates the REST contract from persistence entities;
- `security` and `config` implement JWT, CORS and JSON security errors;
- `exception` maps expected failures to the standard API error envelope.

Controllers never accept an owner ID from the client. The authenticated `userId` JWT claim is
passed into the service layer, where deck ownership is required before deck, card, training or
statistics data is returned. A missing resource and a resource owned by another user both produce
`404 Not Found`, which avoids disclosing its existence.

## Frontend

The Angular application is organized by feature:

- `core/auth` stores and validates the access token, attaches it to same-API requests and guards
  protected routes;
- `core/api` contains environment-aware API configuration and the shared error contract;
- `features/auth` implements registration and login;
- `features/decks` implements deck and card CRUD;
- `features/training` implements answer reveal and four review ratings;
- `features/statistics` visualizes the current deck schedule.

Feature pages and their data-access services are lazy-loaded where appropriate. Authentication is
stateless: the JWT is kept in `localStorage`, removed when expired or rejected with `401`, and is
sent only to the configured API while login/register and third-party requests remain token-free.
Logout is client-side token removal. Refresh tokens are out of scope.

## Security boundaries

Public endpoints are limited to:

- `GET /api/health`;
- `POST /api/auth/register`;
- `POST /api/auth/login`.

Every other API endpoint requires an HS256 JWT with the configured issuer. Startup validation
requires an absolute HTTP(S) issuer, a signing secret of at least 32 characters and a positive
expiration. CORS uses an explicit origin allowlist and never enables credentials. Security-filter
failures and controller failures share the same JSON error shape.

## Persistence and scheduling

Flyway owns schema creation for both MySQL and the H2 test database. Hibernate runs with
`ddl-auto=validate` and `open-in-view=false`, so schema drift fails at startup and lazy persistence
access stays inside service transactions.

A card is new while `nextReviewAt` is null. Training selects the oldest new card first; otherwise
it selects the due card with the earliest `nextReviewAt`. A rating creates an immutable
`ReviewLog` and sets the next interval to 1, 3, 7 or 14 days for `AGAIN`, `HARD`, `GOOD` or `EASY`.
Statistics partition a deck into new, due and scheduled cards.

## Verification

- Backend integration tests run against the H2 Flyway migration and cover authentication,
  validation, owner isolation, CRUD, scheduling, statistics, CORS and JSON security errors.
- Frontend unit/component tests cover the shell, auth, HTTP services and every feature page.
- One Playwright happy path runs against the complete Docker Compose stack and verifies
  registration through the resulting statistics.
- GitHub Actions runs backend verification, frontend test/build and container/browser checks for
  each pull request and push to `main`.

## Deployment

Both application images use multi-stage builds and unprivileged runtime users. Compose waits for
MySQL, backend and frontend health checks, stores database data in a named volume and injects all
passwords/signing material through environment variables. Production deployments must replace
the example values and provide their own TLS/reverse-proxy boundary.
