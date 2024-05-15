import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdRequestFormComponent } from './ad-request-form.component';

describe('AdRequestFormComponent', () => {
  let component: AdRequestFormComponent;
  let fixture: ComponentFixture<AdRequestFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdRequestFormComponent]
    });
    fixture = TestBed.createComponent(AdRequestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
