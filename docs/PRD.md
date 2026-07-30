
# LinguaCards - Final Project (Intec)

## 1. Project Goal

LinguaCards is a monolingual flashcard web application for vocabulary learning.

Backend:
- Spring Boot 3
- Java 17
- MySQL
- Flyway
- JWT Authentication

Frontend:
- Angular

The system must demonstrate clean architecture, security, and REST design.

---

## 2. Core Concept

Each user can create language decks.
Each deck contains vocabulary cards.

Important rule:
This is a MONOLINGUAL application.
Definitions and examples must be in the same language as the deck.

No translations.

---

## 3. Entities

### User
- id (Long)
- email (unique)
- passwordHash
- createdAt

### Deck
- id
- name
- language (ENUM)
- createdAt
- owner (User)

### Card
- id
- word
- definition
- exampleSentence
- status (NEW, LEARNING, REVIEW, MASTERED)
- createdAt
- deck (Deck)

### ReviewLog
- id
- card (Card)
- rating (AGAIN, HARD, GOOD, EASY)
- reviewedAt

---

## 4. Functional Requirements

### Authentication
- Register
- Login
- JWT-based security
- Protected endpoints

### Deck Management
- Create deck
- List own decks
- Update deck
- Delete deck
- Owner validation

### Card Management
- Create card inside deck
- Update card
- Delete card
- List cards by deck
- Owner validation

### Training
- Get next card
- Submit rating
- Update card status:
  AGAIN -> LEARNING
  HARD  -> LEARNING
  GOOD  -> REVIEW
  EASY  -> MASTERED

### Statistics
Per deck:
- total cards
- count by status
- total reviews
- reviews today

---

## 5. Non-Functional Requirements

- Layered architecture
- DTO usage
- Validation annotations
- Global exception handling
- Proper HTTP status codes
- Integration testing
- Clean code principles
