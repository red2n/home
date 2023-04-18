import { Component } from '@angular/core';
import { style, transition, trigger, animate, state } from '@angular/animations';

export const fadeInOut = trigger('fadeInOut', [
  state(
    'in',
    style({
      opacity: 100,
    })
  ),
  transition('void => *', [style({ opacity: 5 }), animate(500, style({ opacity: 5,  }))]),
  transition('* => void', [animate('100ms', style({ opacity: 0 })), style({ opacity: 0 })]),
]);

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  onAnimationDone(event: any) {
    console.log('onAnimationDone', event);
  }
}
