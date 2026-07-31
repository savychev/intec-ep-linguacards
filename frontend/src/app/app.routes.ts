import { Routes } from '@angular/router';

import { authGuard, guestGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'decks',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/login/login.page').then((module) => module.LoginPage),
    title: 'Sign in | LinguaCards',
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/auth/register/register.page').then((module) => module.RegisterPage),
    title: 'Create account | LinguaCards',
  },
  {
    path: 'decks/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/decks/deck-details/deck-details.page').then(
        (module) => module.DeckDetailsPage,
      ),
    title: 'Deck | LinguaCards',
  },
  {
    path: 'decks',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/decks/decks-list/decks-list.page').then((module) => module.DecksListPage),
    title: 'My decks | LinguaCards',
  },
  {
    path: 'training',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/training/training.page').then((module) => module.TrainingPage),
    title: 'Training | LinguaCards',
  },
  {
    path: '**',
    redirectTo: '',
  },
];
