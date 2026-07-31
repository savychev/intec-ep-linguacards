# LinguaCards task backlog

The priority order below is the working sequence. Detailed milestone goals and exit criteria are
in [`PLAN.md`](PLAN.md).

## P0 — baseline

- [x] Preserve the histories of the consolidated repositories.
- [x] Add backend and frontend CI jobs.
- [x] Add deterministic backend test configuration.
- [x] Correct the Maven wrapper executable bit.
- [x] Align README and planning documents with the implementation.
- [x] Merge only after both CI jobs pass.
- [x] Archive the superseded `LinguaCards_v2` repository after the merge is verified.

## P1 — backend confidence

- [x] Authentication integration test: register → login → `/api/me`.
- [x] Duplicate-email and invalid-credentials tests.
- [x] Deck CRUD tests, including a second user's forbidden access.
- [x] Card CRUD and duplicate-term tests.
- [x] Training selection and rating-transition tests.
- [x] Deck statistics tests.
- [x] Validation and standard error-response tests.
- [x] Flyway dependency and complete `V1` schema migration.
- [x] Runtime configuration with external secrets and `ddl-auto=validate`.
- [x] JWT, CORS and authorization review.

## P2 — frontend MVP

- [x] Shared API configuration and typed authentication models.
- [x] Authentication service, token interceptor and route guard.
- [x] Login and registration UI.
- [x] Deck list, create, edit and delete UI.
- [ ] Card list, create, edit and delete UI.
- [ ] Training card and rating UI.
- [ ] Statistics view.
- [ ] Loading, empty and error states.
- [ ] Component and service tests for the core workflow.

## P3 — release polish

- [ ] Dockerfile for backend.
- [ ] Dockerfile for frontend.
- [ ] Docker Compose with MySQL health check.
- [ ] Browser-level happy-path test.
- [ ] README screenshots and concise demo instructions.
- [ ] Final documentation and UML reconciliation.
- [ ] Dependency and security scan.
- [ ] Portfolio release tag.
