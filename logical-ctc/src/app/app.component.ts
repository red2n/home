import { Component } from '@angular/core';
import { style, transition, trigger, animate, state, keyframes } from '@angular/animations';

export const fadeInOut = trigger('fadeInOut', [
  state(
    'in',
    style({
      opacity: 0,
      transform: 'translate-x-full'
    })
  ), state(
    'out',
    style({
      opacity: 100,
      transform: 'translate-x-0'
    })
  ),
  transition('void => *', animate('500ms',
    style({ opacity: 100 }))
  ),
  transition('* => void', animate('500ms',
  style({ opacity: 0 }))
),
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
