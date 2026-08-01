# LinguaCards backend

Spring Boot 4.0.7 REST API for authentication, personal decks and cards, review scheduling and
deck statistics.

## Verify

```bash
./mvnw verify
```

The test profile uses H2 with its own Flyway migration and validates the schema against the JPA
entities.

## Run for development

```bash
export DB_USERNAME=root
export DB_PASSWORD=your-mysql-password
export JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
./mvnw spring-boot:run
```

The complete configuration and Docker Compose path are documented in the
[project README](../README.md).
