import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Permission } from 'src/app/model/permission.model';
import { UserRole } from 'src/app/model/userRole.model';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-permissionsManipulation',
  templateUrl: './permissionsManipulation.component.html',
  styleUrls: ['./permissionsManipulation.component.css']
})
export class PermissionsManipulationComponent implements OnInit {

  constructor(private userService: UserService, 
    private auth: AuthService, 
    private router: Router) { }

  roles: UserRole[] | undefined;
  permissionsForRole: Permission[] | undefined;
  selectedRole: UserRole | null = null;

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);

    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    } else if (!userRoles?.includes(UserRole.ADMINISTRATOR)) {
      this.router.navigate(['/homepage']);
    } else {
      this.loadAllRoles();
    }
  }

  loadAllRoles() {
    this.userService.getAllRoles().subscribe(
      (data: any) => {
        this.roles = data;
        console.log(this.roles);
      },
      error => {
        console.error('Error fetching roles:', error);
      }
    );
  }

  togglePermissionsForRole(role: UserRole) {
    if (this.selectedRole === role) {
      this.selectedRole = null;
    } else {
      this.selectedRole = role;
      this.getAllPermissionsForRole(role);
    }
  }

  getAllPermissionsForRole(role: UserRole) {
    this.userService.getAllPermissionsForRole(role).subscribe(
      (data: any) => {
        this.permissionsForRole = data;
        console.log(this.permissionsForRole);
      },
      error => {
        console.error('Error fetching permissions:', error);
      }
    );
  }

}
