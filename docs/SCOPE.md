# LinguaCards vision and v1 scope

## Vision

LinguaCards helps self-directed language learners build direct associations in a target language.
Instead of a translated answer, each card uses a monolingual definition and optional example. Short
review sessions turn a learner's recall rating into a simple future schedule.

## Delivered v1 scope

- JWT registration/login and protected Angular routes.
- Personal deck CRUD with language metadata.
- Card CRUD with term, definition, example, CEFR and tags.
- Owner isolation for every deck-derived resource.
- Training that reveals the answer before enabling four recall ratings.
- Fixed next-review intervals of 1, 3, 7 and 14 days with review history.
- Per-deck total, new, due and scheduled statistics.
- Consistent loading, empty, retry and error states.
- Flyway-managed MySQL/H2 schemas and Hibernate validation.
- Docker Compose deployment with health checks and same-origin NGINX API proxy.
- Automated backend, frontend and full-stack Chromium verification.

## Explicit non-goals

- Sharing, public decks, social features, roles or administration.
- SM-2 or another adaptive spaced-repetition algorithm.
- Refresh tokens and server-side session revocation.
- Search, bulk editing, CSV/Anki import or export.
- Offline-first/PWA behavior or native mobile clients.
- Audio, images, translation and automatic language detection.

The `isPrivate`, CEFR and tags fields leave room for future evolution, but v1 does not expose
public decks or tag/search workflows.

## Demonstration success criteria

- A fresh checkout starts with documented environment values and one Compose command.
- A learner can register, create a deck/card, review it and see the resulting scheduled count.
- A second user cannot read or mutate the first user's resources.
- MySQL starts from the Flyway migration and Hibernate accepts the resulting schema.
- Backend, frontend and browser tests pass in GitHub Actions without repository secrets.
