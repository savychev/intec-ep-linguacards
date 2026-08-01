# LinguaCards user stories and acceptance criteria

## Authentication

### Register

As a learner, I want to create an account so that my vocabulary remains private.

- A valid, unique email and 6–72 character password produce `201 Created`.
- A duplicate email produces `409 Conflict`.
- Only a BCrypt hash is persisted; registration does not create an authenticated session.
- The UI redirects to login and confirms that the account is ready.

### Sign in and protected access

As a learner, I want to sign in once and use protected screens until my token expires.

- Valid credentials return a Bearer JWT; invalid credentials return `401`.
- The UI persists a non-expired token, attaches it only to the configured API and restores the
  session after reload.
- Missing, malformed, expired or rejected tokens redirect to login and are removed locally.
- Logout removes the token and returns to login.

## Decks

### Manage my decks

As a learner, I want to create, edit and remove language decks so that vocabulary stays organized.

- A deck requires a name and language code; privacy defaults to true.
- The deck list contains only the authenticated user's decks.
- Create/update return the saved deck, and confirmed delete returns no content.
- Empty, loading and failed-request states give a clear next action or retry.
- An unknown or non-owned deck returns `404`.

## Cards

### Manage cards inside a deck

As a learner, I want to maintain vocabulary cards so that training content stays accurate.

- A card requires `term` and `definition`; `example`, `cefr` and `tags` are optional.
- Terms are unique within a deck without case distinction.
- The detail screen supports create, edit and confirmed delete without mocks.
- A learner can navigate directly from the deck to training or statistics.
- Every list/create/get/update/delete operation first verifies deck ownership.

### Follow the monolingual convention

As a learner, I want definitions and examples in the deck language so that I practice direct
understanding.

- The deck stores a language code and the form explicitly asks for monolingual content.
- Required text and field lengths are validated.
- Natural-language detection is not part of v1, so content language remains the learner's
  responsibility.

## Training

### Receive the next useful card

As a learner, I want new or due cards in a deterministic order so that each session is predictable.

- Starting/continuing a session returns the lowest-ID new card before any due card.
- When no new card exists, the earliest due time wins, then the lowest ID.
- An empty deck returns `hasCard=false` with `EMPTY_DECK`.
- A caught-up deck returns `hasCard=false` with `NO_CARDS_TO_TRAIN`.
- Unknown and non-owned decks both return `404`.

### Reveal and rate recall

As a learner, I want to reveal the answer and rate recall so that the next review is scheduled.

- Rating controls are unavailable until the answer is revealed.
- `AGAIN`, `HARD`, `GOOD` and `EASY` schedule 1, 3, 7 and 14 days respectively.
- Each accepted rating creates one review log and returns the updated timestamps/interval.
- After a successful review, the UI requests the next card.
- If that second request fails, retry loads only the next card and cannot submit the prior review
  twice.

## Statistics

### Inspect the current schedule

As a learner, I want to see the deck's current state so that I know whether to train or add cards.

- The API returns total, new, due and scheduled card counts for an owned deck.
- The three state counts sum to the total.
- The UI visualizes the distribution and distinguishes empty, ready-to-train and caught-up decks.
- API failure offers retry without losing the selected deck.

## Cross-cutting acceptance

- Validation and malformed JSON return the standard `400` envelope.
- Missing/invalid authentication returns the same JSON structure with `401`.
- Unknown and non-owned resources are indistinguishable as `404`.
- Duplicate email or deck-local term returns `409`.
- The complete registration → deck → card → review → statistics path passes against the real
  container stack in Chromium.
