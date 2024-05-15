import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllClientAdsComponent } from './all-client-ads.component';

describe('AllClientAdsComponent', () => {
  let component: AllClientAdsComponent;
  let fixture: ComponentFixture<AllClientAdsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AllClientAdsComponent]
    });
    fixture = TestBed.createComponent(AllClientAdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
