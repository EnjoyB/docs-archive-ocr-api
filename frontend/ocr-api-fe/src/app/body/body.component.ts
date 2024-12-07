import {Component, OnDestroy, signal, WritableSignal} from '@angular/core';
import {MenuStateService} from '../shared/service/menu-state.service';
import {RouterOutlet} from '@angular/router';
import {Subscription} from 'rxjs';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-body',
  imports: [
    RouterOutlet,
    MatSidenavModule,
    MatButton
  ],
  templateUrl: './body.component.html',
  styleUrl: './body.component.scss',
  standalone: true
})
export class BodyComponent implements OnDestroy{

  vents: string[] = [];
  opened: boolean;

  openMenu: WritableSignal<boolean> = signal(false);
  menuStateSub: Subscription;

  constructor(private menuStateService: MenuStateService) {
    this.menuStateSub = menuStateService.getMenuStateObs().subscribe(value => {
      this.openMenu.set(value)
    });

    this.opened = true;
  }


  ngOnDestroy(): void {
    this.menuStateSub.unsubscribe();
  }

}
