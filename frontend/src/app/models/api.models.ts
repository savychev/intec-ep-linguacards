export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
}

export interface Deck {
  id: number;
  name: string;
  languageCode: string;
  isPrivate: boolean;
}

export interface Card {
  id: number;
  term: string;
  definition: string;
  example?: string;
  cefrLevel?: string;
  tags?: string;
}

export interface DeckStats {
  totalCards: number;
  reviewedCards: number;
  masteredCards: number;
  dueCards: number;
}
