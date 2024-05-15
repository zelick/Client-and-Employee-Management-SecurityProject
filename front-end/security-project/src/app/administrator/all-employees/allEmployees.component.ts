import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
      const userRole = this.auth.getLoggedInUserRole(); // Poziv metode da dobijete stvarnu vrednost uloge
      console.log(userRole);
      if (userRole !== "ADMINISTRATOR") {
        this.router.navigate(['/']);
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
