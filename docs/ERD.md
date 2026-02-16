# LinguaCards - Data Model (ERD)

## 1. Overview
Database: MySQL
Migration tool: Flyway

Tables:
- users
- decks
- cards
- review_logs

Relationships:
- users (1) -> (many) decks
- decks (1) -> (many) cards
- cards (1) -> (many) review_logs

---

## 2. Tables

### 2.1 users
| Column        | Type           | Constraints                         |
|--------------|----------------|-------------------------------------|
| id           | BIGINT         | PK, auto increment                  |
| email        | VARCHAR(255)   | NOT NULL, UNIQUE                    |
| password_hash| VARCHAR(255)   | NOT NULL                            |
| created_at   | DATETIME       | NOT NULL                            |

Indexes:
- ux_users_email (unique)

---

### 2.2 decks
| Column      | Type           | Constraints                               |
|------------|----------------|-------------------------------------------|
| id         | BIGINT         | PK, auto increment                        |
| user_id    | BIGINT         | NOT NULL, FK -> users(id)                 |
| name       | VARCHAR(120)   | NOT NULL                                  |
| language   | VARCHAR(10)    | NOT NULL (enum stored as string)          |
| created_at | DATETIME       | NOT NULL                                  |

Indexes:
- ix_decks_user_id
- ix_decks_user_id_created_at (optional)

FK:
- fk_decks_user_id: decks.user_id -> users.id
  On delete:
- CASCADE (recommended in MVP) or RESTRICT + manual delete

---

### 2.3 cards
| Column          | Type           | Constraints                               |
|----------------|----------------|-------------------------------------------|
| id             | BIGINT         | PK, auto increment                        |
| deck_id        | BIGINT         | NOT NULL, FK -> decks(id)                 |
| word           | VARCHAR(120)   | NOT NULL                                  |
| definition     | TEXT           | NOT NULL                                  |
| example_sentence | TEXT         | NOT NULL                                  |
| status         | VARCHAR(20)    | NOT NULL (NEW/LEARNING/REVIEW/MASTERED)   |
| created_at     | DATETIME       | NOT NULL                                  |

Indexes:
- ix_cards_deck_id
- ix_cards_deck_id_status
- ix_cards_deck_id_created_at (for deterministic ordering)

FK:
- fk_cards_deck_id: cards.deck_id -> decks.id
  On delete:
- CASCADE (recommended)

---

### 2.4 review_logs
| Column      | Type           | Constraints                               |
|------------|----------------|-------------------------------------------|
| id         | BIGINT         | PK, auto increment                        |
| card_id    | BIGINT         | NOT NULL, FK -> cards(id)                 |
| rating     | VARCHAR(10)    | NOT NULL (AGAIN/HARD/GOOD/EASY)           |
| reviewed_at| DATETIME       | NOT NULL                                  |

Indexes:
- ix_review_logs_card_id
- ix_review_logs_reviewed_at
- ix_review_logs_card_id_reviewed_at (useful for stats)

FK:
- fk_review_logs_card_id: review_logs.card_id -> cards.id
  On delete:
- CASCADE (recommended)

---

## 3. Notes (Implementation Decisions)
- Store enums as strings (readable, stable for MVP).
- Use DATETIME for timestamps.
- Owner isolation is enforced in service layer, not via DB row-level security.
- "reviewsToday" can be computed by filtering review_logs.reviewed_at by current date range.
