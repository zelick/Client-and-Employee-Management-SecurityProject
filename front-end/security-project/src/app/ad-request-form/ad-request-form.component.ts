import { Component, OnInit } from '@angular/core';
import { AdRequest } from '../model/adRequest.model';
import { UserService } from '../services/user.service';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { User } from '../model/user.model';

@Component({
  selector: 'app-ad-request-form',
  templateUrl: './ad-request-form.component.html',
  styleUrls: ['./ad-request-form.component.css']
})
export class AdRequestFormComponent implements OnInit{
  loggedUser!: User;

  adRequest: AdRequest = {
    email: '',
    deadline: new Date(),
    activeFrom: new Date(),
    activeTo: new Date(),
    description: ''
  };

  constructor(private userService: UserService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
      const userRole = this.auth.getLoggedInUserRole();
      console.log(userRole);
      if (userRole !== "CLIENT") {
        this.router.navigate(['/']);
      }else{
        this.getLoggedInUser();
      }
    }

    
  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;
        console.log("User:" + this.loggedUser);
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.loggedUser.role);
        localStorage.setItem('loggedUserRole', this.loggedUser.role);
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  onSubmit() {
    if (this.loggedUser) {
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