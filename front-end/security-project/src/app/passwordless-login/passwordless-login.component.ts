import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-passwordless-login',
  templateUrl: './passwordless-login.component.html',
  styleUrls: ['./passwordless-login.component.css']
})
export class PasswordlessLoginComponent implements OnInit{
  @ViewChild('emailInput') emailInput!: ElementRef;

  message: string = '';

  constructor(private loginService: LoginService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    
  }

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
