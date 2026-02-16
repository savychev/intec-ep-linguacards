
# LinguaCards API Specification

Base path: /api

---

## AUTH

POST /api/auth/register

Request:
{
"email": "test@test.com",
"password": "password"
}

POST /api/auth/login

Response:
{
"token": "jwt-token"
}

---

## DECKS

GET /api/decks
POST /api/decks
GET /api/decks/{id}
PUT /api/decks/{id}
DELETE /api/decks/{id}

---

## CARDS

GET /api/decks/{deckId}/cards
POST /api/decks/{deckId}/cards
PUT /api/cards/{id}
DELETE /api/cards/{id}

---

## TRAINING

GET /api/training/next?deckId=1

POST /api/training/review

Request:
{
"cardId": 1,
"rating": "GOOD"
}

---

## STATS

GET /api/stats/decks/{deckId}

Response example:
{
"totalCards": 20,
"new": 5,
"learning": 7,
"review": 4,
"mastered": 4,
"totalReviews": 120,
"reviewsToday": 8
}
