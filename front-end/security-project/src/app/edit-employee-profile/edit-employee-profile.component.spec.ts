import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditEmployeeProfileComponent } from './edit-employee-profile.component';

describe('EditEmployeeProfileComponent', () => {
  let component: EditEmployeeProfileComponent;
  let fixture: ComponentFixture<EditEmployeeProfileComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditEmployeeProfileComponent]
    });
    fixture = TestBed.createComponent(EditEmployeeProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
