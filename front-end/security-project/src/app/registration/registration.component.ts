import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { ClientType } from '../model/clientType.model';
import { ServicesPackage } from '../model/servicesPackage.model';
import { UserService } from '../services/user.service';
import { RegistrationStatus } from '../model/registrationStatus.model';
import { NgForm } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { User } from '../model/user.model';
import * as DOMPurify from 'dompurify';
import { RegistrationResponse } from '../model/registrationResponse';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit {

  constructor(private router: Router, 
              private userService: UserService,
              private auth: AuthService) {}

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    if (userRoles && userRoles.includes(UserRole.ADMINISTRATOR)) {
      this.isAdmin = true;
    } else {
      if (userRoles && (userRoles.includes(UserRole.CLIENT) || userRoles.includes(UserRole.EMPLOYEE))) {
        this.router.navigate(['/homepage']);
      } else {
        this.isUnAuthorize = true;
      }
    }
  }

  userRole: UserRole | undefined;
  userRoles = Object.values(UserRole);
  clientTypes = Object.values(ClientType);
  servicesPackages = Object.values(ServicesPackage);

  passwordInvalid: boolean = false;
  registrationMessage: string = '';
  passwordMismatch: boolean = false;
  isAdmin: boolean = false;
  isUnAuthorize: boolean = false;
  includesClient: boolean = true;
  showQrCode: boolean = false;
  qrCode: string = '';
  verificationCode: string = '';

  userData = {
    email: '',
    password: '',
    name: '',
    surname: '',
    address: '',
    city: '',
    country: '',
    phoneNumber: '',
    roles: [] as UserRole[],
    clientType: ClientType.NONE,
    servicesPackage: ServicesPackage.NONE,
    registrationStatus: RegistrationStatus.PENDING,
    mfaEnabled: false
  };

  confirmPassword: string = '';

  onSubmit(registrationForm: NgForm): void {
    if (!registrationForm.valid) {
      return;
    }

    if (this.userData.password !== this.confirmPassword) {
      this.passwordMismatch = true;
      return;
    }

    if (!this.validatePassword(this.userData.password)) {
      this.passwordInvalid = true;
      return;
    }

    if (!this.isValidEmail(this.userData.email)) {
      this.registrationMessage = 'Invalid email format.';
      return;
    }

    if (this.userRole) {
      this.userData.roles.push(this.userRole);
    }

    this.userData.email = DOMPurify.sanitize(this.userData.email);
    this.userData.password = DOMPurify.sanitize(this.userData.password);
    this.confirmPassword = DOMPurify.sanitize(this.confirmPassword);
    this.userData.name = DOMPurify.sanitize(this.userData.name);
    this.userData.surname = DOMPurify.sanitize(this.userData.surname);
    this.userData.address = DOMPurify.sanitize(this.userData.address);
    this.userData.city = DOMPurify.sanitize(this.userData.city);
    this.userData.country = DOMPurify.sanitize(this.userData.country);
    this.userData.phoneNumber = DOMPurify.sanitize(this.userData.phoneNumber);

    this.registerUser();
  }

  selectedRole: UserRole | undefined;

  isValidEmail(email: string): boolean {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
  }

  private registerUser(): void {
    this.userService.registerUser(this.userData).subscribe(
      (response: RegistrationResponse) => {
        this.registrationMessage = response.responseMessage;
        if (response.flag) {
          if (this.userData.mfaEnabled && response.secretImageUri) {
            this.showQrCode = true;
          } else {
            //ZA SAD VIDETI KAKO OVO OSMISLITI
            //this.redirectUser();
          }
        }
      },
      (error) => {
        console.error('Error during registration:', error);
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
    this.userData.roles = [];    
    this.userData.clientType = ClientType.INDIVIDUAL;
    this.userData.servicesPackage = ServicesPackage.BASIC;
    this.userData.registrationStatus = RegistrationStatus.PENDING;
    this.confirmPassword = '';
    this.passwordInvalid = false;
  }

  onRoleChange(role: string) {
    switch (role) {
      case 'ADMINISTRATOR':
        this.userRole = UserRole.ADMINISTRATOR;
        break;
      case 'EMPLOYEE':
        this.userRole = UserRole.EMPLOYEE;
        break;
      case 'CLIENT':
        this.userRole = UserRole.CLIENT;
        break;
      default:
        break;
    }
  }

  onMfaChange(enable: boolean) {
    this.userData.mfaEnabled = enable;
  }

  redirectUser(): void {
    if (this.isAdmin) {
      this.router.navigate(['/homepage']);
    } else {
      this.router.navigate(['/']);
    }
  }

  verifyCode(): void {
    const verificationData = {
      email: this.userData.email,
      code: this.verificationCode,
      fromLogin: false
    };

    this.userService.verifyMfaCode(verificationData).subscribe(response => {
      if (response.flag) {
        this.registrationMessage = response.responseMessage;
        this.redirectUser();
      } else {
        this.registrationMessage = 'Verification failed. Please check the code and try again.';
      }
    }, error => {
      this.registrationMessage = 'Verification failed. Please try again later.';
    });
  }
}
