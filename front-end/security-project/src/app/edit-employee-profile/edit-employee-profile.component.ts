import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-edit-employee-profile',
  templateUrl: './edit-employee-profile.component.html',
  styleUrls: ['./edit-employee-profile.component.css']
})
export class EditEmployeeProfileComponent implements OnInit{
  user!: User;
  loggedUser!: User;

  constructor ( private activatedRoute : ActivatedRoute, private userService : UserService, private router : Router, private auth: AuthService) {}

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
    this.activatedRoute.paramMap.subscribe(params => {
      const email = params.get('email');
      console.log("Email:" + email);
      if (email) {
        this.userService.findUserByEmail(this.loggedUser.email).subscribe(
          (user: User) => {
            console.log(user);
            this.user = user; // Postavljanje vrednosti korisnika za uređivanje
          },
          (error) => {
            console.error('Error fetching user:', error);
          }
        );
      }
    });
  }

  onSubmit(): void {
    this.userService.updateClient(this.user).subscribe(
      (response: any) => {
        console.log('User updated successfully:', response);
        // Možete dodati poruku ili preusmeriti korisnika na drugu stranicu
        this.router.navigate(['/employee-profile']);
      },
      (error) => {
        console.error('Error updating user:', error);
        // Možete prikazati odgovarajuću poruku o grešci
      }
    );
  }
  
  
}
