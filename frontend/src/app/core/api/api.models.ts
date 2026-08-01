export interface AuthCredentials {
  email: string;
  password: string;
}

export interface AuthResponse {
  message: string;
  accessToken?: string | null;
  tokenType?: string | null;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface Deck {
  id: number;
  name: string;
  languageCode: string;
  isPrivate: boolean;
}

export interface DeckPayload {
  name: string;
  languageCode: string;
  isPrivate: boolean;
}

export interface Card {
  id: number;
  deckId: number;
  term: string;
  definition: string;
  example: string | null;
  cefr: string | null;
  tags: string | null;
}

export interface CardPayload {
  term: string;
  definition: string;
  example: string | null;
  cefr: string | null;
  tags: string | null;
}

export type ReviewRating = 'AGAIN' | 'HARD' | 'GOOD' | 'EASY';

export type TrainingEmptyReason = 'EMPTY_DECK' | 'NO_CARDS_TO_TRAIN';

export interface TrainingCard {
  cardId: number;
  deckId: number;
  term: string;
  definition: string;
  example: string | null;
  lastReviewedAt: string | null;
  nextReviewAt: string | null;
  intervalDays: number;
}

export interface TrainingNextResponse {
  hasCard: boolean;
  reason?: TrainingEmptyReason | null;
  card?: TrainingCard | null;
}

export interface DeckStats {
  deckId: number;
  totalCards: number;
  newCards: number;
  dueCards: number;
  scheduledCards: number;
}
