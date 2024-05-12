import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { UserRole } from '../model/userRole.model';
import { ClientType } from '../model/clientType.model';
import { ServicesPackage } from '../model/servicesPackage.model';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Dodajte ovu liniju

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

export class RegistrationComponent {
  userRoles = Object.values(UserRole);
  clientTypes = Object.values(ClientType);
  servicesPackages = Object.values(ServicesPackage);

  userData = {
    email: '',
    password: '',
    name: '',
    surname: '',
    address: '',
    city: '',
    country: '',
    phoneNumber: '',
    role: UserRole.CLIENT,
    clientType: ClientType.INDIVIDUAL,
    servicesPackage: ServicesPackage.BASIC
  };

  confirmPassword: string = '';

  get passwordMismatch() {
    return this.userData.password !== this.confirmPassword;
  }

  onSubmit() {
    // Logika za slanje podataka na server ili dodatna obrada
    console.log('USPESNO REGISTROVANJE: ' + this.userData.email);
  }
}