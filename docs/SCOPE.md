# LinguaCards - Vision & Scope

## Vision
LinguaCards is a monolingual flashcard web application that helps users learn vocabulary by creating decks and reviewing cards through short training sessions with simple spaced-repetition style feedback.

## Problem
Many learners rely on translation-based flashcards, which slows down thinking in the target language. LinguaCards forces monolingual definitions and examples to accelerate direct language processing.

## Target Users
- Language learners (self-study, daily practice)
- Students who want structured vocabulary repetition
- Users who prefer short, frequent training sessions (5-10 minutes)

## MVP Goals (what we must deliver)
1. Secure authentication (register/login) using JWT.
2. Users can manage their own decks (CRUD).
3. Users can manage cards inside a deck (CRUD).
4. Training flow:
    - get next card
    - submit rating (AGAIN/HARD/GOOD/EASY)
    - store review history
    - update card status
5. Deck statistics:
    - total cards
    - cards by status
    - total reviews
    - reviews today

## Non-Goals / Out of Scope (explicitly NOT in MVP)
- Sharing decks with other users
- Admin panel / roles beyond normal user
- Real spaced repetition scheduling algorithm (SM-2 etc.)
- Offline mode
- Mobile app
- Import/export (CSV/Anki)
- Full text search, tags, images/audio
- Social features (likes, comments, public decks)

## Success Criteria (for evaluation/demo)
- A user can register, login, and access protected endpoints.
- A user can create a deck, add cards, run training, and see stats.
- Owner isolation works: user A cannot access user B data.
- Flyway migrations + MySQL work reliably.
- At least one integration test covers auth + protected endpoint.
