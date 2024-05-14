import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/user.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls:[ './navbar.component.css']
})
export class NavbarComponent implements OnInit {
  user!: User;
  
  constructor(
    private router: Router) {
  }

  ngOnInit():void{
  }
    
  redirectToLogin() {
    this.router.navigate(['/login']);
  }

  logout(){
    //this.userStateService.clearLoggedInUser();
    this.router.navigate(['/']);
  }

  registration() {
    this.router.navigate(['/registration']);
  }

  administratorProfile() {
    this.router.navigate(['/administratorProfile']);
  }

  allClients() {
    this.router.navigate(['/allClients']);
  }

  allEmployees() {
    this.router.navigate(['/allEmployees']);
  }

  registrationRequests() {
    this.router.navigate(['/registrationRequests']);
  }

}
