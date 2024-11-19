import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-body',
  imports: [
    RouterOutlet
  ],
  templateUrl: './body.component.html',
  standalone: true,
  styleUrl: './body.component.sass'
})
export class BodyComponent {

}
