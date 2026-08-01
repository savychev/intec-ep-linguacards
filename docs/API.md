# LinguaCards API endpoint index

Base path: `/api`. See [`API_CONTRACT.md`](API_CONTRACT.md) for request/response examples,
validation and scheduling rules.

Except for the three explicitly public routes, requests require
`Authorization: Bearer <accessToken>`.

## Public

| Method | Path                 | Success | Purpose                              |
| ------ | -------------------- | ------- | ------------------------------------ |
| `GET`  | `/api/health`        | `200`   | Container/load-balancer health probe |
| `POST` | `/api/auth/register` | `201`   | Create a user                        |
| `POST` | `/api/auth/login`    | `200`   | Issue a JWT                          |

## Authenticated user

| Method | Path      | Success | Purpose                           |
| ------ | --------- | ------- | --------------------------------- |
| `GET`  | `/api/me` | `200`   | Inspect the current JWT principal |

## Decks

| Method   | Path              | Success | Purpose                        |
| -------- | ----------------- | ------- | ------------------------------ |
| `GET`    | `/api/decks`      | `200`   | List owned decks               |
| `POST`   | `/api/decks`      | `201`   | Create a deck                  |
| `GET`    | `/api/decks/{id}` | `200`   | Get one owned deck             |
| `PUT`    | `/api/decks/{id}` | `200`   | Replace editable deck fields   |
| `DELETE` | `/api/decks/{id}` | `204`   | Delete a deck and its contents |

## Cards

| Method   | Path                        | Success | Purpose                              |
| -------- | --------------------------- | ------- | ------------------------------------ |
| `GET`    | `/api/decks/{deckId}/cards` | `200`   | List cards in an owned deck          |
| `POST`   | `/api/decks/{deckId}/cards` | `201`   | Create a card                        |
| `GET`    | `/api/cards/{cardId}`       | `200`   | Get one card through deck ownership  |
| `PUT`    | `/api/cards/{cardId}`       | `200`   | Replace editable card fields         |
| `DELETE` | `/api/cards/{cardId}`       | `204`   | Delete a card and its review history |

## Training and statistics

| Method | Path                              | Success | Purpose                             |
| ------ | --------------------------------- | ------- | ----------------------------------- |
| `POST` | `/api/decks/{deckId}/train/start` | `200`   | Get the first available card/result |
| `POST` | `/api/decks/{deckId}/train/next`  | `200`   | Get the next available card/result  |
| `POST` | `/api/cards/{cardId}/review`      | `200`   | Save a rating and new schedule      |
| `GET`  | `/api/decks/{deckId}/stats`       | `200`   | Get new/due/scheduled counts        |

Owner mismatch and unknown resource IDs both return `404`. Invalid input returns `400`, missing or
invalid authentication returns `401`, and duplicate email/term conflicts return `409`.
