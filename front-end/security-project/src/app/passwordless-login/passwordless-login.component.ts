import { Component, ViewChild, ElementRef } from '@angular/core';
import { LoginService } from '../services/login.service';

@Component({
  selector: 'app-passwordless-login',
  templateUrl: './passwordless-login.component.html',
  styleUrls: ['./passwordless-login.component.css']
})
export class PasswordlessLoginComponent {
  @ViewChild('emailInput') emailInput!: ElementRef;

  message: string = '';

  constructor(private loginService: LoginService) {}

  onLoginClick() {
    const email = this.emailInput.nativeElement.value;

    this.loginService.sendEmail(email).subscribe(
      response => {
        console.log(response);
        this.message = response;
      },
      error => {
        console.error(error);
        this.message = error.error;
      }
    );
  }
}
