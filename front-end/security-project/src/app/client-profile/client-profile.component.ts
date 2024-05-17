import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../model/user.model';
import { Router } from '@angular/router';
import { UserRole } from '../model/userRole.model';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-client-profile',
  templateUrl: './client-profile.component.html',
  styleUrls: ['./client-profile.component.css']
})
export class ClientProfileComponent implements OnInit{
  user!: User;

  constructor(private userService: UserService, private router: Router, private auth: AuthService) { }

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
    this.userService.findUserByEmail().subscribe(
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
