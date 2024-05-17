import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientHomepageComponent } from './client-homepage.component';

describe('ClientHomepageComponent', () => {
  let component: ClientHomepageComponent;
  let fixture: ComponentFixture<ClientHomepageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientHomepageComponent]
    });
    fixture = TestBed.createComponent(ClientHomepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
