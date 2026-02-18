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
  - `POST /api/auth/logout`
- Manage Decks (CRUD)
  - `GET /api/decks`
  - `POST /api/decks`
  - `PUT /api/decks/{deckId}`
  - `DELETE /api/decks/{deckId}`
- Manage Cards (CRUD)
  - `GET /api/decks/{deckId}/cards`
  - `POST /api/decks/{deckId}/cards`
  - `PUT /api/cards/{cardId}`
  - `DELETE /api/cards/{cardId}`
- Start Training Session
  - `POST /api/training/sessions`
- Rate Card (Again/Hard/Good/Easy)
  - `POST /api/training/reviews`
- View Statistics
  - `GET /api/stats/overview`
