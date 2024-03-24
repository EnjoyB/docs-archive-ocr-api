import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DragAndDropOrSelectComponent } from './drag-and-drop-or-select.component';

describe('DragAndDropOrSelectComponent', () => {
  let component: DragAndDropOrSelectComponent;
  let fixture: ComponentFixture<DragAndDropOrSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DragAndDropOrSelectComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DragAndDropOrSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
