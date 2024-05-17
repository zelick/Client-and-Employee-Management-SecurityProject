import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { ClientType } from '../model/clientType.model';
import { ServicesPackage } from '../model/servicesPackage.model';
import { UserService } from '../services/user.service';
import { RegistrationStatus } from '../model/registrationStatus.model';
import { ResponseMessage } from '../model/responseMessage.model';
import { NgForm } from '@angular/forms';
import { AuthService } from '../service/auth.service';
import { User } from '../model/user.model';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})

export class RegistrationComponent implements OnInit{

  constructor(private router: Router, 
    private userService: UserService,
    private auth: AuthService) {
  }
  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);

    if (userRoles && userRoles.includes(UserRole.ADMINISTRATOR)) {
      this.isAdmin = true;
    }
    else {
      if (userRoles && userRoles.includes(UserRole.CLIENT) || userRoles.includes(UserRole.EMPLOYEE)) {
        this.router.navigate(['/homepage']);
      }
      else {
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
  includesClient: boolean = true; //posle izmeni logiku

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
    registrationStatus: RegistrationStatus.PENDING
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
      console.log('USAO DA PUSGUJE ROLU: ' + this.userData.roles);
    }

    this.registerUser();
  }

  selectedRole: UserRole | undefined;

  isValidEmail(email: string): boolean {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
  }

  private registerUser(): void {
    console.log("USER KOJI SE REGISTRUJE: ");
    console.log(this.userData);
    this.userService.registerUser(this.userData).subscribe(
      (response: ResponseMessage) => {
        //console.log('USPESNO REGISTROVANJE: ' + this.userData.email);
        this.registrationMessage = response.responseMessage;
        console.log("ADMIN JE ULOGOVAN: " + this.isAdmin);
        console.log("RESPONSE FLAG: " + response.flag)
        if (response.flag === true && this.isAdmin === true) {
          console.log("USAAO GDE TREBA samo nece u homepage ");
          this.router.navigate(['/homepage']);
        }
        else if (response.flag === true && this.isAdmin === false) {
          this.router.navigate(['/']);
        }
        //this.clearFields();
      },
      (error) => {
        console.error('GREŠKA PRILIKOM REGISTRACIJE: ', error);
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
}