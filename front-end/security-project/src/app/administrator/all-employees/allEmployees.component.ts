import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-allEmployees',
  templateUrl: './allEmployees.component.html',
  styleUrls: ['./allEmployees.component.css']
})
export class AllEmployeesComponent implements OnInit {
  employees: any[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadEmployees();
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
