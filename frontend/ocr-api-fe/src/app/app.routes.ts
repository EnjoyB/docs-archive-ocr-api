import { Routes } from '@angular/router';
import {AboutComponent} from './body/about/about.component';
import {StorageControllerComponent} from './body/storage-controller/storage-controller.component';
import {PdfControllerComponent} from './body/pdf-controller/pdf-controller.component';
import {ImgControllerComponent} from './body/img-controller/img-controller.component';

export const routes: Routes = [

  { path: 'pdf-upload', component: PdfControllerComponent},
  { path: 'img-upload', component: ImgControllerComponent},
  { path: 'manage', component: StorageControllerComponent},
  { path: 'about', component: AboutComponent},
  { path: '', component: AboutComponent}
];
