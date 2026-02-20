# EPlinguaCards

Spring Boot + MySQL backend for language-learning flashcards.

## Documentation

- Analysis: [`/docs/analysis/README.md`](docs/analysis/README.md)
- UML diagrams: [`/docs/uml/`](docs/uml/)

UML diagrams are authored in PlantUML (`*.puml`) format.

## Environment variables

The backend reads sensitive values from environment variables (with local defaults for development):

- `DB_URL` — datasource JDBC URL.
- `DB_USERNAME` — datasource username.
- `DB_PASSWORD` — datasource password.
- `JWT_SECRET` — JWT signing secret (use a strong value, 32+ chars).

For local overrides, use `src/main/resources/application-local.example.yml` as a template and create your own `application-local.yml`.

## Run backend locally

1. Start MySQL and create database `linguacards`.
2. Export environment variables if you want to override defaults (recommended for secrets).
3. Run the app:

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`. For Angular dev server (`http://localhost:4200`) CORS is enabled by default via `app.cors.allowed-origin`.

## Run frontend locally (Angular)

1. Move to frontend folder:

```bash
cd frontend
```

2. Install dependencies:

```bash
npm install
```

3. Start dev server:

```bash
npm start
```

Frontend runs on `http://localhost:4200` and calls backend at `http://localhost:8080/api`.
