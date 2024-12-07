import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MenuStateService {

  menuStateSubject = new BehaviorSubject(false);

  constructor() { }

  changeMenuState() {
    this.menuStateSubject.next(!this.menuStateSubject.getValue());
  }

  getMenuStateObs(){
    return this.menuStateSubject.asObservable();
  }
}
