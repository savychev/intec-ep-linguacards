# LinguaCards product requirements

## Product goal

LinguaCards is a monolingual flashcard application for vocabulary learners. A learner describes a
term in the same language as its deck and practices recalling that meaning without translating it.
The application is the final project for the Intec Java Developer EE programme.

## Target workflow

1. Register and sign in.
2. Create a private language deck.
3. Add vocabulary cards with a term and definition plus optional example, CEFR level and tags.
4. Start training, reveal the answer and rate recall as `AGAIN`, `HARD`, `GOOD` or `EASY`.
5. Inspect how many cards are new, due now or scheduled for later.

## Implemented functional requirements

### Authentication

- Registration accepts a unique email and a 6–72 character password.
- Passwords are stored only as BCrypt hashes.
- Login returns a short-lived HS256 JWT.
- Protected UI routes restore a valid session from local storage and redirect unauthenticated or
  expired sessions to login.
- The API derives user identity exclusively from the validated JWT.

### Decks

- Create, list, view, update and delete personal decks.
- A deck has a name, free-form language code and `isPrivate` flag.
- Only the owner can see or change a deck.
- Deleting a deck removes its cards and review history through JPA cascades.

### Cards

- Create and list cards inside an owned deck; view, update and delete by card ID.
- Required fields: `term`, `definition`.
- Optional fields: `example`, `cefr`, `tags`.
- A term is unique within its deck, ignoring case under the MySQL collation and service check.
- The UI explains the monolingual convention; automatic language detection is not attempted.

### Training

- Start or continue training for an owned deck.
- Show the term before revealing its definition and example.
- Select an unreviewed card first, otherwise the earliest currently due card.
- Distinguish an empty deck from a non-empty deck with nothing due.
- Store one immutable review log for every submitted rating.
- Schedule the next review after 1, 3, 7 or 14 days for `AGAIN`, `HARD`, `GOOD` or `EASY`.
- Prevent duplicate review submission in the UI if loading the next card fails after a successful
  review.

### Statistics

- Return and display `totalCards`, `newCards`, `dueCards` and `scheduledCards` per owned deck.
- Show empty, ready-to-train and caught-up states with an appropriate next action.

## Quality requirements

- Java 17 and Spring Boot 4 REST backend with DTO validation and standard JSON errors.
- Stateless Spring Security with explicit CORS and owner isolation.
- MySQL schema managed by immutable Flyway migrations and checked by Hibernate validation.
- Angular SPA with typed API services, strict templates and accessible form/status semantics.
- Automated backend, frontend and full-stack browser coverage in GitHub Actions.
- Reproducible multi-container startup with health checks and external secrets.

## Deliberate non-goals for v1.0

- Deck sharing, public discovery, roles or an admin panel.
- A dynamic spaced-repetition algorithm such as SM-2; v1 uses fixed rating intervals.
- Refresh tokens, server-side logout or multi-device session management.
- Import/export, offline mode or native mobile clients.
- Audio, images, automatic translation or language detection.
- Search and filtering beyond the stored CEFR/tag metadata.

## Release acceptance

The first portfolio release is acceptable when a clean checkout can start through Docker Compose,
the browser happy path passes from registration to statistics, owner isolation and schema creation
are protected by backend tests, the dependency/security audit has no unresolved release blocker,
and the documentation matches the shipped contracts.
