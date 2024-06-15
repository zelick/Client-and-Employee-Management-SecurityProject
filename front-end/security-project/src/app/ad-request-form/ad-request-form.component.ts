import { Component, OnInit} from '@angular/core';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import * as DOMPurify from 'dompurify';

@Component({
  selector: 'app-ad-request-form',
  templateUrl: './ad-request-form.component.html',
  styleUrls: ['./ad-request-form.component.css']
})
export class AdRequestFormComponent implements OnInit{
  loggedUser!: User;
  message: String = '';
  today: string = new Date().toISOString().split('T')[0];

  adRequest: AdRequest = {
    email: '',
    deadline: new Date(),
    activeFrom: new Date(),
    activeTo: new Date(),
    description: ''
  };

  constructor(private userService: UserService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    }
    else if (!userRoles.includes(UserRole.CLIENT)) {
      this.router.navigate(['/homepage']); 
    }
    else {
      this.getLoggedInUser();
    }
  }
 
  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.loggedUser.roles);
        localStorage.setItem('loggedUserRole', this.loggedUser.roles.join(','));
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  onSubmit() {
    if (this.loggedUser) {
      const today = new Date();
      console.log("Today: " + today);
      if (this.adRequest.activeFrom >= this.adRequest.activeTo) {
        this.message = 'Active from must be before active to date.';
        return;
      }
      this.adRequest.description = DOMPurify.sanitize(this.adRequest.description);

      this.adRequest.email = this.loggedUser.email;
      this.userService.createAdRequest(this.adRequest).subscribe(
        (response) => {
          console.log('Ad request created successfully');
          this.router.navigate(['/client-ads']);
        },
        (error) => {
          console.error('Error creating ad request:', error);
        }
      );
    } else {
      console.error('Logged user is not defined');
    }
  }
}