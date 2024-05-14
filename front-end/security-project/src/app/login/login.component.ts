import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { LoginReponse } from '../model/loginResponse.model';
import { ResponseMessage } from '../model/responseMessage.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  email: string = '';
  password: string = '';
  changePasswordFlag: boolean = false;
  messageLogin: string | undefined;
  messagePassword: string | undefined;
  passwordForm: FormGroup = new FormGroup({});

  constructor(private userService: UserService, private fb: FormBuilder) { }

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  login(): void {
    this.userService.login(this.email, this.password).subscribe(
      (response: LoginReponse) => {
        console.log('Login successful:', response);
        this.messageLogin = response.response;
        this.changePasswordFlag = !response.loggedInOnce;
      },
      (error) => {
        console.error('Login failed:', error);
        this.messageLogin = 'Login failed. Please try again.';
      }
    );
  }

  changePassword(): void {
    if (this.passwordForm.valid) {
      const passwordData = this.passwordForm.value;
      this.userService.changePassword(passwordData).subscribe(
        (response: ResponseMessage) => {
            this.messagePassword = response.responseMessage;
          //console.log('Password changed successfully:', response);
        },
        (error) => {
          console.error('Error changing password:', error);
        }
      );
    } else {
    }
  }
}
