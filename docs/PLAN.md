# LinguaCards delivery plan

Last audited: 2026-07-31

This plan reflects the consolidated repository as it exists today. A checked item means that
the code or infrastructure is present; it does not imply production readiness unless the
milestone explicitly says so.

## Current state

- The full backend MVP is implemented: JWT authentication, personal decks and cards, training,
  review history and deck statistics.
- The Angular application is a buildable scaffold; its feature pages are not implemented yet.
- Backend integration tests cover authentication, data ownership, training and deck statistics;
  the frontend still has two scaffold-level tests.
- Flyway owns schema creation, with Hibernate validation guarding entity/schema drift.

## P0 — reproducible baseline (0.5–1 day)

- [x] Consolidate the useful repositories and their histories into this monorepo.
- [x] Add GitHub Actions jobs for backend verification and frontend test/build.
- [x] Give backend tests an isolated H2 configuration and test-only JWT settings.
- [x] Make the Unix Maven wrapper executable.
- [x] Document required runtime configuration and correct the project status.
- [x] Confirm both CI jobs are green on the baseline pull request.

Exit criterion: a clean checkout can be verified automatically without a developer-specific
database or secret.

## P1 — trustworthy backend (2–4 days)

- [x] Add authentication integration tests (register, login and protected endpoint).
- [x] Add owner-isolation tests for decks and cards.
- [x] Add training-flow and statistics integration tests.
- [x] Add validation and error-contract tests for authentication requests.
- [x] Introduce Flyway migrations for all current entities.
- [x] Replace runtime `ddl-auto=update` with migration-based schema validation.
- [ ] Review JWT configuration, CORS and production secrets.
- [ ] Fix defects exposed by the tests and keep CI green.

Exit criterion: core API behaviour, data isolation and schema creation are protected by tests.

## P2 — usable Angular MVP (4–6 days)

- [ ] Implement login and registration screens.
- [ ] Add token handling, route protection and API error handling.
- [ ] Implement deck and card management screens.
- [ ] Implement the training flow and rating controls.
- [ ] Implement deck statistics.
- [ ] Add focused component and service tests.
- [ ] Provide clear loading, empty and error states.

Exit criterion: a user can complete the main workflow from registration through training in the UI.

## P3 — portfolio release (1–3 days)

- [ ] Add Dockerfiles and Docker Compose for MySQL, backend and frontend.
- [ ] Add one end-to-end happy-path test.
- [ ] Add demo screenshots or a short GIF to the README.
- [ ] Reconcile API, architecture and UML documentation with the final code.
- [ ] Run dependency, security and repository hygiene checks.
- [ ] Tag the first portfolio-ready release.

Exit criterion: the project is easy to run, demonstrate and evaluate from a fresh checkout.

## Definition of done

A task is done when:

- the relevant automated tests exist and pass;
- both CI jobs remain green;
- validation, HTTP status codes and owner isolation are handled where applicable;
- database changes have an immutable migration;
- no secret or machine-specific configuration is committed;
- user-facing behaviour and setup changes are documented.

## Main risks

| Risk | Mitigation |
|---|---|
| Missing regression coverage | Add tests before expanding features. |
| Cross-user data access | Derive ownership from JWT and test negative cases. |
| Schema drift | Adopt Flyway, then use Hibernate validation. |
| Frontend scope growth | Finish one end-to-end MVP workflow before visual extras. |
| Insecure deployment defaults | Require external secrets and define a production profile. |
