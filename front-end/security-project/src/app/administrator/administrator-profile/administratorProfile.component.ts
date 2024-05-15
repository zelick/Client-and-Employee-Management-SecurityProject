import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ResponseMessage } from 'src/app/model/responseMessage.model';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-administratorProfile',
  templateUrl: './administratorProfile.component.html',
  styleUrls: ['./administratorProfile.component.css']
})
export class AdministratorProfileComponent implements OnInit {
  administratorForm: FormGroup = new FormGroup({}); 
  passwordForm: FormGroup = new FormGroup({});
  showPasswordForm: boolean = false;
  message: string | undefined;
  passwordMessage: string | undefined;

  constructor(private fb: FormBuilder,
              private router: Router,
              private userService: UserService,
              private auth: AuthService) { }

  ngOnInit(): void {

    const userRole = this.auth.getLoggedInUserRole(); 
    console.log(userRole);
    if (userRole !== "ADMINISTRATOR") {
      this.router.navigate(['/']);
    } else {
      this.administratorForm = this.fb.group({
        email: [{value: '', disabled: true}],
        password: [''],
        name: [''],
        surname: [''],
        address: [''],
        city: [''],
        country: [''],
        phoneNumber: ['']
    });

    this.passwordForm = this.fb.group({
        oldPassword: ['', Validators.required],
        newPassword: ['', Validators.required],
        confirmPassword: ['', Validators.required]
    });

    this.passwordMessage = "";
    this.message = "";

    this.getUserData();    }
  }

  getUserData() {
    this.userService.getUserData().subscribe(
      (data: any) => {
        this.administratorForm.patchValue({
          email: data.email,
          name: data.name,
          surname: data.surname,
          address: data.address,
          city: data.city,
          country: data.country,
          phoneNumber: data.phoneNumber
        });
      },
      error => {
        console.error('Error fetching administrator data:', error);
      }
    );
  }

  updateAdministratorData() {
    console.log('USAO U U[ADTAE');
    console.log(this.administratorForm.valid);
    if (this.administratorForm.valid) {
      console.log('usao ovdeee');
      const updatedData = this.administratorForm.value;
      this.userService.updateUserData(updatedData).subscribe(
        (response: ResponseMessage) => {
          this.message = response.responseMessage;
          console.log('Administrator data updated successfully:', response);
          this.getUserData();
        },
        error => {
          console.error('Error updating administrator data:', error);
        }
      );
    }
  }

  togglePasswordForm() {
    this.showPasswordForm = !this.showPasswordForm;
  }

  changePassword() {
    if (this.passwordForm.valid) {
      const passwordData = this.passwordForm.value;
      this.userService.changeAdminPassword(passwordData).subscribe(
        (response: ResponseMessage) => {
          this.passwordMessage = response.responseMessage;
          console.log('Password changed successfully:', response);
        },
        error => {
          console.error('Error changing password:', error);
        }
      );
    }
  }
}
