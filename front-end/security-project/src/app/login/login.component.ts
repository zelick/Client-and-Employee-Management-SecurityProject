import { Component, OnInit, OnDestroy } from '@angular/core';

import { AuthService } from '../service/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { LoginReponse } from '../model/loginResponse.model';
import { ResponseMessage } from '../model/responseMessage.model';
import { UserService } from '../services/user.service';


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
      (response: LoginReponse) => {
        console.log('Login successful:', response);
        this.messageLogin = response.response;
        this.changePasswordFlag = !response.loggedInOnce;
        if(response.loggedInOnce){
          this.loginUser();
        }
      },
      (error) => {
        console.error('Login failed:', error);
        this.messageLogin = 'Login failed. Please try again.';
      }
    );
  }

  loginUser(): void{
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
