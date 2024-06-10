import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';
import { FormGroup, Validators } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { ResponseMessage } from '../model/responseMessage.model';

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


  constructor(private userService: UserService, private fb: FormBuilder, private router: Router, private auth: AuthService) { }

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
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
          this.messagePassword = response.responseMessage;
          console.log('Password changed successfully:', response);
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
        //this.findUserByEmail();
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
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
  
}
