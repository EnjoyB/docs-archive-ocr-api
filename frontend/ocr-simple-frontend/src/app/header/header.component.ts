import { Component } from '@angular/core';
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatIconButton} from "@angular/material/button";
import {RouterLink} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {MessageDialogComponent} from "../body/shared/dialogs/message-dialog/message-dialog.component";
import {SpinnerDialogComponent} from "../body/shared/dialogs/spinner-dialog/spinner-dialog.component";


@Component({
  selector: 'app-header',
  imports: [MatToolbarModule, MatIcon, MatIconButton, MatButton, RouterLink],
  templateUrl: './header.component.html',
  standalone: true,
  styleUrl: './header.component.sass'
})
export class HeaderComponent {


  constructor(public dialog: MatDialog) {}

  openDialogMessage() {
    const dialogRef = this.dialog.open(MessageDialogComponent, {
      data: {
        title: "Random message",
        description: "We who see, we who be.",
        content: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The message dialog was closed');
    });
  }

  openDialogSpinner() {
    const dialogRef = this.dialog.open(SpinnerDialogComponent, {
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The spinner dialog was closed');
    });
  }



}
