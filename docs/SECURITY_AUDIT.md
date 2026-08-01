# v1.0 release security audit

Audited: 2026-08-01

This is a release-readiness dependency and repository review, not a penetration test or a claim of
production certification.

## Remediation in the release candidate

- Angular was updated from 21.2.0 to the supported 21.2.19 LTS patch. This clears the high-severity
  Angular advisories reported against the old lockfile.
- Spring Boot was updated within its existing line from 4.0.3 to 4.0.7.
- Every GitHub Action is pinned to a full commit SHA. Dependabot still tracks the human-readable
  major-version comments and opens update pull requests.
- Trivy Action v0.36.0 is pinned to commit
  [`ed142fd`](https://github.com/aquasecurity/trivy-action/commit/ed142fd0673e97e23eac54620cfb913e5ce36c25),
  and that action in turn pins its setup/cache actions. The scanner binary is fixed at v0.70.0.
- Weekly Dependabot checks cover Maven, npm, GitHub Actions and both Dockerfiles.
- Both runtime Dockerfiles explicitly select non-root users; the frontend repeats the upstream
  unprivileged NGINX image's UID/GID `101:101` so the property is visible to static policy scans.

## Automated gates

Every pull request and push to `main` now runs:

1. `npm audit --omit=dev --audit-level=high` for shipped JavaScript dependencies.
2. Trivy filesystem scanning for high/critical dependency vulnerabilities, exposed secrets and
   Docker/Compose misconfiguration.
3. A tracked-file check that rejects generated `node_modules`, `dist`, `target`, Playwright report
   and test-result directories.
4. The existing backend, frontend, container health and real-browser test suites.

The Maven dependency graph is resolved before Trivy runs so transitive Java dependencies are
available to the scanner without relying on incomplete `pom.xml` inference.

## Results and residual risk

- [CI run #33](https://github.com/savychev/intec-ep-linguacards/actions/runs/30702742878)
  passed backend verification, 44 frontend tests and production build, the full Compose/Playwright
  path, repository hygiene and Trivy. The first scanner run found the frontend Dockerfile's implicit
  runtime user; the explicit non-root declaration above fixed it without an ignore rule.
- A clean install of the release lockfile reports **0 known vulnerabilities in production npm
  dependencies**.
- The full development tree reports three moderate package entries caused by one transitive
  advisory: Angular CLI → MCP SDK → `@hono/node-server` and
  [GHSA-frvp-7c67-39w9](https://github.com/advisories/GHSA-frvp-7c67-39w9). It concerns encoded
  backslash traversal in a Windows development static-file adapter, is absent from the production
  bundle, and currently has no compatible Angular 21 CLI resolution. It is accepted for v1.0 and
  remains monitored by Dependabot.
- Application secrets have no repository defaults: Compose and production startup require
  external database/JWT values, while tests generate or provide isolated test-only values.
- JWT/CORS/owner isolation, malformed requests and security error envelopes remain covered by
  backend integration tests.

Future releases should reassess the accepted development-only advisory, add dynamic application
security testing if the project is exposed publicly, and scan published container images in their
target registry in addition to the source/build manifests.
