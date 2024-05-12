import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/user.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

export class RegistrationComponent {

  constructor(private router: Router) {
  }

}