import { Component } from '@angular/core';

@Component({
  selector: 'app-decks-list-page',
  template: `
    <section class="page-heading">
      <p class="eyebrow">Your library</p>
      <h1>My decks</h1>
      <p>Your account is connected. Deck management is the next LinguaCards milestone.</p>
    </section>

    <section class="empty-state" aria-labelledby="empty-title">
      <span class="empty-icon" aria-hidden="true">Aa</span>
      <h2 id="empty-title">Ready for your first deck</h2>
      <p>Soon you will be able to create a deck and add your own vocabulary cards here.</p>
    </section>
  `,
  styles: `
    :host {
      display: grid;
      gap: 2.5rem;
    }

    .page-heading {
      max-width: 42rem;
    }

    .eyebrow {
      margin: 0 0 0.65rem;
      color: var(--color-primary);
      font-size: 0.75rem;
      font-weight: 800;
      letter-spacing: 0.13em;
      text-transform: uppercase;
    }

    h1,
    h2 {
      font-family: var(--font-display);
    }

    h1 {
      margin: 0;
      font-size: clamp(2.7rem, 7vw, 4.8rem);
      letter-spacing: -0.04em;
    }

    .page-heading > p:last-child,
    .empty-state p {
      color: var(--color-muted);
      line-height: 1.65;
    }

    .empty-state {
      display: grid;
      justify-items: center;
      padding: clamp(2.5rem, 8vw, 5rem) 1.5rem;
      border: 1px dashed #aeb9b4;
      border-radius: 1.25rem;
      background: rgb(255 253 248 / 70%);
      text-align: center;
    }

    .empty-icon {
      display: grid;
      width: 4.5rem;
      height: 4.5rem;
      place-items: center;
      border-radius: 1rem;
      background: var(--color-primary-soft);
      color: var(--color-primary-dark);
      font-family: var(--font-display);
      font-size: 1.5rem;
    }

    .empty-state h2 {
      margin: 1.25rem 0 0;
      font-size: 1.75rem;
    }

    .empty-state p {
      max-width: 36rem;
      margin-bottom: 0;
    }
  `,
})
export class DecksListPage {}
