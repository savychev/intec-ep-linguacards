# Analysis

## MVP Scope
The first release targets a practical language-learning workflow with the following capabilities:

- Authentication: register, login, and logout.
- Deck management: create, read, update, and delete personal decks.
- Card management: create, read, update, and delete cards within decks.
- Training mode: run a training session and rate each shown card.
- Basic statistics: view simple progress and activity metrics.

## Post-MVP Items
Planned extensions after the MVP include:

- Spaced repetition scheduling based on the SM-2 algorithm.
- Import/export support for decks and cards.
- Public deck sharing and discovery.

## Key Constraints
Current product and technical constraints:

- Monolingual definitions: card definitions are expected in one target language context.
- Per-user data isolation: each user can access only their own private data unless explicit sharing is introduced.

## Use Cases to Planned REST Endpoints (High-Level)

- Register
  - `POST /api/auth/register`
- Login
  - `POST /api/auth/login`
- Logout
  - No backend endpoint currently exists.
  - Logout is a **client-side action**: remove JWT from client storage and clear auth state.
- Manage Decks (CRUD)
  - `GET /api/decks`
  - `GET /api/decks/me`
  - `GET /api/decks/{deckId}`
  - `POST /api/decks`
  - `POST /api/decks/me`
  - `PUT /api/decks/{deckId}`
  - `DELETE /api/decks/{deckId}`
- Manage Cards (CRUD)
  - `GET /api/decks/{deckId}/cards`
  - `POST /api/decks/{deckId}/cards`
  - `PUT /api/decks/{deckId}/cards/{cardId}`
  - `DELETE /api/decks/{deckId}/cards/{cardId}`
  - `GET /api/decks/me/{deckId}/cards`
  - `POST /api/decks/me/{deckId}/cards`
  - `PUT /api/decks/me/{deckId}/cards/{cardId}`
  - `DELETE /api/decks/me/{deckId}/cards/{cardId}`
- Start Training Session
  - `GET /api/decks/{deckId}/training`
  - `GET /api/decks/me/{deckId}/training`
- Rate Card (Again/Hard/Good/Easy)
  - `POST /api/decks/{deckId}/cards/{cardId}/review`
  - `POST /api/decks/me/{deckId}/cards/{cardId}/review`
- View Statistics
  - `GET /api/decks/{deckId}/stats`
  - `GET /api/decks/me/{deckId}/stats`
