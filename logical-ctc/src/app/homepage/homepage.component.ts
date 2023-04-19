import { Component } from '@angular/core';
import { Location } from '@angular/common'
import { fadeInOut } from '../app.component';
@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],  animations: [fadeInOut  ]

})
export class HomepageComponent {
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
