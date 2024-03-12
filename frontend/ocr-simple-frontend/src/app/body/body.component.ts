import { Component } from '@angular/core';
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-body',
  standalone: true,
  imports: [
    RouterOutlet
  ],
  templateUrl: './body.component.html',
  styleUrl: './body.component.sass'
})
export class BodyComponent {

}
