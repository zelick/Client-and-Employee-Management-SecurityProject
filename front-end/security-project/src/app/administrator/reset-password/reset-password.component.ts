import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { LoginResponse } from 'src/app/model/loginResponse.model';
import { Router } from '@angular/router';
import * as DOMPurify from 'dompurify';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  passwordForm: FormGroup;
  message: string = '';
  messagePassword: string = '';
  email: string | undefined;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private userService: UserService,
    private router: Router
  ) {
    this.passwordForm = this.fb.group({
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['email']) {
        this.email = params['email'];
      }
    });
  }

  resetPassword(): void {
    if (this.passwordForm.invalid) {
      this.message = 'Please fill in the form correctly.';
      return;
    }

    const newPassword = DOMPurify.sanitize(this.passwordForm.get('newPassword')?.value);
    const confirmPassword = DOMPurify.sanitize(this.passwordForm.get('confirmPassword')?.value);

    if (newPassword !== confirmPassword) {
      this.messagePassword = 'Passwords do not match.';
      return;
    }

    console.log("Mejl" + this.email);
    if(this.email)
      {
        this.userService.resetPassword(this.email, newPassword).subscribe(
          (response: LoginResponse) => {
            if(response.response === "Password do not meet the requirements.")
              {
                this.messagePassword = response.response;
                return;
              }
                console.log(response);
                this.messagePassword = response.response;
                this.router.navigate(['/']);
            
          },
          error => {
            console.error('Error resetting password:', error);
          }
        );
      }
      }
}
