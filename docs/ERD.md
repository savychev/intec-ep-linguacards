# LinguaCards data model

The production database is MySQL 8. Flyway migration
[`V1__create_core_schema.sql`](../backend/src/main/resources/db/migration/mysql/V1__create_core_schema.sql)
is the schema source of truth; an H2-specific migration provides equivalent test types.

## Relationships

| Parent  | Relationship | Child         | Foreign key           |
| ------- | ------------ | ------------- | --------------------- |
| `users` | 1 → 0..\*    | `decks`       | `decks.owner_id`      |
| `decks` | 1 → 0..\*    | `cards`       | `cards.deck_id`       |
| `cards` | 1 → 0..\*    | `review_logs` | `review_logs.card_id` |

## `users`

| Column          | MySQL type     | Constraints                         |
| --------------- | -------------- | ----------------------------------- |
| `id`            | `BIGINT`       | primary key, auto increment         |
| `email`         | `VARCHAR(320)` | not null, unique (`uk_users_email`) |
| `password_hash` | `VARCHAR(255)` | not null                            |

## `decks`

| Column          | MySQL type     | Constraints                                |
| --------------- | -------------- | ------------------------------------------ |
| `id`            | `BIGINT`       | primary key, auto increment                |
| `owner_id`      | `BIGINT`       | not null, FK `fk_decks_owner` → `users.id` |
| `name`          | `VARCHAR(120)` | not null                                   |
| `language_code` | `VARCHAR(10)`  | not null                                   |
| `is_private`    | `BOOLEAN`      | not null, default true                     |

Index: `idx_decks_owner_id(owner_id)`.

## `cards`

| Column             | MySQL type      | Constraints                               |
| ------------------ | --------------- | ----------------------------------------- |
| `id`               | `BIGINT`        | primary key, auto increment               |
| `deck_id`          | `BIGINT`        | not null, FK `fk_cards_deck` → `decks.id` |
| `term`             | `VARCHAR(200)`  | not null                                  |
| `definition`       | `VARCHAR(2000)` | not null                                  |
| `example`          | `VARCHAR(500)`  | nullable                                  |
| `cefr`             | `VARCHAR(5)`    | nullable                                  |
| `tags`             | `VARCHAR(200)`  | nullable                                  |
| `next_review_at`   | `DATETIME(6)`   | nullable; null means new                  |
| `last_reviewed_at` | `DATETIME(6)`   | nullable                                  |
| `interval_days`    | `INT`           | not null, default 0                       |

Constraints and indexes:

- `uk_cards_deck_term(deck_id, term)` prevents duplicate terms within one deck. The configured
  `utf8mb4_0900_ai_ci` collation makes this comparison case-insensitive.
- `idx_cards_deck_id(deck_id)` supports deck card access and counts.
- `idx_cards_next_review_at(next_review_at)` supports schedule queries.

## `review_logs`

| Column        | MySQL type    | Constraints                                     |
| ------------- | ------------- | ----------------------------------------------- |
| `id`          | `BIGINT`      | primary key, auto increment                     |
| `card_id`     | `BIGINT`      | not null, FK `fk_review_logs_card` → `cards.id` |
| `rating`      | native `ENUM` | `AGAIN`, `HARD`, `GOOD` or `EASY`               |
| `reviewed_at` | `DATETIME(6)` | not null                                        |

Indexes: `idx_review_logs_card_id(card_id)` and
`idx_review_logs_reviewed_at(reviewed_at)`.

## Lifecycle notes

- MySQL stores timestamps to microsecond precision; Java exposes them as `Instant` values.
- Hibernate validates this schema at startup and does not mutate it.
- The foreign keys do not declare database-level `ON DELETE CASCADE`. Aggregate deletion is
  performed by JPA `cascade = ALL` plus `orphanRemoval = true` inside service transactions.
- Owner isolation is an application-layer rule based on the authenticated JWT, not database row
  security.
