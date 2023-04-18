
import { Component, ElementRef, OnInit } from '@angular/core';
import { fadeInOut } from '../app.component';
import { Location } from '@angular/common'

@Component({
  selector: 'app-right-slide',
  templateUrl: './right-slide.component.html',
  styleUrls: ['./right-slide.component.css'],
  animations: [fadeInOut
  ]
})
export class RightSlideComponent {
  constructor(private location: Location) { }
  isShown: boolean = true;

  fadeInOut(): void {
    console.log('asdfasdf');
    this.isShown = !this.isShown;
  }

  onAnimationDone(event: any) {
    console.log('onAnimationDone', event);
    if (!this.isShown)
      this.location.back();
  }
}
