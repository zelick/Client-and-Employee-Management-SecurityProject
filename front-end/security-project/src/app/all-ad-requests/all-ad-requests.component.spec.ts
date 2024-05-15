import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllAdRequestsComponent } from './all-ad-requests.component';

describe('AllAdRequestsComponent', () => {
  let component: AllAdRequestsComponent;
  let fixture: ComponentFixture<AllAdRequestsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllAdRequestsComponent]
    });
    fixture = TestBed.createComponent(AllAdRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
