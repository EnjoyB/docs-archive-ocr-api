import { Component } from '@angular/core';
import {DragAndDropOrSelectComponent} from "./drag-and-drop-or-select/drag-and-drop-or-select.component";

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [
    DragAndDropOrSelectComponent
  ],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.sass'
})
export class UploadComponent {

}
