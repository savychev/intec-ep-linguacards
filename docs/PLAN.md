# LinguaCards - Project Plan

## 1. Milestones

### Milestone 1 - Backend Foundation ✅ COMPLETED
- [x] Spring Boot project initialized
- [x] MySQL + Flyway configured
- [x] User entity
- [x] JWT authentication
- [x] Register/Login endpoints
- [x] Global exception handler
- [x] Integration test for authentication

Deliverable:
- [x] Working auth flow
- [x] Protected endpoint accessible with JWT

---

### Milestone 2 - Deck & Card Management
- Deck entity + CRUD
- Owner validation
- Card entity + CRUD
- Monolingual rule (non-empty validation)
- Integration test for owner isolation

Deliverable:
- User can manage decks and cards securely

---

### Milestone 3 - Training Module
- ReviewLog entity
- Get next card logic
- Submit rating logic
- Status update mapping
- Integration test for training flow

Deliverable:
- User can train and change card statuses

---

### Milestone 4 - Statistics
- Aggregation queries
- Stats endpoint
- Integration test for stats

Deliverable:
- Deck statistics working correctly

---

### Milestone 5 - Frontend (Angular)
- Auth pages
- Deck management UI
- Card management UI
- Training page
- Stats page

Deliverable:
- End-to-end working application

---

## 2. Definition of Done (DoD)

A feature is considered done when:

- Code compiles
- At least one integration or unit test exists (where applicable)
- Validation annotations added
- Proper HTTP status codes returned
- Owner isolation enforced
- Code reviewed (self-review)
- No TODO comments left
- Flyway migration added (if DB changed)

---

## 3. Risks & Mitigation

### Risk 1 - JWT misconfiguration
Impact:
- All endpoints insecure or always 401

Mitigation:
- Write integration test for auth
- Test manually with Postman

---

### Risk 2 - Owner isolation bugs
Impact:
- Users can access others' data

Mitigation:
- Implement owner check in service layer
- Add integration test for unauthorized access

---

### Risk 3 - Flyway migration errors
Impact:
- App fails on startup

Mitigation:
- Keep migrations simple
- Test migration on empty DB
- Never edit old migrations (create new version)

---

### Risk 4 - Overengineering
Impact:
- Time wasted, complex code

Mitigation:
- Stick strictly to MVP scope
- Avoid premature optimization

---

## 4. Timeline (example for 3-4 weeks)

Week 1:
- Backend foundation

Week 2:
- Deck + Card management

Week 3:
- Training + Stats

Week 4:
- Frontend + polishing + tests
