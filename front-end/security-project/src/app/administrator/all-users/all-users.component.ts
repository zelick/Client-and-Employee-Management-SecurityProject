import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { UserRole } from 'src/app/model/userRole.model';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/services/user.service';
import { Location } from '@angular/common';

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
              private cdr: ChangeDetectorRef,
              private location: Location
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
        this.cdr.detectChanges();
        this.refreshPage();
      },
      error => {
        console.error('Error blocking user:', error);
      }
    );
  }

  unblockUser(email: string) {
    this.userService.unblockUser(email).subscribe(
      (response: any) => {
        console.log('User unblocked successfully:', response);
        this.cdr.detectChanges();
        this.refreshPage();
      },
      error => {
        console.error('Error blocking user:', error);
      }
    );
  }

  private refreshPage() {
    this.location.go(this.location.path()); 
    window.location.reload();
  }
}
