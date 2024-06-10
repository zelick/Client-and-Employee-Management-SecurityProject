import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { UserRole } from 'src/app/model/userRole.model';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-all-users',
  templateUrl: './all-users.component.html',
  styleUrls: ['./all-users.component.css']
})
export class AllUsersComponent implements OnInit {
  users: any[] = [];

  constructor(private userService: UserService,
              private auth: AuthService,
              private router: Router,
              private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);
    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    } else if (!userRoles.includes(UserRole.ADMINISTRATOR)) {
      this.router.navigate(['/homepage']); 
    } else {
      this.loadUsers();
    }
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe(
      (data: any) => {
        this.users = data;
        console.log(this.users);
      },
      error => {
        console.error('Error fetching users:', error);
      }
    );
  }

  blockUser(email: string) {
    this.userService.blockUser(email).subscribe(
      (response: any) => {
        console.log('User blocked successfully:', response);
        // Remove blocked user from the users array
        this.users = this.users.filter(user => user.email !== email);
        // Ručno pokrenuti promjene detekcije Angulara
        this.cdr.detectChanges();
      },
      error => {
        console.error('Error blocking user:', error);
      }
    );
  }
}
