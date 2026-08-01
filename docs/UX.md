# LinguaCards implemented UX

## Routes

| Route                | Access        | Main purpose                                         |
| -------------------- | ------------- | ---------------------------------------------------- |
| `/login`             | public-only   | Sign in and show registration/session-ended feedback |
| `/register`          | public-only   | Create an account, then redirect to login            |
| `/decks`             | authenticated | List and manage personal decks                       |
| `/decks/:id`         | authenticated | Manage cards and enter training/statistics           |
| `/training?deckId=…` | authenticated | Reveal and rate the next available card              |
| `/stats?deckId=…`    | authenticated | Inspect the deck schedule                            |

`/` redirects according to authentication state. Auth guards reject expired JWTs before a
protected page renders.

## Authentication

Login and registration use labeled email/password controls, inline validation and a disabled
submit state while the request is active. Registration redirects to login with an account-ready
message. Login redirects to the originally requested protected URL when safe, otherwise `/decks`.
API and connectivity failures are shown in the form rather than losing the user's input.

## Deck list

- Shows deck name, language code and privacy state.
- Uses an inline create/edit region with name, language code and private checkbox.
- Provides `Open`, `Edit` and two-step `Delete` actions.
- Separates loading, network failure with retry and no-decks states.

## Deck detail and cards

- Shows the selected deck, language, card count and navigation back to all decks.
- Provides `View statistics`, `Start training` and `Add card` actions.
- Displays term, definition and optional example/CEFR/tags in each card.
- Uses one accessible inline form for create/edit and requires confirmation before delete.
- Reminds the learner to write definitions/examples in the deck language.

## Training

1. The page calls `POST /api/decks/{deckId}/train/start`.
2. Only the term is initially visible.
3. `Show answer` reveals the definition/example and enables four rating controls.
4. The selected rating is saved with `POST /api/cards/{cardId}/review`.
5. The next card is loaded with `POST /api/decks/{deckId}/train/next`.

The ratings show their fixed intervals: Again 1 day, Hard 3 days, Good 7 days, Easy 14 days.
Empty decks link to card creation; caught-up decks link back to deck management. If review succeeds
but the next request fails, retry cannot repeat the already accepted review.

## Statistics

The page shows total, new, due-now and scheduled metric cards plus a distribution bar. A new/due
deck offers training, an empty deck offers card creation, and a fully scheduled deck explains that
the learner is caught up. Network failure retains the deck ID and offers retry.

## Interaction and accessibility conventions

- Native labels, headings, landmarks, live status/error messages and buttons/links are used for
  stable keyboard and assistive-technology behavior.
- Destructive actions require a second explicit confirmation.
- Loading disables repeated submissions.
- Empty and error states always provide an actionable recovery path.
- The visual system uses high-contrast ink/green accents, responsive layouts and no heavy UI
  framework.
