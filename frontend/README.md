# LinguaCards frontend

Angular 21 single-page application for authentication, deck/card management, training and deck
statistics.

## Development

Node.js 22 is the CI baseline.

```bash
npm ci
npm start
```

The development server opens on <http://localhost:4200> and calls the backend at
<http://localhost:8080>.

## Verify

```bash
npm test -- --watch=false
npm run build
npm run e2e:check
```

The browser happy path requires the root Docker Compose stack:

```bash
cd ..
docker compose up --build --wait
cd frontend
npx playwright install --with-deps chromium
npm run e2e
```

See the [project README](../README.md) for application configuration and the complete workflow.
