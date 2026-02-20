import { Routes } from '@angular/router';
import { AuthPageComponent } from './pages/auth/auth-page.component';
import { DecksPageComponent } from './pages/decks/decks-page.component';
import { TrainingPageComponent } from './pages/training/training-page.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'auth' },
  { path: 'auth', component: AuthPageComponent },
  { path: 'decks', component: DecksPageComponent },
  { path: 'training/:deckId', component: TrainingPageComponent },
  { path: '**', redirectTo: 'auth' }
];
