import {Injectable} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {MessageDialogComponent} from "../dialogs/message-dialog/message-dialog.component";
import * as timers from "timers";
import {timeout} from "rxjs";
import {SpinnerDialogComponent} from "../dialogs/spinner-dialog/spinner-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class MessageSpinnerService {

  private messageRef!: MatDialogRef<MessageDialogComponent>;
  private isMessageRunning = false;

  private spinnerRef: MatDialogRef<SpinnerDialogComponent> | null = null;
  private isSpinnerRunning = false;


  constructor(private dialog: MatDialog) {
  }


  openDialogMessage(title: string,
                    description: string,
                    content: any) {

    this.messageRef = this.dialog.open(MessageDialogComponent, {
      data: {
        title: title,
        description: description,
        content: true
      }
    });

    return this.messageRef;
  }

  openDialogSpinner(title: string,
                    description: string,
                    content: any) {

    this.isSpinnerRunning = true;

    setTimeout(() => {
      if (this.isSpinnerRunning) {
        this.spinnerRef = this.dialog.open(SpinnerDialogComponent, {
          data: {
            title: title,
            description: description,
            content: true
          }
        });
        this.spinnerRef.disableClose = true;
      }
    }, 500);

    // return this.spinnerRef;
  }

  closeDialogSpinner() {
    this.isSpinnerRunning = false
    if (this.spinnerRef) {
      this.spinnerRef.close();
      this.spinnerRef = null;
    }
  }

}
