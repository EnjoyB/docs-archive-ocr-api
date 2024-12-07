import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PdfControllerComponent } from './pdf-controller.component';

describe('PdfControllerComponent', () => {
  let component: PdfControllerComponent;
  let fixture: ComponentFixture<PdfControllerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PdfControllerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PdfControllerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
