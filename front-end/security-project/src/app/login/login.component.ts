import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LoginReponse } from '../model/loginResponse.model';
import { ResponseMessage } from '../model/responseMessage.model';
import { UserService } from '../services/user.service';
import { LoginService } from '../services/login.service';
import * as DOMPurify from 'dompurify';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit, OnDestroy {
  email: string = '';
  password: string = '';
  changePasswordFlag: boolean = false;
  messageLogin: string | undefined;
  messagePassword: string | undefined;
  passwordForm: FormGroup = new FormGroup({});
  resetPasswordFlag: boolean = false;
  private unsubscribe$ = new Subject<void>();

  constructor(private userService: UserService, private fb: FormBuilder, private authService: AuthService, private loginService: LoginService) { }

  ngOnInit(): void {
    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  resetPassword(): void {
    const sanitizedEmail = DOMPurify.sanitize(this.email);
    this.loginService.resetPassword(sanitizedEmail).subscribe(
      response => {
        console.log(response);
      },
      error => {
        console.error(error);
      }
    );
  }

  tryLogin(): void {
    const sanitizedEmail = DOMPurify.sanitize(this.email);
    const sanitizedPassword = DOMPurify.sanitize(this.password);
    this.userService.tryLogin(sanitizedEmail, sanitizedPassword).pipe(
      takeUntil(this.unsubscribe$)
    ).subscribe(
      (response: LoginReponse) => {
        console.log('Login response:', response);
        this.messageLogin = response.response;
        this.changePasswordFlag = !response.loggedInOnce;
        if (response.response === "This account is not active, please wait for admin to activate your account.") {
          this.changePasswordFlag = false;
          return;
        }
        if (response.response === "Your account has been blocked by administrator.") {
          this.changePasswordFlag = false;
          return;
        }
        if (response.response === "Wrong password. Try again or click ‘Forgot password’ to reset it.") {
          this.resetPasswordFlag = true;
          return; 
        }
        if (response.loggedInOnce) {
          this.loginUser();
        }
      },
      (error) => {
        console.error('Login failed:', error);
        this.messageLogin = 'Login failed. Please try again.';
      }
    );
  }

  loginUser(): void {
    const sanitizedEmail = DOMPurify.sanitize(this.email);
    const sanitizedPassword = DOMPurify.sanitize(this.password);
    this.authService.login({ username: sanitizedEmail, password: sanitizedPassword }).pipe(
      takeUntil(this.unsubscribe$)
    ).subscribe(
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
        oldPassword: DOMPurify.sanitize(this.passwordForm.value.oldPassword),
        newPassword: DOMPurify.sanitize(this.passwordForm.value.newPassword),
        confirmPassword: DOMPurify.sanitize(this.passwordForm.value.confirmPassword),
        email: this.email, 
      };
      this.userService.changePassword(passwordData).pipe(
        takeUntil(this.unsubscribe$)
      ).subscribe(
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
