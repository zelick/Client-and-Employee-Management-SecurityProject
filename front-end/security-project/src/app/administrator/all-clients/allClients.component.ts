import { Component, OnInit } from '@angular/core';
import { Route, Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-allClients',
  templateUrl: './allClients.component.html',
  styleUrls: ['./allClients.component.css']
})
export class AllClientsComponent implements OnInit {
  clients: any[] = [];

  constructor(private userService: UserService,
              private auth: AuthService,
              private router: Router
  ) { }

  ngOnInit(): void {
    const userRole = this.auth.getLoggedInUserRole(); 
    console.log(userRole);
    if (userRole !== "ADMINISTRATOR") {
      this.router.navigate(['/']);
    } else {
      this.loadClients();
    }
  }

  loadClients() {
    this.userService.getAllClients().subscribe(
      (data: any) => {
        this.clients = data;
        console.log(this.clients);
      },
      error => {
        console.error('Error fetching clients:', error);
      }
    );
  }
}
