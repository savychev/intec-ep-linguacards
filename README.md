# EPlinguaCards

Spring Boot + MySQL backend for language-learning flashcards.

## Documentation

- Analysis: [`/docs/analysis/README.md`](docs/analysis/README.md)
- UML diagrams: [`/docs/uml/`](docs/uml/)

UML diagrams are authored in PlantUML (`*.puml`) format.

## Run backend locally

1. Start MySQL and create database `linguacards`.
2. Configure credentials in `src/main/resources/application.yml` if needed.
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
