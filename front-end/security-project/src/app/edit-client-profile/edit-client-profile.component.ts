import { Component, OnInit } from '@angular/core';
import { User } from '../model/user.model';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-edit-client-profile',
  templateUrl: './edit-client-profile.component.html',
  styleUrls: ['./edit-client-profile.component.css']
})
export class EditClientProfileComponent implements OnInit{
  user!: User;

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
      this.findUserByEmail();
    }
    
  }

  findUserByEmail(): void{
    this.activatedRoute.paramMap.subscribe(params => {
      const email = params.get('email');
      console.log("Email:" + email);
      if (email) {
        this.userService.findUserByEmail().subscribe(
          (user: User) => {
            console.log(user);
            this.user = user;
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
        this.router.navigate(['/client-profile']);
      },
      (error) => {
        console.error('Error updating user:', error);
      }
    );
  }
  
  
}
