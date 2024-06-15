import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';
import * as DOMPurify from 'dompurify';

@Component({
  selector: 'app-edit-client-profile',
  templateUrl: './edit-client-profile.component.html',
  styleUrls: ['./edit-client-profile.component.css']
})
export class EditClientProfileComponent implements OnInit{
  user?: User;
  loggedUser?: User;
  clientFlag: boolean = false;

  constructor ( private activatedRoute : ActivatedRoute, private userService : UserService, private router : Router, private auth: AuthService) {}

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
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;

        if(this.loggedUser.roles.includes(UserRole.CLIENT))
          {
            this.clientFlag = true;
          }

        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.loggedUser.roles);
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  findUserByEmail(): void{
    this.activatedRoute.paramMap.subscribe(params => {
      const email = params.get('email');
      console.log("Email:" + email);
      if (email) {
        if (this.loggedUser)
        this.userService.findUserByEmail(this.loggedUser.email).subscribe(
          (user: User) => {
            console.log(user);
            this.loggedUser = user;
          },
          (error) => {
            console.error('Error fetching user:', error);
          }
        );
      }
    });
  }

  onSubmit(): void {
    // Primena DOMPurify na sve propertije objekta loggedUser pre slanja
    if (this.loggedUser) {
      this.loggedUser.email = DOMPurify.sanitize(this.loggedUser.email);
      this.loggedUser.surname = DOMPurify.sanitize(this.loggedUser.surname);
      this.loggedUser.name = DOMPurify.sanitize(this.loggedUser.name);
      this.loggedUser.address = DOMPurify.sanitize(this.loggedUser.address);
      this.loggedUser.city = DOMPurify.sanitize(this.loggedUser.city);
      this.loggedUser.country = DOMPurify.sanitize(this.loggedUser.country);
      this.loggedUser.phoneNumber = DOMPurify.sanitize(this.loggedUser.phoneNumber);

      this.userService.updateClient(this.loggedUser).subscribe(
        (response: any) => {
          console.log('User updated successfully:', response);
          this.router.navigate(['/client-profile']);
        },
        (error) => {
          console.error('Error updating user:', error);
        }
      );
    }
  } 
}
