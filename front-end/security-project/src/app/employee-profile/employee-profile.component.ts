import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-employee-profile',
  templateUrl: './employee-profile.component.html',
  styleUrls: ['./employee-profile.component.css']
})
export class EmployeeProfileComponent implements OnInit{
  user!: User;
  loggedUser!: User;

  constructor(private userService: UserService, private router: Router, private auth: AuthService) { }

  ngOnInit(): void {
    const userRole = this.auth.getLoggedInUserRole(); 
    console.log(userRole);
    if (userRole !== "EMPLOYEE") {
      this.router.navigate(['/']);
    } else {
      this.getLoggedInUser();
    }
  }

  getLoggedInUser() {
    this.userService.getLoggedInUser().subscribe(
      (user: User) => {
        console.log("Uspesno dobavio ulogovanog usera: ", user);
        this.loggedUser = user;
        console.log('ROLA ULOGOVANOG KORISNIKA: ' + this.loggedUser.role);
        localStorage.setItem('loggedUserRole', this.loggedUser.role);
        this.findUserByEmail();
      },
      (error) => {
        console.error('Error dobavljanja ulogovanog usera:', error);
      }
    );
  }

  findUserByEmail(): void{
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
    this.router.navigate(['/edit-employee-profile/' + email]);
  }
  
}
