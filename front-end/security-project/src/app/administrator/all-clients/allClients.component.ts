import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-allClients',
  templateUrl: './allClients.component.html',
  styleUrls: ['./allClients.component.css']
})
export class AllClientsComponent implements OnInit {
  clients: any[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.loadClients();
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
