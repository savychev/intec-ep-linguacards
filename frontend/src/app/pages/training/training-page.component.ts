import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DecksService } from '../../core/decks.service';
import { Card } from '../../models/api.models';

@Component({
  selector: 'app-training-page',
  imports: [RouterLink],
  templateUrl: './training-page.component.html',
  styleUrl: './training-page.component.scss'
})
export class TrainingPageComponent implements OnInit {
  deckId = 0;
  queue: Card[] = [];
  current: Card | null = null;
  showAnswer = false;
  done = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly decksService: DecksService
  ) {}

  ngOnInit(): void {
    this.deckId = Number(this.route.snapshot.paramMap.get('deckId'));
    this.decksService.getTrainingCards(this.deckId).subscribe((cards) => {
      this.queue = cards;
      this.nextCard();
    });
  }

  flip(): void {
    this.showAnswer = true;
  }

  rate(rating: 'AGAIN' | 'HARD' | 'GOOD' | 'EASY'): void {
    if (!this.current) {
      return;
    }

    this.decksService.reviewCard(this.deckId, this.current.id, rating).subscribe(() => this.nextCard());
  }

  private nextCard(): void {
    this.current = this.queue.shift() ?? null;
    this.showAnswer = false;
    this.done = this.current === null;
  }
}
