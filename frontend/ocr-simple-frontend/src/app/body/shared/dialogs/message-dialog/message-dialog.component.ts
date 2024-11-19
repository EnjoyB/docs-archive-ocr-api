import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-message-dialog',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton
  ],
  templateUrl: './message-dialog.component.html',
  standalone: true,
  styleUrl: './message-dialog.component.sass'
})
export class MessageDialogComponent {

  title: string;
  description: string;
  content: any;

  constructor(
    public dialogRef: MatDialogRef<MessageDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      title: string,
      description: string,
      content: any
    }
  ) {
    this.title = data.title;
    this.description = data.description;
    this.content = data.content;
  }

  onAgreementClick() {
    this.dialogRef.close(true);
  }

  onDeclineClick() {
    this.dialogRef.close(false)
  }
}
