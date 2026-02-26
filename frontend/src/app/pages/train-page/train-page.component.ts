import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-train-page',
  standalone: true,
  templateUrl: './train-page.component.html',
  styleUrl: './train-page.component.css',
})
export class TrainPageComponent {
  deckId: string;

  constructor(route: ActivatedRoute) {
    this.deckId = route.snapshot.paramMap.get('deckId') ?? '';
  }
}
