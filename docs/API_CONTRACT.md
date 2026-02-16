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
- 204 No Content: no next training card
- 400 Bad Request: validation errors
- 401 Unauthorized: missing/invalid JWT
- 403 Forbidden: authenticated but not allowed (or use 404 to not leak existence)
- 404 Not Found: resource not found
- 409 Conflict: unique constraint violations (email)

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
"token": "<jwt>",
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
"language": "NL",
"createdAt": "2026-02-16T20:00:00"
}
]

### POST /api/decks
Request DTO:
{
"name": "Dutch A2",
"language": "NL"
}

Response DTO:
{
"id": 1,
"name": "Dutch A2",
"language": "NL",
"createdAt": "2026-02-16T20:00:00"
}

### GET /api/decks/{id}
Returns deck if owned.

### PUT /api/decks/{id}
Request DTO:
{
"name": "Dutch A2 Updated",
"language": "NL"
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
"word": "gezellig",
"definition": "pleasant and cozy social atmosphere",
"exampleSentence": "Het was een gezellige avond.",
"status": "NEW",
"createdAt": "2026-02-16T20:00:00"
}
]

### POST /api/decks/{deckId}/cards
Request DTO:
{
"word": "gezellig",
"definition": "pleasant and cozy social atmosphere",
"exampleSentence": "Het was een gezellige avond."
}

Rules:
- all fields required (MVP)
- status defaults to NEW

### PUT /api/cards/{id}
Request DTO (same fields as create)

### DELETE /api/cards/{id}

---

## TRAINING

### GET /api/training/next?deckId={deckId}
Response DTO:
{
"id": 10,
"word": "gezellig",
"definition": "pleasant and cozy social atmosphere",
"exampleSentence": "Het was een gezellige avond.",
"status": "NEW"
}

If no card available:
- 204 No Content

### POST /api/training/review
Request DTO:
{
"cardId": 10,
"rating": "GOOD"
}

Rules:
- rating enum: AGAIN, HARD, GOOD, EASY
- create ReviewLog
- update Card.status using MVP mapping:
  AGAIN -> LEARNING
  HARD  -> LEARNING
  GOOD  -> REVIEW
  EASY  -> MASTERED

Response:
- 200 OK (optionally return updated card)

---

## STATS

### GET /api/stats/decks/{deckId}
Response DTO:
{
"totalCards": 20,
"new": 5,
"learning": 7,
"review": 4,
"mastered": 4,
"totalReviews": 120,
"reviewsToday": 8
}
