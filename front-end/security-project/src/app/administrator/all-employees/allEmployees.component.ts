import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserRole } from 'src/app/model/userRole.model';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-allEmployees',
  templateUrl: './allEmployees.component.html',
  styleUrls: ['./allEmployees.component.css']
})
export class AllEmployeesComponent implements OnInit {
  employees: any[] = [];

  constructor(private userService: UserService, 
    private auth: AuthService, 
    private router: Router) { }

  ngOnInit(): void {
    const userRoles = this.auth.getLoggedInUserRoles(); 
    console.log(userRoles);

    if (userRoles.length === 0) {
      this.router.navigate(['/']);
    } else if (!userRoles?.includes(UserRole.ADMINISTRATOR)) {
      this.router.navigate(['/homepage']);
    } else {
      this.loadEmployees();
    }
  }

  loadEmployees() {
    this.userService.getAllEmployees().subscribe(
      (data: any) => {
        this.employees = data;
        console.log(this.employees);
      },
      error => {
        console.error('Error fetching employees:', error);
      }
    );
  }
}
