import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-deck-details-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './deck-details-page.component.html',
  styleUrl: './deck-details-page.component.css',
})
export class DeckDetailsPageComponent {
  deckId: string;

  constructor(route: ActivatedRoute) {
    this.deckId = route.snapshot.paramMap.get('id') ?? '';
  }
}
