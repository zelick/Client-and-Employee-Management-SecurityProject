import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';
import { FormGroup, Validators } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { ResponseMessage } from '../model/responseMessage.model';
import { Location } from '@angular/common';

@Component({
  selector: 'app-client-profile',
  templateUrl: './client-profile.component.html',
  styleUrls: ['./client-profile.component.css']
})
export class ClientProfileComponent implements OnInit{
  user!: User;
  loggedUser?: User;
  changePasswordFlag: boolean = false;
  passwordForm: FormGroup = new FormGroup({});
  messagePassword: string = "";
  email: any;
  goldClient: boolean = false;
  messageDeleteData: string = "";
  client: boolean = false;


  constructor(private userService: UserService, private cdr: ChangeDetectorRef, private fb: FormBuilder, private router: Router, private auth: AuthService, private location: Location) { }

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
    if (userRoles.includes(UserRole.CLIENT))
      {
        this.client = true;
      }
    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    }
    else if (!userRoles.includes(UserRole.CLIENT) && !userRoles.includes(UserRole.EMPLOYEE)) {
      this.router.navigate(['/homepage']); 
    }
    else {
      //this.findUserByEmail();
      this.getLoggedInUser();
    }
    this.passwordForm = this.fb.group({
      oldPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  changePasswordToggle(): void{
    this.changePasswordFlag = true;
  }

  changePassword(): void {
    if (this.passwordForm.valid) {
      const passwordData = {
        oldPassword: this.passwordForm.value.oldPassword,
        newPassword: this.passwordForm.value.newPassword,
        confirmPassword: this.passwordForm.value.confirmPassword,
        email: this.email, 
      };
      this.userService.changePassword(passwordData).subscribe(
        (response: ResponseMessage) => {
          if (response.responseMessage === "You have not entered a good current password."){
            this.messagePassword = response.responseMessage;
            return;
          }
          if (response.responseMessage === "The new password and confirm password do not match."){
            this.messagePassword = response.responseMessage;
            return;
          }
          if (response.responseMessage === "The password does not meet the requirements."){
            this.messagePassword = response.responseMessage;
            return;
          }
          this.messageDeleteData = "You have successfully change your password.";
          this.router.navigate(['/']);
        },
        (error) => {
          console.error('Error changing password:', error);
        }
      );
    } else {
    }
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;
        this.email = this.loggedUser.email;
        const userRoles = this.auth.getLoggedInUserRoles();
        if (this.loggedUser.servicesPackage === 'GOLDEN' && userRoles.includes(UserRole.CLIENT)) {
          this.goldClient = true;
        }
        //this.findUserByEmail();
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  deleteAllData(): void {
    if (confirm("Are you sure you want to delete all your data?")) {
      this.userService.deleteUserData(this.email).subscribe(
        (response: ResponseMessage) => {
          console.log('User data deleted successfully:', response);
          this.messageDeleteData = response.responseMessage;
          this.cdr.detectChanges();
          this.refreshPage();
        },
        (error) => {
          console.error('Failed to delete user data:', error);
          this.messageDeleteData = "Failed to delete user data.";
        }
      );
    }
  }

  findUserByEmail(): void{
    if (this.loggedUser)
    this.userService.findUserByEmail(this.loggedUser.email).subscribe(
      (user: User) => {
        this.user = user;
      },
      (error) => {
        console.error('Error fetching user:', error);
      }
    );
  }

  editProfile(email: string) {
    this.router.navigate(['/edit-client-profile/' + email]);
  }

  private refreshPage() {
    this.location.go(this.location.path()); 
    window.location.reload(); 
  }
  
}
