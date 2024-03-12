import { Routes } from '@angular/router';
import {AboutComponent} from "./body/about/about.component";
import {UploadComponent} from "./body/upload/upload.component";
import {ListComponent} from "./body/list/list.component";

export const routes: Routes = [
  {path: 'about', title:'About App', component: AboutComponent  },
  {path: 'upload', title:'Upload an file', component: UploadComponent  },
  {path: 'list', title:'An file overview', component: ListComponent  },
  {path: '', title:'An file overview', component: AboutComponent  }

];
