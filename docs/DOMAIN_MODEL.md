# LinguaCards domain model and business rules

## Entities and ownership

- A `User` owns zero or more `Deck` entities.
- A `Deck` contains zero or more `Card` entities.
- A `Card` has zero or more immutable `ReviewLog` entries.
- `Deck` is the ownership boundary for cards, training and statistics.

The database relationships are many-to-one from deck to user, card to deck and review log to card.
JPA cascades delete children when the aggregate parent is deleted.

## Invariants

1. User email is non-blank, valid, at most 320 characters and globally unique.
2. A deck always has one owner, a 1–120 character name and a 2–10 character language code.
3. A card always belongs to one deck and has a non-blank term and definition.
4. Term is unique within a deck without case distinction.
5. A review log always belongs to one card and contains one `ReviewRating` and timestamp.
6. The authenticated user must own the deck behind every deck, card, training or statistics
   operation.

## Card content

| Field        | Required | Maximum | Meaning                                   |
| ------------ | -------- | ------: | ----------------------------------------- |
| `term`       | yes      |     200 | Vocabulary item shown before reveal       |
| `definition` | yes      |    2000 | Monolingual meaning shown after reveal    |
| `example`    | no       |     500 | Example sentence                          |
| `cefr`       | no       |       5 | Learner-supplied CEFR label               |
| `tags`       | no       |     200 | Learner-supplied comma-separated metadata |

The deck language and UI guidance express the monolingual rule. The backend validates structure
and length, not the natural language of user-entered text.

## Review scheduling

Review state is derived from timestamps rather than a status enum:

- `new`: `nextReviewAt` is null;
- `due`: `nextReviewAt` is at or before the current instant;
- `scheduled`: `nextReviewAt` is after the current instant.

When training requests a card:

1. Return the unreviewed card with the lowest ID, if one exists.
2. Otherwise return the due card with the earliest `nextReviewAt`, breaking a tie by ID.
3. Return `EMPTY_DECK` when the deck has no cards.
4. Return `NO_CARDS_TO_TRAIN` when every card is scheduled for later.

Submitting a rating updates the card and creates a `ReviewLog` in one transaction:

| Rating  | `intervalDays` | Next review              |
| ------- | -------------: | ------------------------ |
| `AGAIN` |              1 | review instant + 1 day   |
| `HARD`  |              3 | review instant + 3 days  |
| `GOOD`  |              7 | review instant + 7 days  |
| `EASY`  |             14 | review instant + 14 days |

`lastReviewedAt`, `nextReviewAt` and `intervalDays` describe the latest schedule; review logs
preserve the event history.

## Statistics

Statistics are calculated for one owned deck at the current instant:

- `totalCards`: every card in the deck;
- `newCards`: `nextReviewAt` is null;
- `dueCards`: non-null `nextReviewAt` is at or before now;
- `scheduledCards`: `nextReviewAt` is after now.

The three state counts form a complete, non-overlapping partition of `totalCards`.

## Privacy behavior

An owner mismatch is intentionally exposed as `404 Not Found`, the same as an unknown ID. The
`isPrivate` property is persisted for future product evolution, but v1 has no sharing or public
deck endpoint; all decks are accessible only to their owner.
