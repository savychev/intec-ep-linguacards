# LinguaCards - API Contract (Detailed)

Base URL: /api
Auth: JWT in header:
Authorization: Bearer <token>

## Common Responses

### Error Response (standard)
{
"timestamp": "2026-02-16T20:00:00",
"status": 400,
"error": "Bad Request",
"message": "Validation failed",
"path": "/api/decks",
"fieldErrors": [
{"field": "name", "message": "must not be blank"}
]
}

### Status codes conventions
- 200 OK: successful read/update
- 201 Created: successful create
- 204 No Content: successful delete
- 400 Bad Request: validation errors
- 401 Unauthorized: missing/invalid JWT
- 404 Not Found: resource not found or not owned by the authenticated user
- 409 Conflict: unique constraint violations (email or a duplicate term in one deck)

---

## AUTH

### POST /api/auth/register
Request DTO:
{
"email": "user@example.com",
"password": "Password123"
}

Rules:
- email required, valid format
- password required, min length (MVP: 6)
  Responses:
- 201 Created (or 200 OK)
- 409 if email already exists

### POST /api/auth/login
Request DTO:
{
"email": "user@example.com",
"password": "Password123"
}

Response DTO:
{
"message": "ok",
"accessToken": "<jwt>",
"tokenType": "Bearer"
}

Responses:
- 200 OK with token
- 401 Unauthorized if invalid credentials

---

## DECKS

### GET /api/decks
Response DTO (list):
[
{
"id": 1,
"name": "Dutch A2",
"languageCode": "nl",
"isPrivate": true
}
]

### POST /api/decks
Request DTO:
{
"name": "Dutch A2",
"languageCode": "nl",
"isPrivate": true
}

Response DTO:
{
"id": 1,
"name": "Dutch A2",
"languageCode": "nl",
"isPrivate": true
}

### GET /api/decks/{id}
Returns deck if owned.

### PUT /api/decks/{id}
Request DTO:
{
"name": "Dutch A2 Updated",
"languageCode": "nl",
"isPrivate": true
}

### DELETE /api/decks/{id}
Deletes deck (and its cards).

---

## CARDS

### GET /api/decks/{deckId}/cards
Response DTO:
[
{
"id": 10,
"deckId": 1,
"term": "gezellig",
"definition": "pleasant and cozy social atmosphere",
"example": "Het was een gezellige avond.",
"cefr": "B1",
"tags": "social, adjective"
}
]

### POST /api/decks/{deckId}/cards
Request DTO:
{
"term": "gezellig",
"definition": "pleasant and cozy social atmosphere",
"example": "Het was een gezellige avond.",
"cefr": "B1",
"tags": "social, adjective"
}

Rules:
- `term` and `definition` are required
- `example`, `cefr` and `tags` are optional
- `term` is unique within a deck, ignoring letter case
- maximum lengths: `term` 200, `definition` 2000, `example` 500, `cefr` 5, `tags` 200

### GET /api/cards/{id}
Returns the card if its deck is owned by the authenticated user.

### PUT /api/cards/{id}
Request DTO (same fields as create)

### DELETE /api/cards/{id}

---

## TRAINING

### POST /api/decks/{deckId}/train/start

### POST /api/decks/{deckId}/train/next

Owner isolation:
- `deckId` must belong to the authenticated user; otherwise return 404 Not Found.

Response DTO when a card is available:
{
"hasCard": true,
"card": {
  "cardId": 10,
  "deckId": 1,
  "term": "gezellig",
  "definition": "pleasant and cozy social atmosphere",
  "example": "Het was een gezellige avond.",
"lastReviewedAt": null,
"nextReviewAt": null,
"intervalDays": 0
}
}

The three scheduling fields are nullable/zero for a card that has not been reviewed yet.

Response DTO when no card is available:
{
"hasCard": false,
"reason": "EMPTY_DECK"
}

`reason` is `EMPTY_DECK` when the deck has no cards, or `NO_CARDS_TO_TRAIN` when every card is
scheduled for a future review.

Selection rule (MVP):
- return the oldest card that has never been reviewed first
- otherwise return the earliest card whose `nextReviewAt` is due
- return `NO_CARDS_TO_TRAIN` when no card is currently due

### POST /api/cards/{cardId}/review
Request DTO:
{
"rating": "GOOD"
}

Owner isolation:
- `cardId` must belong to a deck owned by the authenticated user; otherwise return 404 Not Found.

Rules:
- `rating` is one of `AGAIN`, `HARD`, `GOOD`, `EASY`
- create a `ReviewLog`
- set the next interval to 1, 3, 7 or 14 days respectively

Response DTO:
{
"cardId": 10,
"deckId": 1,
"term": "gezellig",
"definition": "pleasant and cozy social atmosphere",
"example": "Het was een gezellige avond.",
"lastReviewedAt": "2026-02-16T20:00:00Z",
"nextReviewAt": "2026-02-23T20:00:00Z",
"intervalDays": 7
}

---

## STATS

### GET /api/decks/{deckId}/stats
Owner isolation:
- `deckId` must belong to the authenticated user; otherwise return 404 Not Found.

Response DTO:
{
"deckId": 1,
"totalCards": 20,
"newCards": 5,
"dueCards": 7,
"scheduledCards": 8
}

---

## Business Rules Traceability (MVP)
- BR-2 Owner Isolation -> all Deck/Card/Training/Stats endpoints verify ownership.
- BR-4 Review scheduling -> POST /api/cards/{cardId}/review (rating-to-interval mapping).
- BR-5 Training selection -> POST /api/decks/{deckId}/train/start and `/train/next`.
- BR-6 Statistics -> GET /api/decks/{deckId}/stats.
