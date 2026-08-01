# Changelog

Notable user-facing and release-readiness changes are recorded here.

## [1.0.0] - 2026-08-01

### Added

- JWT registration/login and owner-isolated deck/card management.
- Review flow with `AGAIN`, `HARD`, `GOOD` and `EASY` scheduling plus deck statistics.
- Angular UI covering the complete registration-to-statistics workflow.
- Flyway-managed MySQL/H2 schemas and backend integration coverage.
- Docker Compose stack with MySQL, Spring Boot, NGINX and health checks.
- Real Playwright happy path and reproducible product screenshots.

### Changed

- Reconciled the API, architecture, domain, ERD, UX and UML documentation with shipped code.
- Updated the release baseline to Spring Boot 4.0.7 and Angular 21.2.19 LTS.

### Security

- Added JWT/CORS/error-contract regression tests and removed anonymous debug access.
- Required external production secrets and non-root application containers.
- Pinned GitHub Actions to immutable commits and added production npm audit, Trivy and Dependabot.
- Recorded the release review and accepted development-only residual risk in
  [`docs/SECURITY_AUDIT.md`](docs/SECURITY_AUDIT.md).

[1.0.0]: https://github.com/savychev/intec-ep-linguacards/releases/tag/v1.0.0
