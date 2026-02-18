# LinguaCards - User Stories & Acceptance Criteria (MVP)

## Epic A - Authentication

### US-A1 Register
As a user, I want to register with email and password, so I can create my account.
Acceptance criteria:
- GIVEN I provide a valid email and password
  WHEN I call POST /api/auth/register
  THEN the account is created and I receive success response.
- Email must be unique.
- Password is never stored in plain text (BCrypt).

### US-A2 Login
As a user, I want to login, so I can access my data.
Acceptance criteria:
- GIVEN my credentials are valid
  WHEN I call POST /api/auth/login
  THEN I receive a JWT token.
- GIVEN credentials are invalid
  THEN I receive 401 Unauthorized.

### US-A3 Protected Access
As a user, I want protected endpoints to require authentication, so my data is secure.
Acceptance criteria:
- GIVEN I call a protected endpoint without JWT
  THEN I receive 401 Unauthorized.
- GIVEN I call a protected endpoint with a valid JWT
  THEN I receive 200 OK.

---

## Epic B - Deck Management

### US-B1 Create Deck
As a user, I want to create a deck with name and language, so I can organize vocabulary.
Acceptance criteria:
- GIVEN I am authenticated
  WHEN I POST /api/decks with name + language
  THEN a deck is created and belongs to me.
- Name is required, language is required.

### US-B2 List My Decks
As a user, I want to see all my decks, so I can choose one to study.
Acceptance criteria:
- GIVEN I am authenticated
  WHEN I GET /api/decks
  THEN I see only my decks.

### US-B3 Update Deck
As a user, I want to rename/update a deck, so it stays relevant.
Acceptance criteria:
- GIVEN I own the deck
  WHEN I PUT /api/decks/{id}
  THEN the deck updates.
- GIVEN I do not own the deck
  THEN I receive 403 Forbidden (or 404 Not Found to avoid leaking existence).

### US-B4 Delete Deck
As a user, I want to delete a deck, so I can remove unused content.
Acceptance criteria:
- GIVEN I own the deck
  WHEN I DELETE /api/decks/{id}
  THEN the deck is removed.
- Deleting a deck removes its cards (cascade or manual delete).

---

## Epic C - Card Management

### US-C1 Create Card
As a user, I want to add a card into a deck, so I can learn new words.
Acceptance criteria:
- GIVEN I own the deck
  WHEN I POST /api/decks/{deckId}/cards
  THEN the card is created under that deck.
- word, definition, exampleSentence are required.
- Card status defaults to NEW.

### US-C2 List Cards of Deck
As a user, I want to view cards of a deck, so I can manage them.
Acceptance criteria:
- GIVEN I own the deck
  WHEN I GET /api/decks/{deckId}/cards
  THEN I receive cards of that deck only.

### US-C3 Update Card
As a user, I want to edit a card, so I can fix mistakes.
Acceptance criteria:
- GIVEN I own the card (via deck ownership)
  WHEN I PUT /api/cards/{id}
  THEN the card updates.
- GIVEN I do not own it
  THEN 403/404.

### US-C4 Delete Card
As a user, I want to delete a card, so I can remove irrelevant items.
Acceptance criteria:
- GIVEN I own the card
  WHEN I DELETE /api/cards/{id}
  THEN the card is removed.

### US-C5 Monolingual Rule
As a user, I want the app to enforce monolingual decks, so my learning stays in one language.
Acceptance criteria:
- Deck language is stored on the deck.
- Validation ensures definition and example are not empty and are intended to be in the deck language (MVP: only enforce “non-empty”; language detection is out of scope).
- UI labels clearly state “no translations”.

---

## Epic D - Training

### US-D1 Get Next Card
As a user, I want to get the next card to review, so I can train efficiently.
Acceptance criteria:
- GIVEN I own the deck
  WHEN I GET /api/training/next?deckId=X
  THEN I receive one card.
- Selection rule (MVP):
    - prefer NEW cards first, then LEARNING, then REVIEW
    - ignore MASTERED
    - if multiple candidates exist, pick the oldest created card first (deterministic)
- If no cards available -> 204 No Content (or 200 with null, but prefer 204).

### US-D2 Submit Rating
As a user, I want to rate my recall, so the app tracks progress.
Acceptance criteria:
- GIVEN I submit rating AGAIN/HARD/GOOD/EASY
  WHEN I POST /api/training/review
  THEN:
    - a ReviewLog record is stored
    - card status updates:
      AGAIN -> LEARNING
      HARD  -> LEARNING
      GOOD  -> REVIEW
      EASY  -> MASTERED

---

## Epic E - Statistics

### US-E1 Deck Statistics
As a user, I want to see deck stats, so I can understand progress.
Acceptance criteria:
- GIVEN I own the deck
  WHEN I GET /api/stats/decks/{deckId}
  THEN I receive:
    - totalCards
    - count by status (NEW/LEARNING/REVIEW/MASTERED)
    - totalReviews
    - reviewsToday
- Owner isolation applies.

---

## Error Handling (global)
Acceptance criteria:
- Validation errors return 400 with field-level messages.
- Not found returns 404.
- Unauthorized returns 401.
- Forbidden returns 403 (or 404 to avoid leaking resources).
