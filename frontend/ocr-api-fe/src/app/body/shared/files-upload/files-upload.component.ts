import { Component } from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatProgressBar} from '@angular/material/progress-bar';
import {MatFormField} from '@angular/material/form-field';
import {MatInput} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatCard, MatCardContent, MatCardHeader} from '@angular/material/card';
import {MatList, MatListItem} from '@angular/material/list';
import {Observable} from 'rxjs';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-files-upload',
  imports: [
    MatToolbar,
    MatProgressBar,
    MatFormField,
    MatInput,
    MatButton,
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatList,
    MatListItem,
    AsyncPipe
  ],
  templateUrl: './files-upload.component.html',
  standalone: true,
  styleUrl: './files-upload.component.scss'
})
export class FilesUploadComponent {
  progressState = 0;
  currentFile?: File;
  message = '';

  fileName = 'Select File';
  fileInfos?: Observable<any>;


  uploadFile() {

  }

  selectFiles(event: any) {
    this.progressState = 0;
    this.message = "";

    if (event.target.files && event.target.files[0]) {
      const file: File = event.target.files[0];
      this.currentFile = file;
      this.fileName = this.currentFile.name;
    } else {
      this.fileName = 'Select File';
    }
  }
}
