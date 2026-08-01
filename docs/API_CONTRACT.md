# LinguaCards API contract

Base path: `/api`. Protected routes require:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

## Common behavior

Successful creates return `201`, reads and updates return `200`, and deletes return `204` with no
body. Invalid input returns `400`; missing or invalid authentication returns `401`; unknown or
non-owned resources return `404`; duplicate email/term conflicts return `409`.

Controller, validation and security failures share this envelope:

```json
{
  "timestamp": "2026-08-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "name: must not be blank",
  "path": "/api/decks"
}
```

Malformed JSON and an invalid numeric path ID also return this shape. Validation currently reports
the first field error in `message`; there is no separate `fieldErrors` property.

## Health and current user

### `GET /api/health` (public)

```json
{ "status": "ok" }
```

### `GET /api/me`

```json
{
  "email": "user@example.com",
  "userId": 1,
  "issuer": "http://localhost:8080"
}
```

## Authentication

### `POST /api/auth/register` (public)

```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

- Email: required, valid format, maximum 320 characters, unique.
- Password: required, 6–72 characters.
- Success: `201 Created`; response message is `registered` and token properties are null.
- Duplicate email: `409 Conflict`.

### `POST /api/auth/login` (public)

Request uses the same validated credential shape. Success:

```json
{
  "message": "ok",
  "accessToken": "<jwt>",
  "tokenType": "Bearer"
}
```

Invalid credentials return `401 Unauthorized`.

## Decks

Deck response:

```json
{
  "id": 1,
  "name": "Dutch B2",
  "languageCode": "nl",
  "isPrivate": true
}
```

### `GET /api/decks`

Returns an array containing only decks owned by the current user.

### `POST /api/decks`

### `PUT /api/decks/{id}`

Both accept:

```json
{
  "name": "Dutch B2",
  "languageCode": "nl",
  "isPrivate": true
}
```

- `name`: required, 1–120 characters after trimming.
- `languageCode`: required, 2–10 characters after trimming.
- `isPrivate`: optional; omitted/null defaults to true.

### `GET /api/decks/{id}`

Returns the owned deck.

### `DELETE /api/decks/{id}`

Deletes the deck, its cards and their review history.

## Cards

Card response:

```json
{
  "id": 10,
  "deckId": 1,
  "term": "gezellig",
  "definition": "Warm and pleasant, especially when people are together.",
  "example": "We hadden een gezellige avond.",
  "cefr": "B2",
  "tags": "social, adjective"
}
```

### `GET /api/decks/{deckId}/cards`

Returns all cards in the owned deck.

### `POST /api/decks/{deckId}/cards`

### `PUT /api/cards/{cardId}`

Both accept:

```json
{
  "term": "gezellig",
  "definition": "Warm and pleasant, especially when people are together.",
  "example": "We hadden een gezellige avond.",
  "cefr": "B2",
  "tags": "social, adjective"
}
```

- `term`: required, maximum 200; unique within a deck ignoring case.
- `definition`: required, maximum 2000.
- `example`: optional, maximum 500.
- `cefr`: optional, maximum 5.
- `tags`: optional, maximum 200.
- Required values are trimmed; blank optional values are stored as null.

### `GET /api/cards/{cardId}`

Returns a card only through ownership of its deck.

### `DELETE /api/cards/{cardId}`

Deletes the card and its review history.

## Training

### `POST /api/decks/{deckId}/train/start`

### `POST /api/decks/{deckId}/train/next`

The two routes currently apply the same selection rule. A card result is wrapped so the API can
distinguish empty and caught-up decks without using `204`:

```json
{
  "hasCard": true,
  "reason": null,
  "card": {
    "cardId": 10,
    "deckId": 1,
    "term": "gezellig",
    "definition": "Warm and pleasant, especially when people are together.",
    "example": "We hadden een gezellige avond.",
    "lastReviewedAt": null,
    "nextReviewAt": null,
    "intervalDays": 0
  }
}
```

No-card result:

```json
{
  "hasCard": false,
  "reason": "NO_CARDS_TO_TRAIN",
  "card": null
}
```

`reason` is `EMPTY_DECK` when the deck has no cards and `NO_CARDS_TO_TRAIN` when every card is
scheduled for later. Selection returns the lowest-ID new card first; otherwise it returns the due
card with the earliest `nextReviewAt` and then lowest ID.

### `POST /api/cards/{cardId}/review`

```json
{ "rating": "GOOD" }
```

`rating` must be `AGAIN`, `HARD`, `GOOD` or `EASY`. The operation creates a review log and sets the
next interval to 1, 3, 7 or 14 days respectively. Success returns the updated training card:

```json
{
  "cardId": 10,
  "deckId": 1,
  "term": "gezellig",
  "definition": "Warm and pleasant, especially when people are together.",
  "example": "We hadden een gezellige avond.",
  "lastReviewedAt": "2026-08-01T12:00:00Z",
  "nextReviewAt": "2026-08-08T12:00:00Z",
  "intervalDays": 7
}
```

## Statistics

### `GET /api/decks/{deckId}/stats`

```json
{
  "deckId": 1,
  "totalCards": 20,
  "newCards": 5,
  "dueCards": 7,
  "scheduledCards": 8
}
```

New, due and scheduled counts are mutually exclusive and sum to `totalCards` at the instant of the
request.
