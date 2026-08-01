# LinguaCards - UX Skeleton (Sitemap + Wireframes)

## 1. Sitemap (Pages)
Public:
- /login
- /register

Protected (requires JWT):
- /decks
- /decks/:id
- /training?deckId=...
- /stats?deckId=...

Optional:
- / (redirect to /decks if authenticated else /login)

---

## 2. Wireframes (Text)

### 2.1 Login Page (/login)
Elements:
- Title: "LinguaCards"
- Email input
- Password input
- Button: "Login"
- Link: "Create account" -> /register
  Validation:
- show inline validation messages

Success:
- redirect to /decks

---

### 2.2 Register Page (/register)
Elements:
- Title: "Create account"
- Email input
- Password input
- Button: "Register"
- Link: "Already have an account?" -> /login

Success:
- auto-login OR redirect to /login (MVP: redirect to /login)

---

### 2.3 Deck List Page (/decks)
Elements:
- Header: "My Decks"
- Button: "Create Deck"
- Deck list items:
    - deck name
    - language badge
    - actions: Open, Edit, Delete
- Create/Edit deck modal or separate form:
    - name
    - language dropdown

Actions:
- Open -> /decks/{id}

---

### 2.4 Deck Detail Page (/decks/:id)
Layout:
- Deck header:
    - deck name, language
    - buttons: Edit Deck, Delete Deck
- Cards section:
    - Button: "Add Card"
    - Table/List of cards:
        - term
        - definition
        - optional CEFR level and tags
        - actions: Edit, Delete
- Quick actions:
    - Button: "Start Training" -> /training?deckId={id}
    - Button: "View Stats" -> /stats?deckId={id}

Add/Edit card form:
- term (required)
- definition (required)
- example sentence (optional)
- CEFR level (optional)
- tags (optional)

---

### 2.5 Training Page (/training?deckId=...)
Layout:
- Header: "Training"
- Card display:
    - Term (big)
- "Show answer" action
- Revealed answer:
    - definition
    - example sentence
- Buttons:
    - AGAIN
    - HARD
    - GOOD
    - EASY
- If no cards:
    - message: "No cards available for training."

Flow:
- On page load -> POST start training
- Reveal the answer before enabling ratings
- On rating -> POST review -> POST next card
- If review succeeds but loading the next card fails, retry only the next-card request

---

### 2.6 Stats Page (/stats?deckId=...)
Elements:
- Header: "Deck Statistics"
- Cards count:
    - total
    - new
    - due now
    - scheduled for later
- Schedule distribution bar for non-empty decks
- Empty state with an action to add cards
- Next-step action:
    - start training when new or due cards are available
    - return to deck management when all cards are scheduled
- Retry action when the API request fails

---

## 3. UX Notes (MVP)
- Keep UI minimal and clear (no heavy UI frameworks required)
- Show clear validation errors
- Make "monolingual" rule visible in UI (hint text on card form: "No translations. Use definitions/examples in the deck language.")
