import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { ClientType } from '../model/clientType.model';
import { ServicesPackage } from '../model/servicesPackage.model';
import { UserService } from '../services/user.service';
import { RegistrationStatus } from '../model/registrationStatus.model';
import { ResponseMessage } from '../model/responseMessage.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

export class RegistrationComponent {

  constructor(private router: Router, 
    private userService: UserService) {
  }
  
  userRoles = Object.values(UserRole);
  clientTypes = Object.values(ClientType);
  servicesPackages = Object.values(ServicesPackage);

  passwordInvalid: boolean = false;
  registrationMessage: string = '';

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
    servicesPackage: ServicesPackage.BASIC,
    registrationStatus: RegistrationStatus.PENDING
  };

  confirmPassword: string = '';

  get passwordMismatch() {
    return this.userData.password !== this.confirmPassword;
  }

  onSubmit() {
    if (!this.validatePassword(this.userData.password)) {
      this.passwordInvalid = true;
      return;
    }

    this.userService.registerUser(this.userData).subscribe(
      (response: ResponseMessage) => {
        console.log('USPESNO REGISTROVANJE: ' + this.userData.email);
        this.registrationMessage = response.responseMessage;
        this.clearFields();
      },
      (error) => {
        console.error('GREŠKA PRILIKOM REGISTRACIJE: ', error);
        //this.registrationMessage = error.text;
        this.clearFields();
      }
    );
  }

  validatePassword(password: string): boolean {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&*!]).{12,}$/;
    return passwordRegex.test(password);
  }

  clearFields() {
    this.userData.email = '';
    this.userData.password = '';
    this.userData.name = '';
    this.userData.surname = '';
    this.userData.address = '';
    this.userData.city = '';
    this.userData.country = '';
    this.userData.phoneNumber = '';
    this.userData.role = UserRole.CLIENT;
    this.userData.clientType = ClientType.INDIVIDUAL;
    this.userData.servicesPackage = ServicesPackage.BASIC;
    this.userData.registrationStatus = RegistrationStatus.PENDING;
    this.confirmPassword = '';
    this.passwordInvalid = false;
  }
}