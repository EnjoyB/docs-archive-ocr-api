import { TestBed } from '@angular/core/testing';

import { MessageSpinnerService } from './message-spinner.service';

describe('MessageSpinnerService', () => {
  let service: MessageSpinnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessageSpinnerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
