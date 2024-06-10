import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { ResponseMessage } from '../model/responseMessage.model';
import { LoginResponse } from '../model/loginResponse.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  email: string = '';
  password: string = '';
  verificationCode: string = '';
  changePasswordFlag: boolean = false;
  showVerificationCode: boolean = false;
  messageLogin: string | undefined;
  messagePassword: string | undefined;
  messageVerification: string | undefined;
  passwordForm: FormGroup = new FormGroup({});

  constructor(private userService: UserService, private fb: FormBuilder, private authService: AuthService) { }

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  tryLogin(): void {
    this.userService.tryLogin(this.email, this.password).subscribe(
      (response: LoginResponse) => {
        if (response.response === "This account is not active, please wait for admin to activate your account.") {
          this.messageLogin = response.response;
          this.changePasswordFlag = false;
          return;
        }
        if (response.response === "The user did not enter the two-factor authentication code correctly, and his account is not active.") {
          this.messageLogin = response.response;
          this.changePasswordFlag = false;
          return;
        }
        if (response.mfaEnabled) {
          this.showVerificationCode = true;
          this.messageLogin = response.response;
        } else {
          this.messageLogin = response.response;
          this.changePasswordFlag = !response.loggedInOnce;
          if (response.loggedInOnce) {
            this.loginUser();
          }
        }
      },
      (error) => {
        console.error('Login failed:', error);
        this.messageLogin = 'Login failed. Please try again.';
      }
    );
  }

  verifyCode(): void {
    const verificationData = {
      email: this.email,
      code: this.verificationCode
    };

    this.userService.verifyMfaCode(verificationData).subscribe(
      response => {
        if (response.flag) {
          this.messageLogin = response.responseMessage;
          this.loginUser();
        } else {
          this.messageVerification = 'Verification failed. Please check the code and try again.';
        }
      },
      error => {
        this.messageVerification = 'Verification failed. Please try again later.';
      }
    );
  }

  loginUser(): void {
    this.authService.login({ username: this.email, password: this.password }).subscribe(
      (response: any) => {
        console.log('Login successful!!!');
      },
      (error) => {
        console.error('Login failed:', error);
        this.messageLogin = 'Login failed. Please try again.';
      }
    );
  }

  changePassword(): void {
    if (this.passwordForm.valid) {
      const passwordData = {
        oldPassword: this.passwordForm.value.oldPassword,
        newPassword: this.passwordForm.value.newPassword,
        confirmPassword: this.passwordForm.value.confirmPassword,
        email: this.email,
      };
      this.userService.changePassword(passwordData).subscribe(
        (response: ResponseMessage) => {
          this.messagePassword = response.responseMessage;
        },
        (error) => {
          console.error('Error changing password:', error);
        }
      );
    }
  }
}
