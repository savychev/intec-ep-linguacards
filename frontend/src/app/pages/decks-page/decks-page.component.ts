import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-decks-page',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './decks-page.component.html',
  styleUrl: './decks-page.component.css',
})
export class DecksPageComponent {}
