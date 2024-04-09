import {Component} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {
  MatCell, MatCellDef, MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatNoDataRow,
  MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {FileCustom} from "./file-custom.model";
import {CdkColumnDef} from "@angular/cdk/table";
import {File} from "node:buffer";
import {Event} from "@angular/router";
import {MatInput, MatPrefix} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";

@Component({
  selector: 'app-drag-and-drop-or-select',
  standalone: true,
  imports: [
    MatIcon,
    MatTable,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatNoDataRow,
    MatColumnDef,
    CdkColumnDef,
    MatInput,
    MatButton,
    MatIconButton,
    MatPrefix
  ],
  templateUrl: './drag-and-drop-or-select.component.html',
  styleUrl: './drag-and-drop-or-select.component.sass'
})
export class DragAndDropOrSelectComponent {

  displayedColumns = ['name', 'size', 'type', 'status'];

  selectedFiles: File[] = [];

  dataSource: MatTableDataSource<FileCustom>;


  constructor() {
    this.dataSource = new MatTableDataSource<FileCustom>([]);
  }

  onChangedFiles(event: any) {
    let fileList = event.target.files;

    if (fileList != null) {
      this.selectedFiles = [];
      const newDataSource: FileCustom[] = [];
      for (let i = 0; i < fileList.length; i++) {
        const file = fileList.item(i);
        if (file) {
          this.selectedFiles.push(file);
          newDataSource.push(new FileCustom(file.name, "Prepared", String(file.size), file.type))
        }
      }
      this.dataSource = new MatTableDataSource<FileCustom>(newDataSource);
    }
  }

  handleFileInputChange(files: FileList | null) {
    
  }
}
