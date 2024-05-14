import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ResponseMessage } from 'src/app/model/responseMessage.model';
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

  constructor(private fb: FormBuilder,
              private router: Router,
              private userService: UserService) { }

  ngOnInit(): void {
    this.administratorForm = this.fb.group({
      email: [{value: '', disabled: true}],
      password: ['', Validators.required],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      address: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required],
      phoneNumber: ['', Validators.required]
    });

    this.passwordForm = this.fb.group({
        oldPassword: ['', Validators.required],
        newPassword: ['', Validators.required],
        confirmPassword: ['', Validators.required]
    });

    this.userService.getUserData().subscribe(
      (data: any) => {
        // Postavljanje vrednosti u formularu
        this.administratorForm.patchValue({
          email: data.email,
          password: data.password,
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
    if (this.administratorForm.valid) {
      const updatedData = this.administratorForm.value;
      this.userService.updateUserData(updatedData).subscribe(
        (response: any) => {
          console.log('Administrator data updated successfully:', response);
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
      this.userService.changePassword(passwordData).subscribe(
        (response: ResponseMessage) => {
          this.message = response.responseMessage;
          console.log('Password changed successfully:', response);
        },
        error => {
          console.error('Error changing password:', error);
        }
      );
    }
  }


}
