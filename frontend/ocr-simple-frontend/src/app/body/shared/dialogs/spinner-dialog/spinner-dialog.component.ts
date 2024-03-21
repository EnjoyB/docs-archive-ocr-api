import { Component } from '@angular/core';
import {MatDialogContent, MatDialogTitle} from "@angular/material/dialog";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-spinner-dialog',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatProgressSpinner
  ],
  templateUrl: './spinner-dialog.component.html',
  styleUrl: './spinner-dialog.component.sass'
})
export class SpinnerDialogComponent {
}
