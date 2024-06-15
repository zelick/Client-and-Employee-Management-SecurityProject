
import { Component, NgModule, OnInit, OnDestroy  } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { FormBuilder, FormGroup, NgForm, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { LoginService } from '../services/login.service';
import * as DOMPurify from 'dompurify';
import { ResponseMessage } from '../model/responseMessage.model';
import { LoginResponse } from '../model/loginResponse.model';
import { environment } from 'src/env/environment';
import { Subject, takeUntil } from 'rxjs';

declare var grecaptcha: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit, OnDestroy {
  email: string = '';
  password: string = '';
  verificationCode: string = '';
  changePasswordFlag: boolean = false;
  showVerificationCode: boolean = false;
  messageLogin: string | undefined;
  messagePassword: string | undefined;
  messageVerification: string | undefined = "";
  passwordForm: FormGroup = new FormGroup({});
  resetPasswordFlag: boolean = false;
  private unsubscribe$ = new Subject<void>();

  reCAPTCHAToken: string = "";
  tokenVisible: boolean = false;

  isEmployeed: boolean = false;

  isEmployeedAndMfaEnabled: boolean = false;

  siteKey = `${environment.recaptcha.siteKey}`;

  constructor(private userService: UserService, 
    private fb: FormBuilder, 
    private authService: AuthService, private loginService: LoginService){}


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
      (response: LoginResponse) => {
        console.log('Login response:', response);
        this.messageLogin = response.response;
        if(response.response === "This email does not exist."){
          this.changePasswordFlag = false;
          return;
        }
        if (response.response === "This account is not active, please wait for admin to activate your account.") {
          this.changePasswordFlag = false;
          return;
        }
        
        if (response.response === "The user did not enter the two-factor authentication code correctly, and his account is not active.") {
          //this.messageLogin = response.response;
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
        if (response.employeed) {
          console.log("UPAO OVDE DA JE EMPLOYEE: " + response.employeed);

          if (!response.loggedInOnce) {
            this.changePasswordFlag = !response.loggedInOnce;
            return;
          }

          if (response.mfaEnabled) {
            this.showVerificationCode = true;
            this.messageVerification = "";
            this.isEmployeedAndMfaEnabled = true;
            return;
          } 

          this.loadReCaptcha();

        } else {
          this.finalizeLogin(response);
        }
      },
      (error) => {
        console.error('Login failed:', error);
        this.messageLogin = 'Login failed. Please try again.';
      }
    );
  }


  loadReCaptcha() {
    this.messageLogin = 'Please complete the reCAPTCHA to proceed.';
          this.isEmployeed = true;
          // Render reCAPTCHA
          setTimeout(() => {
            if (typeof grecaptcha !== 'undefined') {
              grecaptcha.render('recaptcha-container', {
                sitekey: this.siteKey,
              });
              /*
              const response = grecaptcha.getResponse();
              console.log("RECAPTCHA RESPONSE::: " + response);
              */

            } 
          }, 0);
  }

  ngAfterViewInit(): void {
    if (this.isEmployeed && typeof grecaptcha !== 'undefined') {
      grecaptcha.ready(() => {
        grecaptcha.render('recaptcha-container', {
          sitekey: this.siteKey,
        });
      });
    }
  }

  recaptcha() {
    const response = grecaptcha.getResponse();
    if (response.length === 0) {
      this.messageLogin = 'Please complete the reCAPTCHA';
      return;
    } else {
      console.log("reCAPTCHA response token: ", response);

      //OVDE TREBA PROVERITI DA LI JE OK TOKEN NA BEKU
      const verificationData = {
        reCaptchaToken: response,
      };
      this.userService.verifyReCaptchaToken(verificationData).subscribe(
        response => {
          if (response.flag) {
            this.messageLogin = response.responseMessage;
            this.loginUser();
          } else {
            this.messageLogin = response.responseMessage;
          }
        },
        error => {
          this.messageLogin = 'Verification failed. Please try again later.';
        }
      );
    }
  }
  
  finalizeLogin(response: LoginResponse): void {
    this.messageLogin = response.response;

    if (response.mfaEnabled) {
      this.showVerificationCode = true;
      this.messageVerification = "";
      this.isEmployeedAndMfaEnabled = false;
      return;
    } 

    if (response.loggedInOnce) {
      this.loginUser();
    }
  }

  verifyCode(): void {
    const verificationData = {
      email: this.email,
      code: this.verificationCode,
      fromLogin: true
    };

    this.userService.verifyMfaCode(verificationData).subscribe(
      response => {
        if (response.flag) {
          this.messageLogin = response.responseMessage;
          if (this.isEmployeedAndMfaEnabled) {
            this.messageLogin = "";
            this.loadReCaptcha();
          }
          else {
            this.loginUser();
          }
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
