import { Component } from '@angular/core';
import {DragAndDropOrSelectComponent} from "./drag-and-drop-or-select/drag-and-drop-or-select.component";

@Component({
  selector: 'app-upload',
  imports: [
    DragAndDropOrSelectComponent
  ],
  templateUrl: './upload.component.html',
  standalone: true,
  styleUrl: './upload.component.sass'
})
export class UploadComponent {

}
