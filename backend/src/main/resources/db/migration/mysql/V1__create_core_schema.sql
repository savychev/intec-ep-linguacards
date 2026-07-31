CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE decks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    KEY idx_decks_owner_id (owner_id),
    CONSTRAINT fk_decks_owner FOREIGN KEY (owner_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE cards (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deck_id BIGINT NOT NULL,
    term VARCHAR(200) NOT NULL,
    definition VARCHAR(2000) NOT NULL,
    example VARCHAR(500),
    cefr VARCHAR(5),
    tags VARCHAR(200),
    next_review_at DATETIME(6),
    last_reviewed_at DATETIME(6),
    interval_days INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_cards_deck_term UNIQUE (deck_id, term),
    KEY idx_cards_deck_id (deck_id),
    KEY idx_cards_next_review_at (next_review_at),
    CONSTRAINT fk_cards_deck FOREIGN KEY (deck_id) REFERENCES decks (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE review_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    card_id BIGINT NOT NULL,
    rating ENUM ('AGAIN', 'HARD', 'GOOD', 'EASY') NOT NULL,
    reviewed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_review_logs_card_id (card_id),
    KEY idx_review_logs_reviewed_at (reviewed_at),
    CONSTRAINT fk_review_logs_card FOREIGN KEY (card_id) REFERENCES cards (id)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
