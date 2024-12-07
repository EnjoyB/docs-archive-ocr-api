import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImgControllerComponent } from './img-controller.component';

describe('ImgControllerComponent', () => {
  let component: ImgControllerComponent;
  let fixture: ComponentFixture<ImgControllerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImgControllerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImgControllerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
