import { Component } from '@angular/core';
import {FilesUploadComponent} from '../shared/files-upload/files-upload.component';

@Component({
  selector: 'app-pdf-controller',
  imports: [
    FilesUploadComponent
  ],
  templateUrl: './pdf-controller.component.html',
  standalone: true,
  styleUrl: './pdf-controller.component.scss'
})
export class PdfControllerComponent {

}
