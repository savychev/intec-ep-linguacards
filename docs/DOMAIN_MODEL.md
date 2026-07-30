# LinguaCards - Domain Model & Business Rules

## 1. Domain Overview
LinguaCards domain consists of users who own decks. Decks contain cards. Training produces review events (ReviewLog) and updates card status.

## 2. Aggregate Boundaries (simplified)
- User is the owner boundary for all data.
- Deck is the container boundary for cards.
- Card is updated by training reviews.

## 3. Entities and Relationships
- User 1..* Deck
- Deck 1..* Card
- Card 1..* ReviewLog

Ownership rule:
- A deck belongs to exactly one user.
- A card belongs to exactly one deck.
- A review log belongs to exactly one card.

## 4. Invariants (must always be true)
1) Email is unique.
2) Only the owner can access/update/delete their decks and cards.
3) A card cannot exist without a deck.
4) A review log cannot exist without a card.
5) Card status is one of: NEW, LEARNING, REVIEW, MASTERED.

## 5. Business Rules

### BR-1 Authentication
- Users authenticate via JWT.
- All endpoints are protected except /api/auth/**.

### BR-2 Owner Isolation
- Any request that accesses Deck/Card/Stats/Training must verify ownership.
- Non-owner access should return 403 or 404 (team decision; MVP may prefer 404 to reduce information leakage).

### BR-3 Deck language
- Deck has a language (enum), e.g. EN, NL, FR, PT.
- Cards in a deck are monolingual (definition and example must be filled).
- Language detection is out of scope (MVP), so validation is "non-empty" only.

### BR-4 Card Status lifecycle (MVP)
- On card creation: status = NEW
- During training rating:
    - AGAIN -> LEARNING
    - HARD  -> LEARNING
    - GOOD  -> REVIEW
    - EASY  -> MASTERED

### BR-5 Training card selection (MVP)
When requesting next card for a deck:
1) Prefer cards with status NEW
2) Then status LEARNING
3) Then status REVIEW
4) Exclude MASTERED
   If there are multiple candidates, pick the oldest created first (simple deterministic ordering).

### BR-6 Statistics
Stats are computed per deck:
- totalCards
- count by status
- totalReviews
- reviewsToday (based on server local date)

## 6. Glossary
- Deck: a collection of cards for one language
- Card: a vocabulary item with definition and example
- Training: reviewing cards and submitting ratings
- Rating: AGAIN/HARD/GOOD/EASY feedback
- Status: NEW/LEARNING/REVIEW/MASTERED internal state
